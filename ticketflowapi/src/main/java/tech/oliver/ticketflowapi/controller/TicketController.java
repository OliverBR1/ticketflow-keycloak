package tech.oliver.ticketflowapi.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tech.oliver.ticketflowapi.controller.dto.CreateTicketDto;
import tech.oliver.ticketflowapi.controller.dto.ListTicketDto;
import tech.oliver.ticketflowapi.controller.dto.ReadTicketDto;
import tech.oliver.ticketflowapi.controller.dto.UpdateTicketStatusDto;
import tech.oliver.ticketflowapi.entities.Ticket;
import tech.oliver.ticketflowapi.entities.TicketStatus;
import tech.oliver.ticketflowapi.repository.TicketRepository;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tickets")
public class TicketController {

    private final TicketRepository ticketRepository;

    public TicketController(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('tickets:list', 'own:tickets:list')")
    public ResponseEntity<List<ListTicketDto>> listTickets(@AuthenticationPrincipal Jwt jwt) {

        List<ListTicketDto> tickets = new ArrayList<>();

        if (jwt.getClaimAsStringList("scp").contains("tickets:list")) {

            tickets = ticketRepository.findAll()
                    .stream()
                    .map(ListTicketDto::fromEntity)
                    .toList();

        } else if (jwt.getClaimAsStringList("scp").contains("own:tickets:list")) {

            UUID ownerId = UUID.fromString(jwt.getSubject());

            tickets = ticketRepository.findByOwnerId(ownerId).stream()
                    .map(ListTicketDto::fromEntity)
                    .toList();
        }

        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('tickets:read', 'own:tickets:read')")
    public ResponseEntity<ReadTicketDto> getTicket(@PathVariable UUID id,
                                                   @AuthenticationPrincipal Jwt jwt) {
        var ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found"));

        if (isAllowedToReadAllTickets(jwt) || isTheTicketOwner(jwt, ticket)) {

            return ResponseEntity.ok(ReadTicketDto.fromEntity(ticket));

        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied to this ticket");
        }
    }

    @PostMapping
    @PreAuthorize("hasAuthority('tickets:create')")
    public ResponseEntity<Void> createTicket(@AuthenticationPrincipal Jwt jwt,
                                             @RequestBody CreateTicketDto dto) {
        var ticket = new Ticket();
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setTitle(dto.title());
        ticket.setDescription(dto.description());
        ticket.setOwnerId(UUID.fromString(jwt.getSubject()));

        var entity = ticketRepository.save(ticket);
        return ResponseEntity.created(URI.create("/tickets/" + entity.getId())).build();
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('tickets-status:update')")
    public ResponseEntity<Void> updateTicketStatus(@PathVariable UUID id,
                                                   @AuthenticationPrincipal Jwt jwt,
                                                   @RequestBody UpdateTicketStatusDto dto) {

        var ticketOpt = ticketRepository.findById(id);

        if (ticketOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var ticket = ticketOpt.get();

        if (canMoveToInProgress(dto, ticket) || canMarkAsSolvedOrReject(dto, ticket)) {

            ticket.setStatus(dto.status());
            ticketRepository.save(ticket);

        } else {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_CONTENT);
        }
        return ResponseEntity.noContent().build();
    }


    private boolean isTheTicketOwner(Jwt jwt, Ticket ticket) {
        return jwt.getClaimAsStringList("scp").contains("own:tickets:read") &&
                ticket.getOwnerId().toString().equals(jwt.getSubject());
    }
    private boolean isAllowedToReadAllTickets(Jwt jwt) {
        return jwt.getClaimAsStringList("scp").contains("tickets:read");
    }

    private boolean canMarkAsSolvedOrReject(UpdateTicketStatusDto dto, Ticket ticket) {
        return ticket.getStatus() == TicketStatus.IN_PROGRESS &&
                (dto.status() == TicketStatus.SOLVED || dto.status() == TicketStatus.REJECTED);
    }
    private boolean canMoveToInProgress(UpdateTicketStatusDto dto, Ticket ticket) {
        return ticket.getStatus() == TicketStatus.OPEN && dto.status() == TicketStatus.IN_PROGRESS;
    }
}
