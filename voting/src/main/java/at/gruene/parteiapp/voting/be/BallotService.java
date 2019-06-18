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
package at.gruene.parteiapp.voting.be;

import at.gruene.parteiapp.platform.be.entities.VersionedEntity;
import at.gruene.parteiapp.platform.be.query.QueryBuilder;
import at.gruene.parteiapp.voting.be.entities.Ballot;
import at.gruene.parteiapp.voting.be.entities.BallotNominee;
import at.gruene.parteiapp.voting.be.entities.BallotUser;
import at.gruene.parteiapp.voting.be.entities.BallotVote;
import at.gruene.platform.idm.api.GruenPrincipal;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import java.time.LocalDate;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.apache.deltaspike.jpa.api.transaction.Transactional;

/**
 * Manage metadata of the ballots.
 * This includes creating a ballot, changing it and administrate users
 */
@ApplicationScoped
@Transactional
public class BallotService {

    private @Inject GruenPrincipal user;

    private @Inject EntityManager em;

    /**
     * Create a Ballot
     *
     * @param ballotDate  On which date is the ballot planned
     * @param ballotTitle The title of the ballot, e.g. '81. LVS'
     * @param orgUnit     The organisation unit the ballot is intended for.
     * @return The freshly created Ballot
     */
    public Ballot createBallot(LocalDate ballotDate, String ballotTitle, String orgUnit) {
        Ballot ballot = new Ballot();
        ballot.setHeldAt(ballotDate);
        ballot.setName(ballotTitle);
        ballot.setStatus(Ballot.BallotStatus.CREATED);

        em.persist(ballot);

        //X TODO orgunit

        // create the current user as admin user
        BallotUser admin1 = new BallotUser();
        admin1.setBallot(ballot);
        admin1.setUserId(user.getName());
        admin1.setAdmin(true);
        admin1.setCounter(true);

        em.persist(admin1);

        return ballot;
    }

    /**
     * Search for all ballots in the given time range and optionally status and orgUnits
     *
     * @param from     lowest date to search the ballot
     * @param until    until which date (including) to search the ballot
     * @param status   if not {@code null} then only find ballots in the given status, if {@code null} all status.
     * @param orgUnits if given, only find ballots which are for one of the given orgUnits, if {@code null} then don't restrict on orgUnit
     * @return
     */
    public List<Ballot> searchBallot(LocalDate from, LocalDate until, Ballot.BallotStatus status, List<String> orgUnits) {
        QueryBuilder qb = new QueryBuilder("Select b from Ballot as b");
        qb.addQueryPart("b.heldAt>=:dateFrom", "dateFrom", from);
        qb.addQueryPart("b.heldAt<=:dateUntil", "dateUntil", until);
        if (status != null) {
            qb.addQueryPart("b.status=:ballotStatus", "ballotStatus", status);
        }
        //X TODO orgUnits, etc

        return qb.getQuery(em).getResultList();
    }

    /**
     * update the ballot to the database if there was any change.
     *
     * @param ballot
     */
    public Ballot saveBallot(Ballot ballot) {
        if (isManaged(ballot)) {
            return em.merge(ballot);
        }
        else {
            em.persist(ballot);
            return ballot;
        }
    }


    /**
     * Add a user with the given id to the ballot.
     * The creator of a ballot is automatically assigned as an administrator.
     *
     * @param ballot  the ballot to assign the user to
     * @param userId  the userId of the user to add
     * @param isAdmin whether the user can administrate the Ballot
     */
    public void addUser(Ballot ballot, String userId, boolean isAdmin) {
        if (Ballot.BallotStatus.CLOSED == ballot.getStatus()) {
            throw new IllegalStateException("Cannot add user to closed Ballot");
        }

        Validate.isTrue(!getBallotUsers(ballot).stream().anyMatch(u -> u.getUserId().equals(userId)));
        BallotUser ballotUser = new BallotUser();
        ballotUser.setBallot(ballot);
        ballotUser.setUserId(userId);
        ballotUser.setCounter(true);
        ballotUser.setAdmin(isAdmin);

        em.persist(ballotUser);
    }

