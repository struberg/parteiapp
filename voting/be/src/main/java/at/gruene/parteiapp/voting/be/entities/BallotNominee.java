package at.gruene.parteiapp.voting.be.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * A single Option on a vote
 * This can be a nominee name or an abstract option name.
 */
@Entity
public class BallotNominee {

    @Id
    @GeneratedValue
    private int id;

    @ManyToOne
    private Ballot ballot;

    /**
     * name of the nominee
     */
    private String name;

}
