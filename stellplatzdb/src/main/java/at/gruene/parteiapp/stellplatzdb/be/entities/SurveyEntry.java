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
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import at.gruene.parteiapp.platform.be.entities.AuditedEntity;
import at.gruene.parteiapp.platform.be.entities.ColumnLength;
import at.gruene.parteiapp.platform.be.entities.VersionedEntity;

/**
 * A single entry in our {@link Survey}.
 *
 * @author <a href="mailto:struberg@apache.org">Mark Struberg</a>
 */
@Entity
@Table(name = "SURVEY_ENTRY")
public class SurveyEntry extends AuditedEntity implements VersionedEntity {
    @Id
    @GeneratedValue
    @Column(name="ID")
    private Integer id;

    @Version
    @Column(name="OPTLOCK")
    private Integer optLock;

    @ManyToOne(optional = false)
    @Column(name = "SURVEY_ID", nullable = false)
    private Survey survey;

    /* section about the user who enters the survey */

    @Column(name = "EMAIL", length = ColumnLength.EMAIL)
    private String email;

    @NotNull
    @Column(name = "BIC", nullable = false)
    private Integer bic;

    @NotNull
    @Column(name = "CITY", length = ColumnLength.MEDIUM_TEXT, nullable = false)
    private String city;

    @NotNull
    @Column(name = "STREET", length = ColumnLength.LONG_TEXT, nullable = false)
    private String street;

    @NotNull
    @Column(name = "HOUSENR", length = ColumnLength.SHORT_TEXT, nullable = false)
    private String houseNr;

    /**
     * How many flats are there in this house.
     */
    @Column(name = "SUM_HOUSING_UNITS")
    private Integer totalHousingUnits;

    /**
     * At which day the data got collected.
     */
    @NotNull
    @Column(name = "COUNTED_AT_DATE", nullable = false)
    private LocalDate countedAt;

    @NotNull
    @Column(name = "COUNTED_AT_TIME", nullable = false)
    private Integer countedAtTime;

    @Column(name = "REMOTE_IP", length = ColumnLength.MEDIUM_TEXT)
    private String remoteIp;

    /* section about the parking lots inside the house and in the courtyard */

    /**
     * How many parking lots are total inside the house.
     * This includes all the free and the reserved ones.
     * E.g. an underground parking area
     */
    @Column(name = "SUM_INSIDE")
    private Integer totalNrInside;

    /**
     * How many inside parking lots are used by residential cars.
     * These are cars which are registered in the current city or area.
     */
    @Column(name = "USED_LOCAL_INSIDE")
    private Integer usedLocalsInside;

    /**
     * How many inside parking lots are used by non-residential cars.
     * These are cars which are not registered in the current city or area
     * but in some abroad region.
     */
    @Column(name = "USED_ABROAD_INSIDE")
    private Integer usedAbroadInside;

    /**
     * How many parking lots are total in the courtyard of the house.
     * This includes all the free and the reserved ones.
     * E.g. an underground parking area
     */
    @Column(name = "SUM_COURTYARD")
    private Integer totalNrCourtyard;

    /**
     * How many outside parking lots are used by residential cars.
     * These are cars which are registered in the current city or area.
     */
    @Column(name = "USED_LOCAL_COURTYARD")
    private Integer usedLocalsCourtyard;

    /**
     * How many outside parking lots are used by non-residential cars.
     * These are cars which are not registered in the current city or area
     * but in some abroad region.
     */
    @Column(name = "USED_ABROAD_COURTYARD")
    private Integer usedAbroadCourtyard;

    /**
     * An optional comment by the user who enters the survey
     */
    @Column(name = "USER_COMMENT", length = ColumnLength.LARGE_TEXT)
    private String userComment;

    @NotNull
    @Column(name = "STATUS", nullable = false)
    private SurveyEntryStatus status;

    /**
     * Additional text which can be entered by the person who verifies that entry.
     */
    @Column(name = "STATUS_TEXT", length = ColumnLength.LARGE_TEXT)
    private String statusText;

