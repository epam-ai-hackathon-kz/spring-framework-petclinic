package org.springframework.samples.petclinic.service;

import java.util.Collection;

import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.model.Vet;
import org.springframework.samples.petclinic.model.Visit;
import org.springframework.samples.petclinic.model.VetSchedule;


/**
 * Mostly used as a facade so all controllers have a single point of entry
 *
 * @author Michael Isvy
 */
public interface ClinicService {

    Collection<PetType> findPetTypes();

    Owner findOwnerById(int id);

    Pet findPetById(int id);

    void savePet(Pet pet);

    void saveVisit(Visit visit);

    Collection<Vet> findVets();

    void saveOwner(Owner owner);

    Collection<Owner> findByLastName(String lastName);

    Collection<Visit> findVisitsByPetId(int petId);

    Collection<Pet> findPetsByOwnerName(String ownerName);

    Collection<VetSchedule> findVetsWithSchedules();

    // Update to use findByLastName
    default Collection<Owner> findOwnersByLastName(String lastName) {
        return findByLastName(lastName);
    }

}