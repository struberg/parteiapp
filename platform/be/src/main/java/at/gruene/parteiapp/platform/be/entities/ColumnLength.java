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
package at.gruene.parteiapp.platform.be.entities;

/**
 * Common column lengh constants for database entities.
 *
 * @author <a href="mailto:struberg@apache.org">Mark Struberg</a>
 */
public interface ColumnLength {

    /**
     * Field length for userId fields.
     */
    int USERID = 70;

    /**
     * Field length for String based primary keys
     */
    int STRING_ID = 30;

    /**
     * Field length for standard description text.
     */
    int DESCRIPTION = 255;

    /**
     * Field length for email addresses;
     */
    int EMAIL = 255;

    int SHORT_TEXT = 30;
    int MEDIUM_TEXT = 100;
    int LONG_TEXT = 255;
    int LARGE_TEXT = 4000;
}
