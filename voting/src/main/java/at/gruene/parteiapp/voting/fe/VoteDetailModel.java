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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.deltaspike.core.api.scope.ViewAccessScoped;
import org.apache.deltaspike.jpa.api.transaction.Transactional;
import org.apache.deltaspike.jsf.api.message.JsfMessage;
import org.primefaces.model.DualListModel;

import at.gruene.parteiapp.voting.be.BallotService;
import at.gruene.parteiapp.voting.be.entities.Ballot;
import at.gruene.parteiapp.voting.be.entities.BallotNominee;
import at.gruene.parteiapp.voting.be.entities.BallotVote;
import at.gruene.parteiapp.voting.fe.msg.BallotMessage;

/**
 * Backing bean for vote details page.
 * @author <a href="mailto:struberg@apache.org">Mark Struberg</a>
 */
@ViewAccessScoped
@Named("voteDetail")
public class VoteDetailModel implements Serializable {
    private Integer voteId;
    private Integer ballotId;

    private Ballot ballot;
    private BallotVote vote;
    private boolean isEditVote;

    private List<String> allSortedNominees;
    private List<String> castedVotes;
    private DualListModel<String> picklist;

    private @Inject BallotService ballotService;

    private @Inject JsfMessage<BallotMessage> ballotMsg;
    private List<BallotNominee> nominees;

    public Integer getVoteId() {
        return voteId;
    }

    public void setVoteId(Integer voteId) {
        this.voteId = voteId;
    }

    public boolean isEditVote() {
        return isEditVote;
    }

    public BallotVote getVote() {
        return vote;
    }

    public Integer getBallotId() {
        return ballotId;
    }

    public void setBallotId(Integer ballotId) {
        this.ballotId = ballotId;
    }

    public Ballot getBallot() {
        return ballot;
    }

    public List<String> getCastedVotes() {
        return castedVotes;
    }

    public DualListModel<String> getPicklist() {
        return picklist;
    }

    public void setPicklist(DualListModel<String> picklist) {
        this.picklist = picklist;
    }

    /**
     * Initialisation which gets called via f:viewAction
     */
    @Transactional
    public String initVote() {
        //X TODO security:

        if (ballotId == null) {
            ballotMsg.addError().noBallotIdGiven();
            return null;
        }
        if (ballot == null || !ballot.getId().equals(ballotId)) {
            // only reload ballot data if needed
            ballot = ballotService.loadBallot(ballotId);

            nominees = ballotService.getBallotNominees(ballot);
            this.allSortedNominees = nominees.stream()
                    .map(n -> n.getName())
                    .sorted()
                    .collect(Collectors.toList());
        }

        if (voteId == -1) {
            if (ballot.getStatus() != Ballot.BallotStatus.OPEN) {
                ballotMsg.addError().ballotNotOpen();
                return "ballotList.xhtml?faces-redirect=true";
            }
            // create new BallotVote
            vote = new BallotVote();
            vote.setBallot(ballot);

            isEditVote = true;
            castedVotes = new ArrayList<>();

            picklist = new DualListModel<>(allSortedNominees, castedVotes);
            return null;
        }
        vote = ballotService.loadVote(voteId);

        if (vote == null) {
            ballotMsg.addError().noVoteFound(voteId);
            return null;
        }

        // casted votes are stored as comma separated list
        castedVotes = new ArrayList<>(vote.getCastedVotes().stream().map(vId -> findNomineeName(vId)).collect(Collectors.toList()));

        picklist = new DualListModel<>(allSortedNominees, castedVotes);

        // start in read mode
        isEditVote = false;

        // stay on the page
        return null;
    }

    public String doSaveVote() {
        this.castedVotes = picklist.getTarget();
        if (castedVotes.isEmpty()) {
            ballotMsg.addError().atLeastOneVoteNeeded();
            return null;
        }

        List<Integer> castedVoteNomineeIds = castedVotes.stream()
                .map(nomineeName -> findNomineeId(nomineeName) )
                .collect(Collectors.toList());
        vote.setCastedVotes(castedVoteNomineeIds);

        ballotService.saveVote(vote);

        // reset most bean fields
        ballotId = null;
        voteId = null;

        // enter next paper ballot sheet
        return "ballotVote.xhtml?voteId=-1&ballotId=" + ballot.getId() + "&faces-redirect=true";
    }

    private Integer findNomineeId(String nomineeName) {
        return nominees.stream()
                .filter(n -> n.getName().equals(nomineeName))
                .findFirst()
                .map(n -> n.getId())
                .get();
    }

    private String findNomineeName(Integer vId) {
        return nominees.stream()
                .filter(n -> n.getId().equals(vId))
                .findFirst()
                .map(n -> n.getName())
                .get();
    }


    /**
     * This paper ballot sheet is illegal
     * @return
     */
    public String doMarkIllegalVote() {
        //X TODO wie verfahren wir mit ungueltigen Wahlzetteln?
        return null;
    }
}
