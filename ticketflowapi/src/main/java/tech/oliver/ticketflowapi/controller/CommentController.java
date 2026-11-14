package tech.oliver.ticketflowapi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import tech.oliver.ticketflowapi.controller.dto.AddCommentDto;
import tech.oliver.ticketflowapi.repository.TicketCommentRepository;
import tech.oliver.ticketflowapi.repository.TicketRepository;
import tech.oliver.ticketflowapi.service.TicketCommentService;

import java.util.UUID;

@RestController
public class CommentController {

    private final TicketCommentService ticketCommentService;

    public CommentController(TicketCommentService ticketCommentService) {
        this.ticketCommentService = ticketCommentService;
    }

    @PostMapping("/tickets/{ticketId}/comments")
    @PreAuthorize("hasAnyAuthority('tickets-comments:create', 'own:tickets-comments:create')")
    public ResponseEntity<Void> addCommentToTicket(@AuthenticationPrincipal Jwt jwt,
                                                   @PathVariable UUID ticketId,
                                                   @RequestBody AddCommentDto dto) {
        ticketCommentService.addComment(jwt, ticketId, dto);
        return ResponseEntity.ok().build();
    }
}
