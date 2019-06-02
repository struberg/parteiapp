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
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.deltaspike.core.api.scope.ViewAccessScoped;
import org.apache.deltaspike.jsf.api.message.JsfMessage;

import at.gruene.parteiapp.voting.be.BallotService;
import at.gruene.parteiapp.voting.be.entities.Ballot;
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
    private boolean isEdit;
    private List<BallotUser> ballotUsers;

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

    public boolean isEdit() {
        return isEdit;
    }

    public List<BallotUser> getBallotUsers() {
        return ballotUsers;
    }

    public String loadBallot() {
        //X TODO security:

        if (ballotId == null) {
            ballotMsg.addError().noBallotIdGiven();
            return null;
        }

        ballot = ballotService.loadBallot(ballotId);
        if (ballot == null) {
            ballotMsg.addError().noBallotFound(ballotId);
            return null;
        }

        ballotUsers = ballotService.getBallotUser(ballot);
        Optional<BallotUser> ballotUser = ballotUsers.stream()
                .filter(b -> b.getUserId().equals(principal.getName()))
                .findFirst();
        isAdmin = ballotUser.map(b -> b.isAdmin())
                    .orElse(false);
        isCounter = ballotUser.map(b -> b.isCounter())
                    .orElse(false);

        // start in read mode
        isEdit = false;

        // stay on the page
        return null;
    }

    public String doEdit() {
        isEdit = true;
        return null;
    }

    public String doCancel() {
        loadBallot();
        return null;
    }

    public String doSave() {
        ballotService.update(ballot);
        return null;
    }
}