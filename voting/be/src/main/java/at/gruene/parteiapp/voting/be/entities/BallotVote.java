package at.gruene.parteiapp.voting.be.entities;

import javax.persistence.*;
import java.util.List;

/**
 * A single vote on a ballot
 */
public class BallotVote {

    @Id
    @GeneratedValue
    private int id;

    /**
     * comma (',') separated list of votes on the single paper ballot.
     * Ordered with highest first.
     */
    @Column(length = 4000)
    private String nominees;


}
