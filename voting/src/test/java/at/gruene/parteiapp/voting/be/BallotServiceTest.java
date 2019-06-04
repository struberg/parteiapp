package at.gruene.parteiapp.voting.be;

import at.gruene.parteiapp.voting.be.entities.Ballot;
import at.gruene.parteiapp.voting.be.entities.BallotNominee;
import at.gruene.parteiapp.voting.be.entities.BallotUser;

import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author <ahref="mailto:struberg@apache.org" > Mark Struberg</a>
 */

@RunWith(CdiTestRunner.class)
public class BallotServiceTest {

    private @Inject BallotService ballotService;

    private @Inject EntityManager em;

    @Test
    public void testCreateBallot() {
        LocalDate today = LocalDate.now();
        List<Ballot> oldBallots = ballotService.searchBallot(today, today, null, null);

        Ballot ballot = ballotService.createBallot(LocalDate.now(),
                "testBallotNow-" + UUID.randomUUID(), "BO10");

        ballotService.addUser(ballot, "testUsr2", true);
        ballotService.addUser(ballot, "testUsr3", false);

        em.clear();
        Ballot ballot2 = em.find(Ballot.class, ballot.getId());
        assertNotNull(ballot2);
        List<BallotUser> ballotUser = ballotService.getBallotUsers(ballot2);
        assertNotNull(ballotUser);
        assertEquals(3, ballotUser.size());

        List<BallotNominee> nominees = ballotService.getBallotNominees(ballot2);
        assertNotNull(nominees);
        assertEquals(0, nominees.size());

        List<Ballot> newBallots = ballotService.searchBallot(today, today, null, null);
        assertEquals(oldBallots.size()+1, newBallots.size());

    }
}
