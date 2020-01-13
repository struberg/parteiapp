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
package at.gruene.parteiapp.stellplatzdb;

import java.time.LocalDate;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.deltaspike.jpa.api.transaction.TransactionHelper;
import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import at.gruene.parteiapp.stellplatzdb.be.SurveyEntryService;
import at.gruene.parteiapp.stellplatzdb.be.SurveyService;
import at.gruene.parteiapp.stellplatzdb.be.entities.Survey;
import at.gruene.parteiapp.stellplatzdb.be.entities.SurveyEntry;
import at.gruene.parteiapp.stellplatzdb.be.entities.SurveyEntryStatus;
import static org.junit.Assert.assertNotNull;

@RunWith(CdiTestRunner.class)
public class StellplatzDbTest {

    private static final String TEST_SURVEY_ID = "test_XXX";

    private @Inject SurveyService surveyService;
    private @Inject SurveyEntryService surveyEntryService;

    private @Inject EntityManager em;

    @Before
    public void cleanupDb() throws Exception {
        TransactionHelper.getInstance().executeTransactional(()-> {
            Survey testSurvey = surveyService.getById(TEST_SURVEY_ID);
            if (testSurvey != null) {
                Query qry = em.createQuery("DELETE from SurveyEntry AS e where e.survey = :survey");
                qry.setParameter("survey", testSurvey);
                qry.executeUpdate();
                surveyService.delete(testSurvey);
            }
            return null;
        });
    }

    @Test
    public void testNewEntry() {
        // first we create a new test survey
        Survey survey = new Survey();
        survey.setId(TEST_SURVEY_ID);
        survey.setDefaultBic(1010);
        survey.setDefaultCity("Wien");
        survey.setActive(true);
        LocalDate today = LocalDate.now();
        survey.setOpenFrom(today);
        survey.setOpenUntil(today.plusDays(30));
        survey.setSurveyName("Test survey");
        survey.setDescription("This is a test survey");

        survey = surveyService.save(survey);
        assertNotNull(survey);

        // and now for the SurveyEntry
        SurveyEntry surveyEntry = new SurveyEntry();
        surveyEntry.setSurvey(survey);
        surveyEntry.setStatus(SurveyEntryStatus.NEW);
        surveyEntry.setCountedAt(LocalDate.now());
        surveyEntry.setCountedAtTime(1000);
        surveyEntry.setBic(survey.getDefaultBic());
        surveyEntry.setCity(survey.getDefaultCity());

        surveyEntry.setHouseNr("666");
        surveyEntry.setStreet("Drowningstreet");
        surveyEntry.setTotalHousingUnits(22);
        surveyEntry.setUsedLocalsInside(10);
        surveyEntry.setTotalNrInside(20);

        surveyEntry = surveyEntryService.save(surveyEntry);
        assertNotNull(surveyEntry);
    }
}
