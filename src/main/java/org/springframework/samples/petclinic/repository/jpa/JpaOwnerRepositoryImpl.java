/*
 * Copyright 2002-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.repository.jpa;

import java.util.Collection;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import org.springframework.orm.hibernate5.support.OpenSessionInViewFilter;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.repository.OwnerRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * JPA implementation of the {@link OwnerRepository} interface.
 *
 * @author Mike Keith
 * @author Rod Johnson
 * @author Sam Brannen
 * @author Michael Isvy
 * @since 22.4.2006
 */
@Repository
@Transactional
public class JpaOwnerRepositoryImpl implements OwnerRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Collection<Owner> findByLastName(String lastName) {
        TypedQuery<Owner> query = this.em.createQuery(
                "SELECT DISTINCT owner FROM Owner owner left join fetch owner.pets WHERE owner.lastName LIKE :lastName", 
                Owner.class);
        query.setParameter("lastName", "%" + lastName + "%");
        return query.getResultList();
    }

    @Override
    public Optional<Owner> findById(int id) {
        TypedQuery<Owner> query = this.em.createQuery(
                "SELECT owner FROM Owner owner left join fetch owner.pets WHERE owner.id =:id", 
                Owner.class);
        query.setParameter("id", id);
        return Optional.ofNullable(query.getSingleResult());
    }

    @Override
    public void save(Owner owner) {
        if (owner.getId() == null) {
            this.em.persist(owner);
        } else {
            this.em.merge(owner);
        }
    }

    @Override
    public void delete(Owner owner) {
        if (this.em.contains(owner)) {
            this.em.remove(owner);
        } else {
            Owner managedOwner = this.em.merge(owner);
            this.em.remove(managedOwner);
        }
    }

    @Override
    public Collection<Owner> findAll() {
        TypedQuery<Owner> query = this.em.createQuery(
                "SELECT owner FROM Owner owner left join fetch owner.pets", 
                Owner.class);
        return query.getResultList();
    }
}