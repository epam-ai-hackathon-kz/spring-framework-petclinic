package org.springframework.samples.petclinic.service;

public class OwnerNotFoundException extends RuntimeException {

    public OwnerNotFoundException(String message) {
        super(message);
    }

}
