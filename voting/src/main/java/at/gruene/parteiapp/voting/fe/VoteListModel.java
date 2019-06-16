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
    private List<VoteLine> voteSheets;

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

        List<BallotVote> ballotVotes = ballotService.getBallotVotes(ballot);
        voteSheets = ballotVotes.stream()
                .map(VoteLine::new)
                .collect(Collectors.toList());

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