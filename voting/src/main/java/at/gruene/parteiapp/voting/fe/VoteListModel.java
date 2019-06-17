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
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.deltaspike.core.api.scope.ViewAccessScoped;
import org.apache.deltaspike.jpa.api.transaction.Transactional;
import org.apache.deltaspike.jsf.api.message.JsfMessage;

import at.gruene.parteiapp.voting.be.BallotService;
import at.gruene.parteiapp.voting.be.entities.Ballot;
import at.gruene.parteiapp.voting.be.entities.BallotNominee;
import at.gruene.parteiapp.voting.be.entities.BallotVote;
import at.gruene.parteiapp.voting.be.util.JsfDownload;
import at.gruene.parteiapp.voting.fe.msg.BallotMessage;

/**
 * @author <a href="mailto:struberg@apache.org">Mark Struberg</a>
 */
@Named("voteList")
@ViewAccessScoped
public class VoteListModel implements Serializable {

    private Integer ballotId;
    private Ballot ballot;
    private List<BallotNominee> nominees;
    private Map<Integer, BallotNominee> nomineesById;
    private List<BallotVote> ballotVotes;

    private List<VoteLine> voteSheets;

    private Integer maxVoteNr = null;
    private Integer amountBallotSheets = null;

    private @Inject BallotService ballotService;
    private @Inject JsfMessage<BallotMessage> ballotMsg;
    private boolean shortKeysAvailable;

    /**
     * Initialisation which gets called via f:viewAction
     */
    @Transactional
    public String initVoteList() {
        //X TODO security:

        if (ballotId == null) {
            ballotMsg.addError().noBallotIdGiven();
            return null;
        }
        if (ballot == null || !ballot.getId().equals(ballotId)) {
            // only reload ballot data if needed
            ballot = ballotService.loadBallot(ballotId);
            nominees = ballotService.getBallotNominees(ballot);

            nomineesById = nominees.stream()
                    .collect(Collectors.toMap(BallotNominee::getId, Function.identity()));

            this.shortKeysAvailable = nominees.stream()
                    .anyMatch(n -> StringUtils.isNoneEmpty(n.getShortKey()));
        }

        ballotVotes = ballotService.getBallotVotes(ballot);
        if (!ballotVotes.isEmpty()) {
            // first sheet is either 0 or 1, depending where the vote numbering starts
            int nextSheetNr = Integer.min(ballotVotes.get(0).getVoteNr(), 1) + 1;

            voteSheets = new ArrayList<>(ballotVotes.size());
            for (BallotVote v : ballotVotes) {
                if (v.getVoteNr() > nextSheetNr) {
                    voteSheets.add(new VoteLine(nextSheetNr, v.getVoteNr() - 1));
                }
                voteSheets.add(new VoteLine(v));
                nextSheetNr = v.getVoteNr() + 1;
            }
        }

        maxVoteNr = ballotVotes.stream()
                .map(v -> v.getVoteNr().intValue())
                .reduce(Integer::max).orElse(null);
        amountBallotSheets = voteSheets.size();

        return null;
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

    public List<VoteLine> getVoteSheets() {
        return voteSheets;
    }

    public String calculateShortKeys(List<Integer> castedVoteIds) {
        if (!shortKeysAvailable) {
            return null;
        }
        if (castedVoteIds == null || castedVoteIds.isEmpty()) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for (Integer castedVote : castedVoteIds) {
            BallotNominee nominee = nomineesById.get(castedVote);
            sb.append(nominee.getShortKey()).append(",");
        }

        return sb.toString();
    }

    public String calculateVoteString(List<Integer> castedVoteIds) {
        if (castedVoteIds == null || castedVoteIds.isEmpty()) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for (Integer castedVote : castedVoteIds) {
            BallotNominee nominee = nomineesById.get(castedVote);
            sb.append(nominee.getName()).append(", ");
        }

        return sb.toString();
    }

    public Integer getMaxVoteNr() {
        return maxVoteNr;
    }

    public Integer getAmountBallotSheets() {
        return amountBallotSheets;
    }

    public String downloadVotes() {
        StringBuilder content = new StringBuilder(4096);

        for (BallotVote ballotVote : ballotVotes) {
            content.append(ballotVote.getVoteNr());
            for (Integer castedVote : ballotVote.getCastedVotes()) {
                // quick and dirty for now.
                content.append(";")
                        .append(nomineesById.get(castedVote).getName());
            }

            content.append("\n");
        }

        JsfDownload.streamFileDownload(content.toString(), "text/csv", ballot.getName() + "_votes.csv");
        return null;
    }


    public class VoteLine implements Serializable {
        private final Integer id;
        private final Integer voteNr;
        private final boolean invalid;
        private final String shortKeys;
        private final String voteString;

        VoteLine(BallotVote vote) {
            this.id = vote.getId();
            this.voteNr = vote.getVoteNr();
            this.invalid = StringUtils.isNotEmpty(vote.getInvalidVoteReason());

            if (invalid) {
                this.shortKeys = null;
                this.voteString = vote.getInvalidVoteReason();
            }
            else {
                List<Integer> castedVotes = vote.getCastedVotes();
                this.shortKeys = calculateShortKeys(castedVotes);
                this.voteString = calculateVoteString(castedVotes);
            }
        }

        /**
         * A VoteLine which represents a 'hole' in the entered paper ballot sheets.
         * That means there is something still not entered into the system.
         *
         * @param missingStart
         * @param missingEnd
         */
        VoteLine(int missingStart, int missingEnd) {
            this.id = null;
            this.voteNr = null;
            this.invalid = false;
            this.shortKeys = null;
            this.voteString = ballotMsg.get().missingPaperBallotSheets(missingStart, missingEnd);
        }

        public Integer getId() {
            return id;
        }

        public Integer getVoteNr() {
            return voteNr;
        }

        public boolean isInvalid() {
            return invalid;
        }

        public String getShortKeys() {
            return shortKeys;
        }

        public String getVoteString() {
            return voteString;
        }
    }
}