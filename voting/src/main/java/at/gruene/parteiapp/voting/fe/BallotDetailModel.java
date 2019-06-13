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
package at.gruene.parteiapp.voting.fe;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.deltaspike.core.api.scope.ViewAccessScoped;
import org.apache.deltaspike.jpa.api.transaction.Transactional;
import org.apache.deltaspike.jsf.api.message.JsfMessage;

import at.gruene.parteiapp.voting.be.BallotService;
import at.gruene.parteiapp.voting.be.entities.Ballot;
import at.gruene.parteiapp.voting.be.entities.BallotNominee;
import at.gruene.parteiapp.voting.be.entities.BallotUser;
import at.gruene.parteiapp.voting.fe.msg.BallotMessage;
import at.gruene.platform.idm.api.GruenPrincipal;

/**
 * Backing bean for the ballot detail.
 *
 * @author <a href="mailto:struberg@apache.org">Mark Struberg</a>
 */
@Named("ballotDetail")
@ViewAccessScoped
public class BallotDetailModel implements Serializable {
    private Integer ballotId;
    private Ballot ballot;
    private boolean isAdmin;
    private boolean isCounter;
    private boolean isEditBallot;

    private List<BallotUser> ballotUsers;
    private BallotUser editedUser;

    private List<BallotNominee> ballotNominees;
    private BallotNominee editedNominee;


    private @Inject GruenPrincipal principal;
    private @Inject BallotService ballotService;

    private @Inject JsfMessage<BallotMessage> ballotMsg;

    public Integer getBallotId() {
        return ballotId;
    }

    public void setBallotId(Integer ballotId) {
        this.ballotId = ballotId;
    }

    public Ballot getBallot() {
        return ballot;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public boolean isCounter() {
        return isCounter;
    }

    public boolean isEditBallot() {
        return isEditBallot;
    }

    public List<BallotUser> getBallotUsers() {
        return ballotUsers;
    }

    public BallotUser getEditedUser() {
        return editedUser;
    }

    public List<BallotNominee> getBallotNominees() {
        return ballotNominees;
    }

    public BallotNominee getEditedNominee() {
        return editedNominee;
    }

    /**
     * Initialisation which gets called via f:viewAction
     */
    @Transactional
    public String initBallot() {
        //X TODO security:

        if (ballotId == null) {
            ballotMsg.addError().noBallotIdGiven();
            return null;
        }

        if (ballotId == -1) {
            // create new Ballot
            ballot = new Ballot();
            ballot.setHeldAt(LocalDate.now());

            isAdmin = true;
            isCounter=false;
            isEditBallot = true;

            ballotUsers = new ArrayList<>();
            editedUser = null;
            ballotNominees = new ArrayList<>();
            editedNominee = null;

            return null;
        }

        ballot = ballotService.loadBallot(ballotId);
        if (ballot == null) {
            ballotMsg.addError().noBallotFound(ballotId);
            return null;
        }
        // Lists we get from JPA are unmodifiable.
        // So we need to copy this over to be able to add a new user
        ballotNominees = new ArrayList<>(ballotService.getBallotNominees(ballot));
        ballotUsers = new ArrayList<>(ballotService.getBallotUsers(ballot));

        Optional<BallotUser> ballotUser = ballotUsers.stream()
                .filter(b -> b.getUserId().equals(principal.getName()))
                .findFirst();
        isAdmin = ballotUser.map(b -> b.isAdmin())
                    .orElse(false);
        isCounter = ballotUser.map(b -> b.isCounter())
                    .orElse(false);

        // start in read mode
        isEditBallot = false;
        editedUser = null;

        // stay on the page
        return null;
    }

    /* ballot edit */

    public String doEdit() {
        isEditBallot = true;
        return null;
    }

    public String doCancel() {
        initBallot();
        return null;
    }

    public String doSave() {
        if (ballot.getId() == null) {
            ballot = ballotService.createBallot(ballot.getHeldAt(), ballot.getName(), null);
            this.ballotId = ballot.getId();

            return "/ballotDetail.xhtml?ballotId=" + ballot.getId() + "&faces-redirect=true";
        }
        else {
            ballotService.updateBallot(ballot);
        }
        isEditBallot = false;
        return null;
    }

    public String doAddUser() {
        this.editedUser = new BallotUser();
        this.editedUser.setBallot(ballot);
        ballotUsers.add(this.editedUser);
        return null;
    }

    /* user edit */
    public String doEditUser(BallotUser u) {
        this.editedUser = u;
        return null;
    }

    public String doCancelEditUser() {
        if (this.editedUser.getId() == null) {
            // cancel adding a user
            this.ballotUsers.remove(this.editedUser);
        }
        this.editedUser = null;

        // simply load everything from scratch
        initBallot();

        return null;
    }

    public String doSaveUser() {
        ballotService.saveBallotUser(editedUser);
        this.editedUser = null;
        return null;
    }

    public String doAddNominee() {
        this.editedNominee = new BallotNominee();
        this.editedNominee.setBallot(ballot);
        ballotNominees.add(this.editedNominee);
        return null;
    }

    /* nominee edit */
    public String doEditNominee(BallotNominee ballotNominee) {
        this.editedNominee = ballotNominee;
        return null;
    }

    public String doDeleteNominee(BallotNominee ballotNominee) {
        this.ballotNominees.remove(ballotNominee);
        ballotService.removeBallotNominee(ballotNominee);
        return null;
    }

    public String doCancelEditNominee() {
        if (this.editedNominee.getId() == null) {
            // cancel while adding a nominee
            this.ballotNominees.remove(this.editedNominee);
        }
        this.editedNominee = null;

        // simply load everything from scratch
        initBallot();

        return null;
    }

    public String doSaveNominee() {
        ballotService.saveBallotNominee(editedNominee);
        this.editedNominee = null;
        return null;
    }

    /* Wahlzettel eingeben */
    public String doStartCounting() {
        ballot.setStatus(Ballot.BallotStatus.OPEN);
        ballot = ballotService.updateBallot(ballot);
        return null;
    }

    public String doEndCounting() {
        ballot.setStatus(Ballot.BallotStatus.CLOSED);
        ballot = ballotService.updateBallot(ballot);
        return null;
    }

    public String doEnterVotes() {
        return "ballotVote.xhtml?voteId=-1&ballotId=" + ballot.getId() + "&faces-redirect=true";
    }

    public String doViewVotes() {
        return "ballotVoteList.xhtml?ballotId=" + ballot.getId() + "&faces-redirect=true";
    }
}