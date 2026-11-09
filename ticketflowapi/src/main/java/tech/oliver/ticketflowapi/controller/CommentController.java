package tech.oliver.ticketflowapi.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import tech.oliver.ticketflowapi.controller.dto.AddCommentDto;
import tech.oliver.ticketflowapi.entities.AuthorType;
import tech.oliver.ticketflowapi.entities.TicketStatus;
import tech.oliver.ticketflowapi.repository.TicketCommentRepository;
import tech.oliver.ticketflowapi.repository.TicketRepository;

import java.util.UUID;

@RestController
public class CommentController {

    private final TicketRepository ticketRepository;
    private final TicketCommentRepository ticketCommentRepository;

    public CommentController(TicketRepository ticketRepository,
                             TicketCommentRepository ticketCommentRepository) {
        this.ticketRepository = ticketRepository;
        this.ticketCommentRepository = ticketCommentRepository;
    }

    @PostMapping(value = "/tickets/{ticketId}/comments")
    @PreAuthorize("hasAnyAuthority('tickets-comments:create', 'own:tickets-comments:create')")
    public ResponseEntity<Void> addCommentToTicket(@AuthenticationPrincipal Jwt jwt,
                                                   @PathVariable("ticketId") UUID ticketId,
                                                   @RequestBody AddCommentDto dto) {
        var ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found"));

        if (ticket.getStatus() != TicketStatus.IN_PROGRESS) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_CONTENT, "Ticket is not in progress");
        }

        if (jwt.getClaimAsStringList("scp").contains("tickets-comments:create")) {

            var comment = dto.toEntity(ticket, AuthorType.SUPPORT, jwt.getSubject());
            ticketCommentRepository.save(comment);

        } else if (jwt.getClaimAsStringList("scp").contains("own:tickets-comments:create")) {

            var userId = UUID.fromString(jwt.getSubject());

            if (!ticket.getOwnerId().equals(userId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the owner of this ticket");
            }

            var comment = dto.toEntity(ticket, AuthorType.USER, jwt.getSubject());
            ticketCommentRepository.save(comment);

        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to add comments to this ticket");
        }

        return ResponseEntity.ok().build();
    }
}
