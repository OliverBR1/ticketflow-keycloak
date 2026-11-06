package tech.oliver.ticketflowapi.entities;

import jakarta.persistence.EnumeratedValue;

public enum TicketStatus {
    OPEN("OPEN"),
    IN_PROGRESS("IN_PROGRESS"),
    SOLVED("SOLVED"),
    REJECTED("REJECTED")
    ;

    TicketStatus(String value) {
        this.value = value;
    }

    @EnumeratedValue
    private final String value;

    public String getValue(){
        return value;
    }
}