    public void removeUser(BallotUser ballotUser) {
        if (Ballot.BallotStatus.CLOSED == ballotUser.getBallot().getStatus()) {
            throw new IllegalStateException("Cannot remove user from a closed Ballot");
        }
        ballotUser = em.find(BallotUser.class, ballotUser);
        em.remove(ballotUser);
    }

    public List<BallotUser> getBallotUsers(Ballot ballot) {
        TypedQuery<BallotUser> qry = em.createNamedQuery(BallotUser.QRY_FIND_BY_BALLOT, BallotUser.class);
        qry.setParameter("ballot", ballot);

        return qry.getResultList();
    }

    public List<BallotNominee> getBallotNominees(Ballot ballot) {
        TypedQuery<BallotNominee> qry = em.createNamedQuery(BallotNominee.QRY_FIND_BY_BALLOT, BallotNominee.class);
        qry.setParameter("ballot", ballot);

        return qry.getResultList();
    }


    protected <T extends VersionedEntity > boolean isManaged (T entity){
        return entity != null && entity.getId() != null;
    }

    /**
     * load a Ballot with the given Id
     */
    public Ballot loadBallot(int ballotId) {
        return em.find(Ballot.class, ballotId);
    }

    /**
     * Save or update the BallotUser to the database if there was any change.
     */
    public BallotUser saveBallotUser(BallotUser ballotUser) {
        if (Ballot.BallotStatus.CLOSED == ballotUser.getBallot().getStatus()) {
            throw new IllegalStateException("Cannot update user of a closed Ballot");
        }
        if (isManaged(ballotUser)) {
            return em.merge(ballotUser);
        }
        else {
            em.persist(ballotUser);
            return ballotUser;
        }
    }

    /**
     * Save or update the BallotNominee to the database if there was any change.
     */
    public BallotNominee saveBallotNominee(BallotNominee ballotNominee) {
        if (Ballot.BallotStatus.CLOSED == ballotNominee.getBallot().getStatus()) {
            throw new IllegalStateException("Cannot modify nominee for closed Ballot");
        }
        if (isManaged(ballotNominee)) {
            return em.merge(ballotNominee);
        }
        else {
            em.persist(ballotNominee);
            return ballotNominee;
        }
    }

    public void removeBallotNominee(BallotNominee ballotNominee) {
        if (Ballot.BallotStatus.CLOSED == ballotNominee.getBallot().getStatus()) {
            throw new IllegalStateException("Cannot remove nominee from closed Ballot");
        }
        ballotNominee = em.find(BallotNominee.class, ballotNominee.getId());
        em.remove(ballotNominee);
    }

    public BallotVote loadVote(Integer voteId) {
        return em.find(BallotVote.class, voteId);
    }

    public BallotVote loadVote(Ballot ballot, Integer voteNr) {
        TypedQuery<BallotVote> qry = em.createNamedQuery(BallotVote.QRY_FIND_BY_BALLOT_VOTENR, BallotVote.class);
        qry.setParameter("ballot", ballot);
        qry.setParameter("voteNr", voteNr);

        try {
            return qry.getSingleResult();
        }
        catch (NoResultException e) {
            return null;
        }
    }

    public BallotVote saveVote(BallotVote vote) {
        if (isManaged(vote)) {
            return em.merge(vote);
        }
        else {
            em.persist(vote);
            return vote;
        }
    }

    public List<BallotVote> getBallotVotes(Ballot ballot) {
        TypedQuery<BallotVote> qry = em.createNamedQuery(BallotVote.QRY_FIND_BY_BALLOT, BallotVote.class);
        qry.setParameter("ballot", ballot);

        return qry.getResultList();
    }
}