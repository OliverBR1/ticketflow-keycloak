package tech.oliver.ticketflowapi.controller.dto;

import tech.oliver.ticketflowapi.entities.AuthorType;
import tech.oliver.ticketflowapi.entities.Ticket;
import tech.oliver.ticketflowapi.entities.TicketComment;

import java.util.UUID;

public record AddCommentDto(String message) {

    public TicketComment toEntity(Ticket ticket,
                                  AuthorType authorType,
                                  String authorId) {

        var entity = new TicketComment();

        entity.setAuthorId(UUID.fromString(authorId));
        entity.setMessage(message);
        entity.setTicketId(ticket);
        entity.setAuthorType(authorType);

        return entity;
    }
}
