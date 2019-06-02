package at.gruene.parteiapp.voting.be.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import at.gruene.parteiapp.platform.be.CollumnLength;
import at.gruene.parteiapp.platform.be.entities.VersionedEntity;

@Entity
@NamedQueries( {
        @NamedQuery(name = BallotUser.QRY_FIND_BY_BALLOT,
                    query = "select u from BallotUser as u where u.ballot=:ballot")
})
@Table(uniqueConstraints = {
        @UniqueConstraint(name="UC_USER", columnNames = {"BALLOT_ID", "USERID"})
})
public class BallotUser implements VersionedEntity {

    public final static String QRY_FIND_BY_BALLOT = "BallotUserFindByBallot";

    @Id
    @GeneratedValue
    private Integer id;

    @Version
    private Integer optLock;

    @ManyToOne
    @JoinColumn(name="BALLOT_ID")
    private Ballot ballot;

    /**
     * user id from LDAP.
     */
    @Column(name="USERID", length = CollumnLength.USERID, nullable = false)
    private String userId;

    /**
     * user name from LDAP
     */
    private String name;

    /**
     * Whether this user is administrator.
     * And admin can add users and start and stop a poll
     */
    @Column(nullable = false)
    private boolean isAdmin;

    /**
     * Whether this user can enter polls
     */
    @Column(nullable = false)
    private boolean isCounter;

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public Integer getOptLock() {
        return optLock;
    }

    public Ballot getBallot() {
        return ballot;
    }

    public void setBallot(Ballot ballot) {
        this.ballot = ballot;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public boolean isCounter() {
        return isCounter;
    }

    public void setCounter(boolean counter) {
        isCounter = counter;
    }
}