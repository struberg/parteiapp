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
package at.gruene.parteiapp.voting.be.entities.listener;

import java.time.OffsetDateTime;

import javax.inject.Inject;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import org.apache.deltaspike.core.api.provider.BeanProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gruene.parteiapp.voting.be.entities.AuditedEntity;
import at.gruene.platform.idm.api.GruenPrincipal;

/**
 * JPA entity listener which automatically fills the createdAt/By and modifiedAt/By fields
 *
 * @author <a href="mailto:struberg@apache.org">Mark Struberg</a>
 */
public class AuditedEntityListener {

    private static final Logger log = LoggerFactory.getLogger(AuditedEntityListener.class);

    private static final String ERROR_MESSAGE_IGNORING_UNSUPPORTED_ENTITY_TYPE = "ignoring unsupported Entity type: {}";

    private GruenPrincipal principal;

    @PrePersist
    public void setCreatedFields(Object o) {
        if (!(o instanceof AuditedEntity)) {
            log.error(ERROR_MESSAGE_IGNORING_UNSUPPORTED_ENTITY_TYPE, o.getClass());
            return;
        }

        AuditedEntity entity = (AuditedEntity) o;

        OffsetDateTime now = getNow();
        String loggedInUser = getUserId();

        if ((entity.getCreatedAt() == null) && (entity.getCreatedBy() == null)) {
            entity.setCreatedAt(now);
            entity.setCreatedBy(loggedInUser);
        }

        entity.setModifiedAt(now);
        entity.setModifiedBy(loggedInUser);
    }


    @PreUpdate
    public void setModifiedFields(Object o) {
        if (!(o instanceof AuditedEntity)) {
            log.error(ERROR_MESSAGE_IGNORING_UNSUPPORTED_ENTITY_TYPE, o.getClass());
            return;
        }

        AuditedEntity entity = (AuditedEntity) o;

        entity.setModifiedAt(getNow());
        entity.setModifiedBy(getUserId());
    }

    private OffsetDateTime getNow() {
        //TODO potentially replace with something like TimeService?
        return OffsetDateTime.now();
    }

    private String getUserId() {
        if (principal == null) {
            principal = BeanProvider.getContextualReference(GruenPrincipal.class);
        }
        return principal.getName();
    }
}
