package at.gruene.parteiapp.voting.be.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

import at.gruene.parteiapp.platform.be.entities.VersionedEntity;

/**
 * A single Option on a vote
 * This can be a nominee name or an abstract option name.
 */
@Entity
public class BallotNominee implements VersionedEntity {

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
    private String name;

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
}
