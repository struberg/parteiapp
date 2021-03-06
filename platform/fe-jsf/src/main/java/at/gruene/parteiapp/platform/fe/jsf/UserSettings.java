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

import java.io.Serializable;
import java.util.Locale;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

import at.gruene.platform.idm.api.GruenPrincipal;

/**
 * @author <a href="mailto:struberg@apache.org">Mark Struberg</a>
 */
@Named("user")
@SessionScoped
public class UserSettings implements Serializable {

    private Locale locale = Locale.GERMAN;

    private @Inject HttpSession httpSession;

    private @Inject GruenPrincipal principal;

    public Locale getLocale() {
        return locale;
    }

    public String getId() {
        // attention, the 'name' in the Principal is it's id as per spec.
        return principal.getName();
    }

    public String getName() {
        return principal.getViewName();
    }

    public String logout() {
        httpSession.invalidate();
        return "index.html";
    }
}
