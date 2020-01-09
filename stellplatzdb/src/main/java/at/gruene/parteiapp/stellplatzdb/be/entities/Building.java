package at.gruene.parteiapp.stellplatzdb.be.entities;/*
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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import at.gruene.parteiapp.platform.be.entities.AuditedEntity;
import at.gruene.parteiapp.platform.be.entities.ColumnLength;
import at.gruene.parteiapp.platform.be.entities.VersionedEntity;

/**
 * A building with geo location data and other verified stats.
 * This is mostly for deduplication of user entries.
 * It also serves as anchor point for official data attached to a building.
 */
@Entity
@Table(name = "BUILDING")
public class Building extends AuditedEntity implements VersionedEntity {

    @Id
    @GeneratedValue
    @Column(name="ID")
    private Integer id;

    @Version
    @Column(name="OPTLOCK")
    private Integer optLock;


    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public Integer getOptLock() {
        return optLock;
    }

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
}
