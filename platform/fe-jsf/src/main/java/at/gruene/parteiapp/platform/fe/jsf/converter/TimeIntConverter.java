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
package at.gruene.parteiapp.platform.fe.jsf.converter;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

/**
 * JSF Converter for type=&quot;time&quot; fields into a 4 digit Integer 'hhmm'.
 * E.g. 15:34 is Integer 1534.
 * Min value is 0000, maxvalue is 2359.
 */
@FacesConverter("parteiapp.TimeConverter")
public class TimeIntConverter implements Converter<Integer> {

    @Override
    public Integer getAsObject(FacesContext context, UIComponent component, String value) throws ConverterException {
        if (value == null || value.isEmpty()) {
            return null;
        }

        value = value.trim();
        if (value.length() == 5 && value.charAt(2) == ':') {
            try {
                Integer hour = Integer.valueOf(value.substring(0, 2));
                Integer minutes = Integer.valueOf(value.substring(3, 5));
                if (hour >= 0 && hour <= 23 && minutes >= 0 && minutes <= 59) {
                    return hour * 100 + minutes;
                }
            }
            catch (NumberFormatException nfe) {
                // will be handled later
            }
        }

        FacesMessage msg = new FacesMessage("Time Conversion error.",  "Time value must be between 00:00 and 23:59");
        msg.setSeverity(FacesMessage.SEVERITY_ERROR);
        throw new ConverterException(msg);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Integer value) throws ConverterException {
        if (value == null) {
            return null;
        }

        if (value < 0 || value > 2359) {
            FacesMessage msg = new FacesMessage("Time Conversion error.",  "Time value must be between 00:00 and 23:59, but is " + value);
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ConverterException(msg);
        }
        return String.format("%02d:%02d", value/100, value%100);
    }
}
