/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package at.gruene.parteiapp.voting.be.entities;

import java.time.LocalDate;
import java.time.OffsetDateTime;

import javax.persistence.*;

import at.gruene.parteiapp.platform.be.entities.VersionedEntity;

/**
 * A single Ballot. E.g. a vote for a leader of a division held at a certain date.
 *
 * @author <a href="mailto:struberg@apache.org">Mark Struberg</a>
 */
@Entity
public class Ballot implements VersionedEntity {

    /**
     * Status of the ballot.
     *
     * @author <a href="mailto:struberg@apache.org">Mark Struberg</a>
     */
    public enum BallotStatus {
        /**
         * The ballot metadata got created, but no entry of votes is possible
         */
        CREATED,

        /**
         * The ballot votes can be entered, but are not yet tallied.
         */
        OPEN,

        /**
         * The ballot votes cannot be entered anymore as the counting is finished.
         */
        CLOSED,
    }


    @Id
    @GeneratedValue
    private Integer id;

    @Version
    private Integer optLock;

    @Column(length = 255, nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDate heldAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BallotStatus status;

    private OffsetDateTime pollOpened;
    private OffsetDateTime pollClosed;

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public Integer getOptLock() {
        return optLock;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getHeldAt() {
        return heldAt;
    }

    public void setHeldAt(LocalDate heldAt) {
        this.heldAt = heldAt;
    }

    public BallotStatus getStatus() {
        return status;
    }

    public void setStatus(BallotStatus status) {
        if (this.status != status) {
            if (BallotStatus.OPEN == status) {
                this.pollOpened = OffsetDateTime.now();
            }
            else if (BallotStatus.CLOSED == status) {
                this.pollClosed = OffsetDateTime.now();
            }
        }
        this.status = status;
    }

    public OffsetDateTime getPollOpened() {
        return pollOpened;
    }

    public OffsetDateTime getPollClosed() {
        return pollClosed;
    }

}
