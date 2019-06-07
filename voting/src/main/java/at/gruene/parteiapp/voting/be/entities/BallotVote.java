package at.gruene.parteiapp.voting.be.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

    @ManyToOne(optional = false)
    private Ballot ballot;

    @Column(nullable = false)
    @NotNull
    private Integer voteNr;

    /**
     * comma (',') separated list of votes on the single paper ballot.
     * Ordered with highest first.
     * Each entry is a {@link BallotNominee#getId()}
     */
    @Column(length = 4000)
    private String castedVotes;

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

    public List<Integer> getCastedVotes() {
        if (castedVotes == null) {
            return Collections.emptyList();
        }
        String[] parts = castedVotes.split(",");
        List<Integer> nomineeIds = new ArrayList<>(parts.length);
        for (String nomineeId : parts) {
            nomineeIds.add(Integer.valueOf(nomineeId));
        }
        return nomineeIds;
    }

    public void setCastedVotes(List<Integer> castedVotes) {
        if (castedVotes ==null || castedVotes.isEmpty()) {
            this.castedVotes = null;
        }

        this.castedVotes = String.join(",",
                castedVotes.stream().map( i -> Integer.toString(i)).collect(Collectors.toList()));
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
