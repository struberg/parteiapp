package at.gruene.parteiapp.voting.be.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Version;

import at.gruene.parteiapp.platform.be.entities.VersionedEntity;

/**
 * A single Option on a vote
 * This can be a nominee name or an abstract option name.
 */
@Entity
@NamedQueries( {
        @NamedQuery(name = BallotNominee.QRY_FIND_BY_BALLOT,
                query = "select n from BallotNominee as n where n.ballot=:ballot")
})
public class BallotNominee implements VersionedEntity {

    public final static String QRY_FIND_BY_BALLOT = "BallotNomineeFindByBallot";

    @Id
    @GeneratedValue
    private Integer id;

    @Version
    private Integer optLock;


    @ManyToOne
    private Ballot ballot;

    /**
     * name of the nominee
     */
    @Column(nullable = false)
    private String name;

    /**
     * A short token for the nominee name.
     * This can be used for faster entry in the UI.
     * E.g. if the nominee name is 'Arnold Schoenberg' then 'as' might be taken as shortKey.
     */
    @Column(length = 3)
    private String shortKey;


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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortKey() {
        return shortKey;
    }

    public void setShortKey(String shortKey) {
        this.shortKey = shortKey;
    }
}
