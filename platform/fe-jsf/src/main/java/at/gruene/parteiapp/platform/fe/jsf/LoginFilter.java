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
package at.gruene.parteiapp.platform.fe.jsf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import at.gruene.platform.idm.api.GruenPrincipal;
import at.gruene.platform.idm.api.IdmService;
import at.gruene.platform.idm.be.PrincipalProducer;

/**
 * This {@link Filter} checks whether the invoked URL is to be
 * protected {@link LoginFilter#dropUrls}
 * steht.
 *
 * The DropUrls can be configured as init-param in web.xml.
 *
 * Beispiel:
 * <code>
 *   <filter>
 *       <filter-name>LoginFilter</filter-name>
 *       <filter-class>at.sozvers.zepta.core.felib.idm.LoginFilter</filter-class>
 *       <init-param>
 *           <param-name>dropurl.0</param-name>
 *           <param-value>/VAADIN/</param-value>
 *       </init-param>
 *       <init-param>
 *           <param-name>dropurl.1</param-name>
 *           <param-value>/clusterTest</param-value>
 *       </init-param>
 *   </filter>
 * </code>
 *
 * @author <a href="mailto:struberg@apache.org">Mark Struberg</a>
 */
public class LoginFilter implements Filter {
    /**
     * Those URLs do not need a login
     */
    private List<String> dropUrls = null;


    private @Inject PrincipalProducer principalProducer;
    private @Inject IdmService idmService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        dropUrls = new ArrayList<>();
        int i = 0;
        String dropUrlParam;
        while ((dropUrlParam = filterConfig.getInitParameter("dropurl." + i)) != null) {
            dropUrls.add(dropUrlParam);
            i++;
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest servletRequest = (HttpServletRequest) request;
        HttpServletResponse servletResponse = (HttpServletResponse) response;

        String loggedInUserId = servletRequest.getRemoteUser();

        if (loggedInUserId == null || loggedInUserId.isEmpty()) {
            // TODO redirect to login site and set backUrl cookie
            servletResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
            return;
        }

        GruenPrincipal principal = idmService.getUser(loggedInUserId);
        if (principal == null) {
            principal = PrincipalProducer.ANONYMOUS;
        }
        principalProducer.setPrincipal(principal);

        try {
            chain.doFilter(request, response);
        }
        finally {
            principalProducer.clearPrincipal();
        }
    }


    @Override
    public void destroy() {

    }

    private boolean isDroppedUrl(String url) {
        // does any stopUrl match url
        for (String stopUrl : dropUrls) {
            if (url.contains(stopUrl)) {
                return true;
            }
        }

        return false;
    }
}
