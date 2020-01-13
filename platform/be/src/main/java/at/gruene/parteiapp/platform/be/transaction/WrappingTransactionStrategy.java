/*
 * Copyright Author and Authors. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package at.gruene.parteiapp.platform.be.transaction;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Priority;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;
import javax.interceptor.InvocationContext;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.deltaspike.jpa.impl.transaction.ResourceLocalTransactionStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Alternative
@Priority(1000)
@Dependent
public class WrappingTransactionStrategy extends ResourceLocalTransactionStrategy {

    private static final Logger logger = LoggerFactory.getLogger(WrappingTransactionStrategy.class);

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private static final String VIOLATION_MSG_SEPARATOR = " -> ";
    private static final String MSG_SLOW_SERVICE_DETECTED = "SLOW SERVICE DETECTED: time={} ms  parallel_invocations={}. max_parallel={}. method={}#{} params={}";
    private static final String PARAM_STRING = "{}";

    /**
     * Services should not take longer than this value in ms.
     */
    private static final long THRESHOLD = 500;

    private final AtomicInteger parallelInvocations = new AtomicInteger(0);

    /** Max amount of parallel invocations so far */
    private volatile int maxParallelInvocations = -1;

    @Override
    protected Exception prepareException(Exception e) {
        ConstraintViolationException cve = null;
        if (e instanceof ConstraintViolationException) {
            cve = (ConstraintViolationException) e;
        }
        if (cve == null) {
            // sometimes the CVE is wrapped in some JPA Exception...
            Throwable cause = e.getCause();
            if (cause != null && cause instanceof ConstraintViolationException) {
                cve = (ConstraintViolationException) cause;
            }
        }

        if (cve != null) {
            StringBuilder msg = new StringBuilder(e.getMessage());
            msg.append(LINE_SEPARATOR);
            if (cve.getConstraintViolations() != null) {
                for (ConstraintViolation cv : cve.getConstraintViolations()) {
                    msg.append(cv.getPropertyPath()).append(VIOLATION_MSG_SEPARATOR).append(cv.getMessage()).append(LINE_SEPARATOR);
                }
            }
            ConstraintViolationException newCve = new ConstraintViolationException(msg.toString(), cve.getConstraintViolations());
            newCve.setStackTrace(cve.getStackTrace());
            return newCve;
        }

        return e;
    }

    @Override
    public Object execute(InvocationContext invocationContext) throws Exception {
        long start = System.currentTimeMillis();
        int currentlyParallel = -1;

        try {
            currentlyParallel = parallelInvocations.incrementAndGet();
            if (currentlyParallel > maxParallelInvocations) {
                maxParallelInvocations = currentlyParallel;
            }

            return super.execute(invocationContext);
        }
        finally {
            long duration = System.currentTimeMillis() - start;
            if (duration > THRESHOLD) {
                logger.info(MSG_SLOW_SERVICE_DETECTED,
                        duration,
                        currentlyParallel,
                        maxParallelInvocations,
                        invocationContext.getTarget().getClass().getName(), // concrete class name
                        invocationContext.getMethod().getName(),
                        getParameterString(invocationContext.getParameters()));
            }
            parallelInvocations.decrementAndGet();
        }
    }

    /**
     * Returns a string representation of the method parameters
     *
     * @param params the array of method parameters (may be null)
     * @return the parameter string
     */
    private String getParameterString(Object[] params) {
        if (params == null) {
            return PARAM_STRING;
        }

        ArrayList<Object> filteredList = new ArrayList<>(params.length);
        for (Object parameter : params) {
            // avoid printing huge byte arrays to the logfile (e.g. when calling archiveService.import)
            if (parameter != null && !byte[].class.equals(parameter.getClass())) {
                filteredList.add(parameter);
            }
        }

        return new ToStringBuilder(filteredList, ToStringStyle.SIMPLE_STYLE).append(filteredList).toString();
    }
}