    /**
     * When did the entry got verified;
     * If this field is NULL then this data may or may not be correct.
     */
    @Column(name = "VERIFIED_AT")
    private LocalDate verifiedAt;

    /**
     * UserId of the person who verified that entry.
     * @see #verifiedAt
     */
    @Column(name = "VERIFIED_BY", length = ColumnLength.USERID)
    private LocalDate verifiedBy;

    /**
     * When the surveyEntry gets verified it will also get assigned
     * to an official building location.
     */
    @ManyToOne
    @JoinColumn(name = "VERIFIEDBUILDING_ID")
    private Building verifiedBuilding;



    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public Integer getOptLock() {
        return optLock;
    }

    public Survey getSurvey() {
        return survey;
    }

    public void setSurvey(Survey survey) {
        this.survey = survey;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getBic() {
        return bic;
    }

    public void setBic(Integer bic) {
        this.bic = bic;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getHouseNr() {
        return houseNr;
    }

    public void setHouseNr(String houseNr) {
        this.houseNr = houseNr;
    }

    public Integer getTotalHousingUnits() {
        return totalHousingUnits;
    }

    public void setTotalHousingUnits(Integer totalHousingUnits) {
        this.totalHousingUnits = totalHousingUnits;
    }

    public LocalDate getCountedAt() {
        return countedAt;
    }

    public void setCountedAt(LocalDate countedAt) {
        this.countedAt = countedAt;
    }

    public Integer getCountedAtTime() {
        return countedAtTime;
    }

    public void setCountedAtTime(Integer countedAtTime) {
        this.countedAtTime = countedAtTime;
    }

    public String getRemoteIp() {
        return remoteIp;
    }

    public void setRemoteIp(String remoteIp) {
        this.remoteIp = remoteIp;
    }

    public Integer getTotalNrInside() {
        return totalNrInside;
    }

    public void setTotalNrInside(Integer totalNrInside) {
        this.totalNrInside = totalNrInside;
    }

    public Integer getUsedLocalsInside() {
        return usedLocalsInside;
    }

    public void setUsedLocalsInside(Integer usedLocalsInside) {
        this.usedLocalsInside = usedLocalsInside;
    }

    public Integer getUsedAbroadInside() {
        return usedAbroadInside;
    }

    public void setUsedAbroadInside(Integer usedAbroadInside) {
        this.usedAbroadInside = usedAbroadInside;
    }

    public Integer getTotalNrCourtyard() {
        return totalNrCourtyard;
    }

    public void setTotalNrCourtyard(Integer totalNrCourtyard) {
        this.totalNrCourtyard = totalNrCourtyard;
    }

    public Integer getUsedLocalsCourtyard() {
        return usedLocalsCourtyard;
    }

    public void setUsedLocalsCourtyard(Integer usedLocalsCourtyard) {
        this.usedLocalsCourtyard = usedLocalsCourtyard;
    }

    public Integer getUsedAbroadCourtyard() {
        return usedAbroadCourtyard;
    }

    public void setUsedAbroadCourtyard(Integer usedAbroadCourtyard) {
        this.usedAbroadCourtyard = usedAbroadCourtyard;
    }

    public String getUserComment() {
        return userComment;
    }

    public void setUserComment(String userComment) {
        this.userComment = userComment;
    }

    public LocalDate getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(LocalDate verifiedAt) {
        this.verifiedAt = verifiedAt;
    }

    public LocalDate getVerifiedBy() {
        return verifiedBy;
    }

    public void setVerifiedBy(LocalDate verifiedBy) {
        this.verifiedBy = verifiedBy;
    }

    public Building getVerifiedBuilding() {
        return verifiedBuilding;
    }

    public void setVerifiedBuilding(Building verifiedBuilding) {
        this.verifiedBuilding = verifiedBuilding;
    }

    public SurveyEntryStatus getStatus() {
        return status;
    }

    public void setStatus(SurveyEntryStatus status) {
        this.status = status;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }
}
