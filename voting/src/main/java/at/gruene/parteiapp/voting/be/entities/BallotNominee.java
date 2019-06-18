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
package at.gruene.parteiapp.voting.be.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import at.gruene.parteiapp.platform.be.entities.VersionedEntity;

/**
 * A single Option on a vote
 * This can be a nominee name or an abstract option name.
 */
@Entity
@Table(name="BALLOT_NOMINEE")
@NamedQueries( {
        @NamedQuery(name = BallotNominee.QRY_FIND_BY_BALLOT,
                query = "select n from BallotNominee as n where n.ballot=:ballot")
})
public class BallotNominee extends AuditedEntity implements VersionedEntity {

    public final static String QRY_FIND_BY_BALLOT = "BallotNomineeFindByBallot";

    @Id
    @GeneratedValue
    @Column(name="ID")
    private Integer id;

    @Version
    @Column(name="OPTLOCK")
    private Integer optLock;


    @ManyToOne(optional = false)
    @JoinColumn(name="BALLOT_ID")
    private Ballot ballot;

    /**
     * name of the nominee
     */
    @Column(name = "NOMINEE_NAME", nullable = false)
    @NotNull
    private String name;

    /**
     * A short token for the nominee name.
     * This can be used for faster entry in the UI.
     * E.g. if the nominee name is 'Arnold Schoenberg' then 'as' might be taken as shortKey.
     */
    @Column(name = "SHORTKEY", length = 3)
    private String shortKey;


    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public Integer getOptLock() {
        return optLock;
    }

    public Ballot getBallot() {
        return ballot;
    }

    public void setBallot(Ballot ballot) {
        this.ballot = ballot;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortKey() {
        return shortKey;
    }

    public void setShortKey(String shortKey) {
        this.shortKey = shortKey;
    }
}
