package at.gruene.parteiapp.voting.be.entities;

import javax.persistence.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import at.gruene.parteiapp.platform.be.CollumnLength;
import at.gruene.parteiapp.platform.be.entities.VersionedEntity;

/**
 * A single vote on a ballot
 */
@Entity
public class BallotVote implements VersionedEntity {

    @Id
    @GeneratedValue
    private Integer id;

    @Version
    private Integer optLock;

    @ManyToOne
    private Ballot ballot;

    @Column(nullable = false)
    private Integer voteNr;

    /**
     * comma (',') separated list of votes on the single paper ballot.
     * Ordered with highest first.
     */
    @Column(length = 4000)
    private String nominees;

    /**
     * The userId who entered the vote into the system
     */
    @Column(length= CollumnLength.USERID)
    private String createdBy;

    /**
     * The Timestamp when this vote entry was created
     */
    private OffsetDateTime createdAt;

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

    public Integer getVoteNr() {
        return voteNr;
    }

    public void setVoteNr(Integer voteNr) {
        this.voteNr = voteNr;
    }

    public List<String> getNominees() {
        if (nominees == null) {
            return Collections.emptyList();
        }
        String[] nomineeIds = nominees.split(",");
        return new ArrayList<>(Arrays.asList(nomineeIds));
    }

    public void setNominees(List<String> nomineeIds) {
        this.nominees = nominees;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
