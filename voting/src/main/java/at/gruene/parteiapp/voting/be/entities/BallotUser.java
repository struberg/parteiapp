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
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import at.gruene.parteiapp.platform.be.CollumnLength;
import at.gruene.parteiapp.platform.be.entities.VersionedEntity;

/**
 *
 */
@Entity
@Table(name = "BALLOT_USER",
    uniqueConstraints = {
        @UniqueConstraint(name="UC_USER", columnNames = {"BALLOT_ID", "USERID"})
})
@NamedQuery(name = BallotUser.QRY_FIND_BY_BALLOT,
        query = "select u from BallotUser as u where u.ballot=:ballot")
public class BallotUser extends AuditedEntity implements VersionedEntity {

    public final static String QRY_FIND_BY_BALLOT = "BallotUserFindByBallot";

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
     * user id from LDAP.
     */
    @Column(name="USERID", length = CollumnLength.USERID, nullable = false)
    @NotNull
    private String userId;

    /**
     * user name from LDAP
     */
    private String name;

    /**
     * Whether this user is administrator.
     * And admin can add users and start and stop a poll
     */
    @Column(nullable = false)
    private boolean isAdmin;

    /**
     * Whether this user can enter polls
     */
    @Column(nullable = false)
    private boolean isCounter;

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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public boolean isCounter() {
        return isCounter;
    }

    public void setCounter(boolean counter) {
        isCounter = counter;
    }
}
