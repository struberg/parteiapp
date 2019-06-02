package at.gruene.parteiapp.voting.be;

import at.gruene.parteiapp.voting.be.entities.Ballot;
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
public class BallotManagementServiceTest {

    private @Inject BallotManagementService ballotManagementService;

    private @Inject EntityManager em;

    @Test
    public void testCreateBallot() {
        LocalDate today = LocalDate.now();
        List<Ballot> oldBallots = ballotManagementService.searchBallot(today, today, null, null);

        Ballot ballot = ballotManagementService.createBallot(LocalDate.now(),
                "testBallotNow-" + UUID.randomUUID(), "BO10");

        ballotManagementService.addUser(ballot, "testUsr2", true);
        ballotManagementService.addUser(ballot, "testUsr3", false);

        em.clear();
        Ballot ballot2 = em.find(Ballot.class, ballot.getId());
        assertNotNull(ballot2);
        List<BallotUser> ballotUser = ballotManagementService.getBallotUser(ballot2);
        assertNotNull(ballotUser);
        assertEquals(3, ballotUser.size());

        List<Ballot> newBallots = ballotManagementService.searchBallot(today, today, null, null);
        assertEquals(oldBallots.size()+1, newBallots.size());

    }
}
