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

package at.gruene.parteiapp.stellplatzdb.be.entities;

public enum SurveyEntryStatus {
    /**
     * New entry. Initial status
     */
    NEW,

    /**
     * Valid and verified entry.
     * This entry can be used for research
     */
    VERIFIED,

    /**
     * Invalid entry. E.g. spam or clearly wrong entry
     */
    REJECTED,

    /**
     * This entry needs more research.
     * Verification is not done.
     */
    RESEARCH
}
