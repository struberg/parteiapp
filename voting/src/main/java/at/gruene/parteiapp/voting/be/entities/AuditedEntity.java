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

import java.time.OffsetDateTime;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import at.gruene.parteiapp.platform.be.CollumnLength;
import at.gruene.parteiapp.platform.be.entities.BaseEntity;
import at.gruene.parteiapp.voting.be.entities.listener.AuditedEntityListener;

/**
 * Base class for all entities who have a simple editing log
 * @author <a href="mailto:struberg@apache.org">Mark Struberg</a>
 */
@MappedSuperclass
@EntityListeners(AuditedEntityListener.class)
public abstract class AuditedEntity implements BaseEntity {

    /**
     * The userId who entered the vote into the system
     */
    @Column(name="CREATED_BY", length= CollumnLength.USERID)
    private String createdBy;

    /**
     * The Timestamp when this vote entry was created
     */
    @Column(name="CREATED_AT")
    private OffsetDateTime createdAt;

    /**
     * The userId who last touched this row into the system.
     */
    @Column(name="MODIFIED_BY", length= CollumnLength.USERID)
    private String modifiedBy;

    /**
     * The Timestamp when this row entry got touched the last time.
     */
    @Column(name="MODIFIED_AT")
    private OffsetDateTime modifiedAt;

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

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public OffsetDateTime getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(OffsetDateTime modifiedAt) {
        this.modifiedAt = modifiedAt;
    }
}
