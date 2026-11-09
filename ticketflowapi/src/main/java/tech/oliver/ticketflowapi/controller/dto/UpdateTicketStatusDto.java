package tech.oliver.ticketflowapi.controller.dto;

import tech.oliver.ticketflowapi.entities.TicketStatus;

public record UpdateTicketStatusDto(TicketStatus status) {
}
