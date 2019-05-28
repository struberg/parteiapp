package at.gruene.parteiapp.voting.be;

import at.gruene.parteiapp.voting.be.entities.Ballot;
import at.gruene.parteiapp.voting.be.entities.BallotUser;
import at.gruene.platform.idm.api.GruenPrincipal;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDate;
import java.util.List;

/**
 * Manage metadata of the ballots.
 * This includes creating a ballot, changing it and administrate users
 */
@ApplicationScoped
public class BallotManagementService {

    private @Inject GruenPrincipal user;

    /**
     * Create a Ballot
     * @param ballotDate On which date is the ballot planned
     * @param ballotTitle The title of the ballot, e.g. '81. LVS'
     * @param orgUnit The organisation unit the ballot is intended for.
     * @return The freshly created Ballot
     */
    public Ballot createBallot(LocalDate ballotDate, String ballotTitle, String orgUnit) {
        return null;
    }

    /**
     * Search for all ballots in the given time range and optionally status and orgUnits
     * @param from lowest date to search the ballot
     * @param until until which date (including) to search the ballot
     * @param status if not {@code null} then only find ballots in the given status, if {@code null} all status.
     * @param orgUnits if given, only find ballots which are for one of the given orgUnits, if {@code null} then don't restrict on orgUnit
     * @return
     */
    public List<Ballot> searchBallot(LocalDate from, LocalDate until, Ballot.BallotStatus status, List<String> orgUnits) {
        return null;
    }

    /**
     * update the ballot to the database if there was any change.
     * @param ballot
     */
    public void update(Ballot ballot) {
        //X TODO
    }


    /**
     * Add a user with the given id to the ballot.
     * The creator of a ballot is automatically assigned as an administrator.
     * @param ballot the ballot to assign the user to
     * @param userId the userId of the user to add
     * @param isAdmin whether the user can administrate the Ballot
     */
    public void addUser(Ballot ballot, String userId, boolean isAdmin) {

    }

    public List<BallotUser> getBallotUser(Ballot ballot) {
        return null;
    }

}
