package tech.oliver.ticketflowapi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import tech.oliver.ticketflowapi.controller.dto.CreateTicketDto;
import tech.oliver.ticketflowapi.controller.dto.ListTicketDto;
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

    private final TicketRepository ticketRespository;

    public TicketController(TicketRepository ticketRespository) {
        this.ticketRespository = ticketRespository;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('tickets:list', 'own:tickets:list')")
    public ResponseEntity<List<ListTicketDto>> listTickets(@AuthenticationPrincipal Jwt jwt) {

        List<ListTicketDto> tickets = new ArrayList<>();

        if (jwt.getClaimAsStringList("scp").contains("tickets:list")) {
            tickets = ticketRespository.findAll()
                    .stream()
                    .map(ListTicketDto::fromEntity)
                    .toList();
        } else if (jwt.getClaimAsStringList("scp").contains("own:tickets:list")) {

            UUID ownerId = UUID.fromString(jwt.getSubject());

            tickets = ticketRespository.findByOwnerId(ownerId).stream()
                    .map(ListTicketDto::fromEntity)
                    .toList();
        }

        return ResponseEntity.ok(tickets);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('tickets:create')")
    public ResponseEntity <Void> createTicket(@AuthenticationPrincipal Jwt jwt,
                                              @RequestBody CreateTicketDto dto){

        var ticket = new Ticket();
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setTitle(dto.title());
        ticket.setDescription(dto.description());
        ticket.setOwnerId(UUID.fromString(jwt.getSubject()));

        var entity = ticketRespository.save(ticket);

        return ResponseEntity.created(URI.create("/tickets/" + entity.getId())).build();
    }


}
