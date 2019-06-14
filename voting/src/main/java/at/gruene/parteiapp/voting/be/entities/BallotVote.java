package at.gruene.parteiapp.voting.be.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import at.gruene.parteiapp.platform.be.entities.VersionedEntity;

/**
 * A single vote on a ballot
 */
@Entity
@Table(name = "BALLOT_VOTE",
    uniqueConstraints = {
        @UniqueConstraint(name="U_VOTENR", columnNames = {"BALLOT_ID","VOTE_NR"})
    })
@NamedQuery(name = BallotVote.QRY_FIND_BY_BALLOT,
        query = "select v from BallotVote as v where v.ballot=:ballot order by v.voteNr asc")
@NamedQuery(name = BallotVote.QRY_FIND_BY_BALLOT_VOTENR,
        query = "select v from BallotVote as v where v.ballot=:ballot and v.voteNr=:voteNr")
public class BallotVote extends AuditedEntity implements VersionedEntity {

    public final static String QRY_FIND_BY_BALLOT = "BallotVoteFindByBallot";
    public final static String QRY_FIND_BY_BALLOT_VOTENR = "BallotVoteFindByBallotVoteNr";

    @Id
    @GeneratedValue
    @Column(name="ID")
    private Integer id;

    @Version
    @Column(name="OPTLOCK")
    private Integer optLock;

    @ManyToOne(optional = false)
    @JoinColumn(name="BALLOT_ID")
    private Ballot ballot;

    @Column(name="VOTE_NR", nullable = false)
    @NotNull
    private Integer voteNr;

    /**
     * If a paper ballot sheet is illegal then a reason must be recorded.
     */
    @Column(name="INVALID_REASON", length = 255)
    private String invalidVoteReason;

    /**
     * comma (',') separated list of votes on the single paper ballot.
     * Ordered with highest first.
     * Each entry is a {@link BallotNominee#getId()}
     */
    @Column(name="CASTED_VOTES", length = 4000)
    private String castedVotes;


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

    /**
     * Whether this paper ballot sheet is not
     * @return
     */
    public boolean isInvalid() {
        return this.invalidVoteReason != null && !invalidVoteReason.isEmpty();
    }

    public String getInvalidVoteReason() {
        return invalidVoteReason;
    }

    public void setInvalidVoteReason(String illegalReason) {
        this.invalidVoteReason = illegalReason;
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




}
