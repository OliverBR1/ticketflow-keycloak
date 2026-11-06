package tech.oliver.ticketflowapi.entities;

import jakarta.persistence.EnumeratedValue;

public enum AuthorType {

    USER("USER"),
    SUPPORT("SUPPORT");

    AuthorType(String value) {
        this.value = value;
    }

    @EnumeratedValue
    private final String value;

    public String getValue(){
        return value;
    }
}
