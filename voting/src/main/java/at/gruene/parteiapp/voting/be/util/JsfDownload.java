/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
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
package at.gruene.parteiapp.voting.be.util;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.apache.deltaspike.core.util.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class to download a Stream to a browser from a JSF action.
 * @author <a href="mailto:struberg@apache.org">Mark Struberg</a>
 */
public class JsfDownload {

    private static final Logger log = LoggerFactory.getLogger(JsfDownload.class);


    public static void streamFileDownload(String content, String contentType, String fileName) {
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();

        ec.responseReset(); // to cleanup any previously set output
        ec.setResponseContentType(contentType);

        ec.setResponseHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\""); // The Save As popup magic is done here. You can give it any file name you want, this only won't work in MSIE, it will use current request URL as file name instead.

        OutputStream output = null;
        try {
            output = ec.getResponseOutputStream();
            output.write(content.getBytes(StandardCharsets.UTF_8));
            output.flush();
        }
        catch (IOException e) {
            ExceptionUtils.throwAsRuntimeException(e);
        }

        log.info("file downloaded {} size={} bytes", fileName, content.length());

        fc.responseComplete();
    }


}
