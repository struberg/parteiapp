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
package at.gruene.parteiapp.stellplatzdb.fe;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.deltaspike.core.api.scope.ViewAccessScoped;
import org.apache.deltaspike.jsf.api.message.JsfMessage;

import at.gruene.parteiapp.stellplatzdb.be.SurveyEntryService;
import at.gruene.parteiapp.stellplatzdb.be.SurveyService;
import at.gruene.parteiapp.stellplatzdb.be.entities.Survey;
import at.gruene.parteiapp.stellplatzdb.be.entities.SurveyEntry;
import at.gruene.parteiapp.stellplatzdb.fe.msg.SurveyMessage;

/**
 * @author <a href="mailto:struberg@apache.org">Mark Struberg</a>
 */
@ViewAccessScoped
@Named("newsurvey")
public class NewSurveyModel implements Serializable {

    private @Inject SurveyEntryService surveyEntryService;
    private @Inject SurveyService surveyService;

    private @Inject JsfMessage<SurveyMessage> surveyMsg;


    private String surveyId;

    private Survey survey;
    private SurveyEntry surveyEntry;

    private boolean surveyStored;

    public String getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(String surveyId) {
        this.surveyId = surveyId;
    }

    public String initSurvey() {
        if (surveyId == null || surveyId.isEmpty()) {
            // redirect to the surveyList
            return "surveyList.xhtml";
        }

        survey = surveyService.getById(surveyId);
        surveyEntry = new SurveyEntry();
        surveyEntry.setSurvey(survey);

        if (survey.getDefaultBic() != null) {
            surveyEntry.setBic(survey.getDefaultBic());
        }
        if (survey.getDefaultCity() != null) {
            surveyEntry.setCity(survey.getDefaultCity());
        }

        surveyEntry.setCountedAt(LocalDate.now());


        surveyStored = false;
        return null;
    }

    public Survey getSurvey() {
        return survey;
    }

    public SurveyEntry getSurveyEntry() {
        return surveyEntry;
    }

    public boolean isSurveyStored() {
        return surveyStored;
    }

    public String saveSurveyEntry() {
        if (!surveyStored) {
            if (surveyEntry.getTotalNrInside() == null && surveyEntry.getTotalNrCourtyard() == null) {
                surveyMsg.addError().atLeastOneNumberRequired();
            }
            surveyEntryService.save(surveyEntry);
        }

        surveyStored = true;
        return null;
    }

}
