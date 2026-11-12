package tech.oliver.ticketflowapi.service;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import tech.oliver.ticketflowapi.controller.dto.UpdateTicketStatusDto;
import tech.oliver.ticketflowapi.entities.Ticket;
import tech.oliver.ticketflowapi.entities.TicketStatus;

@Service
public class TicketService {

    public boolean isTheTicketOwner(Jwt jwt, Ticket ticket) {
        return jwt.getClaimAsStringList("scp").contains("own:tickets:read") &&
                ticket.getOwnerId().toString().equals(jwt.getSubject());
    }
    public boolean isAllowedToReadAllTickets(Jwt jwt) {
        return jwt.getClaimAsStringList("scp").contains("tickets:read");
    }

    public boolean canMarkAsSolvedOrReject(UpdateTicketStatusDto dto, Ticket ticket) {
        return ticket.getStatus() == TicketStatus.IN_PROGRESS &&
                (dto.status() == TicketStatus.SOLVED || dto.status() == TicketStatus.REJECTED);
    }
    public boolean canMoveToInProgress(UpdateTicketStatusDto dto, Ticket ticket) {
        return ticket.getStatus() == TicketStatus.OPEN && dto.status() == TicketStatus.IN_PROGRESS;
    }
}
