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
package org.springframework.samples.petclinic.repository.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.model.Visit;
import org.springframework.samples.petclinic.repository.OwnerRepository;
import org.springframework.samples.petclinic.util.EntityUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.List;

/**
 * A simple JDBC-based implementation of the {@link OwnerRepository} interface.
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @author Sam Brannen
 * @author Thomas Risberg
 * @author Mark Fisher
 * @author Antoine Rey
 */
@Repository
public class JdbcOwnerRepositoryImpl implements OwnerRepository {

    private static final Logger logger = LoggerFactory.getLogger(JdbcOwnerRepositoryImpl.class);

    private final JdbcClient jdbcClient;

    private final SimpleJdbcInsert insertOwner;

    @Autowired
    public JdbcOwnerRepositoryImpl(DataSource dataSource, JdbcClient jdbcClient) {

        this.insertOwner = new SimpleJdbcInsert(dataSource)
            .withTableName("owners")
            .usingGeneratedKeyColumns("id");

        this.jdbcClient = jdbcClient;

    }

    @Override
    public Collection<Owner> findByLastName(String lastName) {
        List<Owner> owners = this.jdbcClient.sql("""
                SELECT id, first_name, last_name, address, city, telephone
                FROM owners
                WHERE last_name LIKE :lastName
                """)
            .param("lastName", lastName + "%")
            .query(BeanPropertyRowMapper.newInstance(Owner.class))
            .list();
        loadOwnersPetsAndVisits(owners);
        return owners;
    }

    @Override
    public Owner findById(int id) {
        Owner owner;
        try {
            owner = this.jdbcClient.sql("""
                    SELECT id, first_name, last_name, address, city, telephone
                    FROM owners WHERE id = :id
                    """)
                .param("id", id)
                .query(BeanPropertyRowMapper.newInstance(Owner.class))
                .single();
        } catch (EmptyResultDataAccessException ex) {
            throw new ObjectRetrievalFailureException(Owner.class, id);
        }
        loadPetsAndVisits(owner);
        return owner;
    }

    public void loadPetsAndVisits(final Owner owner) {
        final List<JdbcPet> pets = this.jdbcClient.sql("""
            SELECT pets.id, name, birth_date, type_id, owner_id, visits.id as visit_id, description, pet_id
            FROM pets LEFT OUTER JOIN visits ON pets.id = pet_id
            WHERE owner_id=:id ORDER BY pet_id
            """)
            .param("id", owner.getId())
            .query(new JdbcPetVisitExtractor());
        Collection<PetType> petTypes = getPetTypes();
        for (JdbcPet pet : pets) {
            pet.setType(EntityUtils.getById(petTypes, PetType.class, pet.getTypeId()));
            owner.addPet(pet);
        }
    }

    @Override
    public void save(Owner owner) {
        BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(owner);
        try {
            if (owner.isNew()) {
                Number newKey = this.insertOwner.executeAndReturnKey(parameterSource);
                owner.setId(newKey.intValue());
            } else {
                this.jdbcClient.sql("""
                        UPDATE owners
                        SET first_name=:firstName, last_name=:lastName, address=:address, city=:city, telephone=:telephone
                        WHERE id=:id
                        """)
                    .paramSource(parameterSource)
                    .update();
            }
        } catch (Exception ex) {
            logger.error("Failed to save owner: " + owner, ex);
            throw new DataAccessException("Failed to save owner: " + owner, ex) {};
        }
    }

    public Collection<PetType> getPetTypes() {
        return this.jdbcClient.sql("SELECT id, name FROM types ORDER BY name")
            .query(BeanPropertyRowMapper.newInstance(PetType.class))
            .list();
    }

    private void loadOwnersPetsAndVisits(List<Owner> owners) {
        for (Owner owner : owners) {
            loadPetsAndVisits(owner);
        }
    }
}