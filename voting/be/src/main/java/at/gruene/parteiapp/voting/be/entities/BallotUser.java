package at.gruene.parteiapp.voting.be.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class BallotUser {

    @Id
    @GeneratedValue
    private int id;

    /**
     * user id from LDAP.
     */
    @Column(length = 70)
    private String userId;

    /**
     * user name from LDAP
     */
    private String name;

    /**
     * Whether this user is administrator.
     * And admin can add users and start and stop a poll
     */
    private boolean isAdmin;

    /**
     * Whether this user can enter polls
     */
    private boolean isCounter;



}
