/*
 * Copyright Author and Authors. See the NOTICE file distributed with
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
package at.gruene.parteiapp.platform.be;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.persistence.EntityManager;

import at.gruene.parteiapp.platform.be.entities.BaseEntity;

/**
 * Some base methods for all CRUD services.
 *
 * @author <a href="mailto:struberg@apache.org">Mark Struberg</a>
 */
public abstract class CrudService<T extends BaseEntity> extends EntityService {

    /**
     * Cache for getEntityClass()
     */
    private Class<T> entityClass;


    protected abstract EntityManager getEntityManager();

    /**
     * Saves new entity in database.
     *
     * @param entity Entity
     * @return Saved entity
     */
    T create(T entity) {
        getEntityManager().persist(entity);
        return entity;
    }

    /**
     * Updates entity. Merges the state of the given object into the persistence context.
     *
     * @param entity Entity to merge
     * @return The instance that was updated
     */
    public T update(T entity) {
        return getEntityManager().merge(entity);
    }

    /**
     * Saves entity in database. Calls update() or create(), depending on entity state.
     *
     * @param entity Entity
     * @return Saved entity
     */
    public T save(T entity) {
        if (isManaged(entity)) {
            return update(entity);
        } else {
            return create(entity);
        }
    }

    /**
     * Get by primary key.
     *
     * @param id Primary key
     * @return Found entity or null, if entity does not exist
     */
    public T getById(Object id) {
        return getEntityManager().find(getEntityClass(), id);
    }

    /**
     * Deletes entity from database.
     *
     * @param entity Entity to delete
     */
    public void delete(T entity) {
        if (entity == null) {
            return;
        }

        T dbentity = getEntityManager().getReference(getEntityClass(), entity.getId());
        getEntityManager().remove(dbentity);
    }

    /**
     * Reattaches given entity to persistence context.
     * Basically uses {@link #update(BaseEntity)} if entity is already persisted.
     *
     * @param entity entity to reattach
     * @return reattached entity or input entity, if entity is not persisted
     */
    public T reattach(T entity) {
        if (isManaged(entity)) {
            return update(entity);
        }
        return entity;
    }

    /**
     * Returns entity class for this service. Uses reflection, overwrite if necessary.
     *
     * @return Entity class
     */
    @SuppressWarnings("unchecked")
    protected Class<T> getEntityClass() {
        if (entityClass == null) {
            final Type thisType = getClass().getGenericSuperclass();
            final Type entityType;
            if (thisType instanceof ParameterizedType) {
                entityType = ((ParameterizedType) thisType).getActualTypeArguments()[0];
            } else if (thisType instanceof Class) {
                entityType = ((ParameterizedType) ((Class) thisType).getGenericSuperclass()).getActualTypeArguments()[0];
            } else {
                throw new IllegalArgumentException("Problem handling type construction for " + getClass());
            }

            if (entityType instanceof Class) {
                entityClass = (Class<T>) entityType;
            } else if (entityType instanceof ParameterizedType) {
                entityClass = (Class<T>) ((ParameterizedType) entityType).getRawType();
            } else {
                throw new IllegalArgumentException("Problem determining the class of the generic for " + getClass());
            }
        }

        return entityClass;
    }

}
