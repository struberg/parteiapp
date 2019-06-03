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
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.deltaspike.core.api.scope.ViewAccessScoped;

import at.gruene.parteiapp.voting.be.BallotService;
import at.gruene.parteiapp.voting.be.entities.Ballot;

/**
 * @author <a href="mailto:struberg@apache.org">Mark Struberg</a>
 */
@Named("ballots")
@ViewAccessScoped
public class BallotsModel implements Serializable {

    private @Inject BallotService ballotService;

    private LocalDate searchFrom = LocalDate.now();
    private LocalDate searchUntil = LocalDate.now();
    private Ballot.BallotStatus searchStatus = null;

    /**
     * the found ballots
     */
    private List<Ballot> ballots = Collections.emptyList();

    public LocalDate getSearchFrom() {
        return searchFrom;
    }

    public void setSearchFrom(LocalDate searchFrom) {
        this.searchFrom = searchFrom;
    }

    public LocalDate getSearchUntil() {
        return searchUntil;
    }

    public void setSearchUntil(LocalDate searchUntil) {
        this.searchUntil = searchUntil;
    }

    public Ballot.BallotStatus getSearchStatus() {
        return searchStatus;
    }

    public void setSearchStatus(Ballot.BallotStatus searchStatus) {
        this.searchStatus = searchStatus;
    }

    public List<Ballot> getBallots() {
        return ballots;
    }

    public void setBallots(List<Ballot> ballots) {
        this.ballots = ballots;
    }

    /**
     * search action
     */
    public String doSearch() {
        //X TODO add orgUnits.
        ballots = ballotService.searchBallot(searchFrom, searchUntil, searchStatus, null);

        return null;
    }

    public String doCreateBallot() {
        return "/ballotDetail.xhtml?ballotId=-1&faces-redirect=true";
    }
}
