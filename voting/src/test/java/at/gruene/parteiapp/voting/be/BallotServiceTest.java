package at.gruene.parteiapp.voting.be;

import at.gruene.parteiapp.voting.be.entities.Ballot;
import at.gruene.parteiapp.voting.be.entities.BallotNominee;
import at.gruene.parteiapp.voting.be.entities.BallotUser;
import at.gruene.platform.idm.api.GruenPrincipal;
import at.gruene.platform.idm.api.IdmService;
import at.gruene.platform.idm.be.PrincipalProducer;

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

    private @Inject PrincipalProducer principalProducer;
    private @Inject IdmService idmService;

    private @Inject BallotService ballotService;
    private @Inject EntityManager em;

    @Test
    public void testCreateBallot() {
        GruenPrincipal principal = idmService.getUser("userA");
        principalProducer.setPrincipal(principal);

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

        BallotNominee nomineeA = new BallotNominee();
        nomineeA.setBallot(ballot2);
        nomineeA.setShortKey("K");
        nomineeA.setName("KAINZ Heinrich");
        ballotService.saveBallotNominee(nomineeA);

        BallotNominee nomineeB = new BallotNominee();
        nomineeB.setBallot(ballot2);
        nomineeB.setShortKey("A");
        nomineeB.setName("ALRICH Susanne");
        ballotService.saveBallotNominee(nomineeB);

        BallotNominee nomineeC = new BallotNominee();
        nomineeC.setBallot(ballot2);
        nomineeC.setShortKey("L");
        nomineeC.setName("LUSTIG Berta");
        ballotService.saveBallotNominee(nomineeC);

    }
    
    @Test
    public void testLongBallot() throws Exception {

        GruenPrincipal principal = idmService.getUser("userA");
        principalProducer.setPrincipal(principal);

        LocalDate today = LocalDate.now();

        Ballot ballot = ballotService.createBallot(LocalDate.now(),
                "testLws-" + UUID.randomUUID(), "LOW");
        ballotService.addUser(ballot, "testUsr2", true);
        
        addNominee(ballot, "1", "Eva Blimlinger");
        addNominee(ballot, "2", "Barbara Boll");
        addNominee(ballot, "3", "Georg Bürstmayr");
        addNominee(ballot, "4", "Meri Disoski");
        addNominee(ballot, "5", "Ewa Dziedzic");
        addNominee(ballot, "6", "Georg Egger");
        addNominee(ballot, "7", "Faika El-Nagashi");
        addNominee(ballot, "8", "Lukas Hammer");
        addNominee(ballot, "9", "Wolfgang Kainrath");
        addNominee(ballot, "10", "Christina Kastner-Frank");
        addNominee(ballot, "11", "Daniela Kickl");
        addNominee(ballot, "12", "Alev Korun");
        addNominee(ballot, "13", "Markus Koza");
        addNominee(ballot, "14", "Martha-Sophie Krumpeck");
        addNominee(ballot, "15", "Bernhard Lahner");
        addNominee(ballot, "16", "Maria Lackner");
        addNominee(ballot, "17", "Martin Margulies");
        addNominee(ballot, "18", "Sigrid Maurer");
        addNominee(ballot, "19", "Elmar Mayer-Baldasseron");
        addNominee(ballot, "20", "Ornette Novotny");
        addNominee(ballot, "21", "Wolfgang Orgler");
        addNominee(ballot, "22", "Markus Reichhart");
        addNominee(ballot, "23", "Dominik Rein");
        addNominee(ballot, "24", "Noah Schönhart");
        addNominee(ballot, "25", "Gerhard Schrenk");
        addNominee(ballot, "26", "Felix Stadler");
        addNominee(ballot, "27", "Johannes Stöckler");
        addNominee(ballot, "28", "Florian Tschebul");
        addNominee(ballot, "29", "Lukas Wurz");
    }

    private void addNominee(Ballot ballot, String shortKey, String name) {
        BallotNominee nominee = new BallotNominee();
        nominee.setBallot(ballot);
        nominee.setShortKey(shortKey);
        nominee.setName(name);
        ballotService.saveBallotNominee(nominee);
    }
}
