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
package at.gruene.platform.idm.be;

import java.util.Collections;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Typed;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gruene.platform.idm.api.GruenPrincipal;

/**
 * keeps the current GruenPrincipal per thread
 *
 * @author <a href="mailto:struberg@apache.org">Mark Struberg</a>
 */
@ApplicationScoped
public class PrincipalProducer {
    private static final Logger log = LoggerFactory.getLogger(PrincipalProducer.class);

    public static final GruenPrincipal ANONYMOUS;

    static {
        ANONYMOUS = new GruenPrincipal("anonym", "Anonym", Collections.EMPTY_LIST, Collections.EMPTY_LIST);
    }


    private ThreadLocal<GruenPrincipal> currentPrincipal = new ThreadLocal<>();

    public void setPrincipal(GruenPrincipal benutzer) {
        this.currentPrincipal.set(benutzer);
    }

    public void clearPrincipal() {
        this.currentPrincipal.set(null);
        this.currentPrincipal.remove();
    }

    @Produces
    @RequestScoped
    @Typed({GruenPrincipal.class})
    @Named("currentUser")
    public GruenPrincipal createPrincipal() {
        GruenPrincipal usr = this.currentPrincipal.get();
        if (usr == null) {
            log.info("This Thread uses the Anonymous user!");
            return ANONYMOUS;
        } else {
            return usr;
        }
    }


}
