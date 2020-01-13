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

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import at.gruene.parteiapp.platform.be.entities.AuditedEntity;
import at.gruene.parteiapp.platform.be.entities.ColumnLength;
import at.gruene.parteiapp.platform.be.entities.VersionedEntity;

/**
 * A single survey campaign.
 *
 * @author <a href="mailto:struberg@apache.org">Mark Struberg</a>
 */
@Entity
@Table(name = "SURVEY")
@NamedQuery(name = Survey.QRY_GET_ALL_ACTIVE,
            query = "select s from Survey AS s where s.active=true and s.openFrom <= :today and s.openUntil >= :today ORDER BY s.surveyName ASC")
public class Survey extends AuditedEntity implements VersionedEntity {

    public static final String QRY_GET_ALL_ACTIVE = "surveyGetAllActive";

    /**
     * The id must be unique and is a short token
     */
    @Id
    @Column(name="ID", length = ColumnLength.STRING_ID)
    private String id;

    @Version
    @Column(name="OPTLOCK")
    private Integer optLock;

    /**
     * A textual description of the servey. E.g. 'Autostellplaetze Favoriten 2020'
     */
    @NotNull
    @Column(name="SURVEY_NAME", length = ColumnLength.DESCRIPTION, nullable = false)
    private String surveyName;

    /**
     * Whether this survey is active
     */
    @Column(name="ACTIVE", nullable = false)
    private boolean active;

    /**
     * Start date from when the survey is open.
     * That means from when on data can get entered.
     */
    @NotNull
    @Column(name="OPEN_FROM", nullable = false)
    private LocalDate openFrom;

    /**
     * Start date till when the survey is open.
     * This is the last day data can get entered.
     */
    @NotNull
    @Column(name="OPEN_UNTIL", nullable = false)
    private LocalDate openUntil;

    /**
     * The default BIC/PLZ to use for each {@link SurveyEntry} of that survey.
     */
    @Column(name = "DEFAULT_BIC")
    private Integer defaultBic;

    /**
     * The default City name to use for each {@link SurveyEntry} of that survey.
     */
    @Column(name = "DEFAULT_CITY", length = ColumnLength.MEDIUM_TEXT)
    private String defaultCity;

    /**
     * A detailed description about the current survey.
     * This will be presented to the user when she enters a new {@link SurveyEntry}
     */
    @NotNull
    @Column(name="DESCRIPTION", length = ColumnLength.LARGE_TEXT, nullable = false)
    private String description;


    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public Integer getOptLock() {
        return optLock;
    }

    public String getSurveyName() {
        return surveyName;
    }

    public void setSurveyName(String surveyName) {
        this.surveyName = surveyName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDate getOpenFrom() {
        return openFrom;
    }

    public void setOpenFrom(LocalDate openFrom) {
        this.openFrom = openFrom;
    }

    public LocalDate getOpenUntil() {
        return openUntil;
    }

    public void setOpenUntil(LocalDate openUntil) {
        this.openUntil = openUntil;
    }

    public Integer getDefaultBic() {
        return defaultBic;
    }

    public void setDefaultBic(Integer defaultBic) {
        this.defaultBic = defaultBic;
    }

    public String getDefaultCity() {
        return defaultCity;
    }

    public void setDefaultCity(String defaultCity) {
        this.defaultCity = defaultCity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
