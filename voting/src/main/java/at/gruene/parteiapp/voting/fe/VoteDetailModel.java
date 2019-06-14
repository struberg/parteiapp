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
import java.util.Optional;
import java.util.stream.Collectors;

import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
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

    private List<BallotNominee> nominees;
    private List<String> allSortedNominees;

    private String directInput;
    private boolean shortKeysAvailable;

    private List<String> availableVotes;
    private List<String> castedVotes;

    private DualListModel<String> picklist;

    /**
     * The previously entered paper ballot sheet
     */
    private BallotVote lastVote;

    private @Inject BallotService ballotService;

    private @Inject JsfMessage<BallotMessage> ballotMsg;


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

    public boolean isShortKeysAvailable() {
        return shortKeysAvailable;
    }

    public BallotVote getLastVote() {
        return lastVote;
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

            this.shortKeysAvailable = nominees.stream()
                    .anyMatch(n -> StringUtils.isNoneEmpty(n.getShortKey()));

            this.allSortedNominees = nominees.stream()
                    .map(n -> getNomineeName(n))
                .sorted()
                    .collect(Collectors.toList());
        }

        this.availableVotes = new ArrayList<>(allSortedNominees);

        if (voteId == -1) {
            if (ballot.getStatus() != Ballot.BallotStatus.OPEN) {
                ballotMsg.addError().ballotNotOpen();
                return "ballotList.xhtml?faces-redirect=true";
            }

            Integer newVoteNr = null;

            if (vote != null && vote.getVoteNr() != null) {
                // if there was a previous vote entered, then we automatically prefill the next paper ballot sheet number
                newVoteNr = vote.getVoteNr() + 1;
            }


            // create new BallotVote
            vote = new BallotVote();
            vote.setBallot(ballot);
            vote.setVoteNr(newVoteNr);

            isEditVote = true;
            this.directInput = null;
            castedVotes = new ArrayList<>();
            picklist = new DualListModel<>(availableVotes, castedVotes);

            return null;
        }
        else {
            lastVote = null;
        }

        vote = ballotService.loadVote(voteId);

        if (vote == null) {
            ballotMsg.addError().noVoteFound(voteId);
            return null;
        }

        // casted votes are stored as comma separated list
        castedVotes = new ArrayList<>(vote.getCastedVotes().stream().map(vId -> findNomineeName(vId)).collect(Collectors.toList()));
        this.directInput = calculateDirectInput(vote.getCastedVotes());
        picklist = new DualListModel<>(availableVotes, castedVotes);

        // start in read mode
        isEditVote = false;

        // stay on the page
        return null;
    }

    private String calculateDirectInput(List<Integer> castedVoteIds) {
        if (castedVoteIds == null || castedVoteIds.isEmpty()) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for (Integer castedVote : castedVoteIds) {
            BallotNominee nominee = nominees.stream().filter(n -> n.getId().equals(castedVote)).findFirst().get();
            sb.append(nominee.getShortKey()).append(",");
            availableVotes.remove(getNomineeName(nominee));
        }

        return sb.toString();
    }

    public String doSaveVote() {
        this.castedVotes = picklist.getTarget();
        if (castedVotes.isEmpty() && StringUtils.isEmpty(vote.getInvalidVoteReason())) {
            ballotMsg.addError().atLeastOneVoteOrInvalidNeeded();
            return null;
        }
        if (!castedVotes.isEmpty() && StringUtils.isNotEmpty(vote.getInvalidVoteReason())) {
            ballotMsg.addError().exactlyOneVoteOrInvalidNeeded();
            return null;
        }
        if (ballotService.loadVote(ballot, vote.getVoteNr()) != null) {
            ballotMsg.addError().shortKeyAlreadyExists(vote.getVoteNr());
            return null;
        }

        List<Integer> castedVoteNomineeIds = castedVotes.stream()
                .map(nomineeName -> findNomineeIdByName(nomineeName) )
                .collect(Collectors.toList());
        vote.setCastedVotes(castedVoteNomineeIds);

        ballotService.saveVote(vote);

        lastVote = vote;

        // reset most bean fields
        ballotId = null;
        voteId = null;

        // enter next paper ballot sheet
        return "ballotVote.xhtml?voteId=-1&ballotId=" + ballot.getId() + "&faces-redirect=true";
    }


    /**
     * update the selection List in the pickList
     * from the data entered in {@link #directInput}
     */
    public void updatePickList(AjaxBehaviorEvent ajaxBehaviorEvent) {
        // fill the picklist fresh from the direct input
        this.castedVotes.clear();
        this.availableVotes.clear();
        this.availableVotes.addAll(allSortedNominees);

        if (StringUtils.isNotEmpty(directInput)) {
            String[] keys = StringUtils.split(directInput," ,-");
            for (String key : keys) {
                if (StringUtils.isNotEmpty(key)) {
                    Optional<String> nomineeNameByShortKey = findNomineeNameByShortKey(key);
                    if (!nomineeNameByShortKey.isPresent()) {
                        ballotMsg.addWarn().invalidShortKey(key);
                    }
                    else {
                        castedVotes.add(nomineeNameByShortKey.get());
                        availableVotes.remove(nomineeNameByShortKey.get());
                    }
                }
            }
        }
    }

    /**
     * update the selection List in the pickList
     * from the data entered in {@link #directInput}
     */
    public void updateDirectInput(AjaxBehaviorEvent ajaxBehaviorEvent) {
        StringBuilder sb = new StringBuilder();
        castedVotes = picklist.getTarget();
        for (String castedVote : castedVotes) {
            BallotNominee nominee = nominees.stream().filter(n -> getNomineeName(n).equals(castedVote)).findFirst().get();
            sb.append(nominee.getShortKey()).append(",");
            availableVotes.remove(getNomineeName(nominee));
        }

        this.directInput = sb.toString();
    }

    private Integer findNomineeIdByName(String nomineeName) {
        return nominees.stream()
                .filter(n -> getNomineeName(n).equals(nomineeName))
                .findFirst()
                .map(n -> n.getId())
                .get();
    }

    private Optional<String> findNomineeNameByShortKey(String shortKey) {
        return nominees.stream()
                .filter(n -> shortKey.equalsIgnoreCase(n.getShortKey()))
                .findFirst()
                .map(n -> getNomineeName(n));
    }

    private String findNomineeName(Integer vId) {
        return nominees.stream()
                .filter(n -> n.getId().equals(vId))
                .findFirst()
                .map(n -> getNomineeName(n))
                .get();
    }


    public String getDirectInput() {
        return directInput;
    }

    public void setDirectInput(String directInput) {
        this.directInput = directInput;
    }

    private String getNomineeName(BallotNominee nominee) {
        if (shortKeysAvailable) {
            return "(" + nominee.getShortKey() + ") " + nominee.getName();
        }
        else {
            return nominee.getName();
        }
    }

}
