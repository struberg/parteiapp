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

import java.util.concurrent.Callable;

import javax.faces.convert.ConverterException;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TimeIntConverterTest {

    @Test
    public void testTimeToIntConversion() {
        TimeIntConverter converter = new TimeIntConverter();

        assertEquals(Integer.valueOf(1034), converter.getAsObject(null, null, "10:34"));
        assertEquals(Integer.valueOf(34), converter.getAsObject(null, null, "00:34"));
        assertEquals(Integer.valueOf(1000), converter.getAsObject(null, null, "10:00"));

        assertException(ConverterException.class, () -> converter.getAsObject(null, null, "bla"));
        assertException(ConverterException.class, () -> converter.getAsObject(null, null, ":00"));
        assertException(ConverterException.class, () -> converter.getAsObject(null, null, "34:00"));
        assertException(ConverterException.class, () -> converter.getAsObject(null, null, "14:80"));
        assertException(ConverterException.class, () -> converter.getAsObject(null, null, "1480"));
    }

    @Test
    public void testIntToTimeConversion() {
        TimeIntConverter converter = new TimeIntConverter();
        assertEquals("10:34", converter.getAsString(null, null, 1034));
        assertEquals("10:04", converter.getAsString(null, null, 1004));
        assertEquals("10:59", converter.getAsString(null, null, 1059));
        assertEquals("00:34", converter.getAsString(null, null, 34));
        assertEquals("14:00", converter.getAsString(null, null, 1400));
        assertEquals("04:00", converter.getAsString(null, null, 400));

        assertException(ConverterException.class, () -> converter.getAsString(null, null, -23));
        assertException(ConverterException.class, () -> converter.getAsString(null, null, 32300));
        assertException(ConverterException.class, () -> converter.getAsString(null, null, 2400));
    }

    private <T extends Exception> void assertException(Class<T> expectedException, Callable c) {
        try {
            c.call();
            fail("should fail!");
        }
        catch(Exception e) {
            assertTrue("Exception not of type " + expectedException.getName(), e.getClass().isAssignableFrom(expectedException));
        }
    }
}
