package tech.oliver.ticketflowapi.controller.dto;

import tech.oliver.ticketflowapi.entities.Ticket;

import java.util.List;

public record ReadTicketDto(String id,
                            String title,
                            String description,
                            String status,
                            String createdAt,
                            String updatedAt,
                            List<ReadCommentDto> comments) {

    public static ReadTicketDto fromEntity(Ticket ticket) {
        var entity = new Ticket();

        return new ReadTicketDto(
                ticket.getId().toString(),
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getStatus().name(),
                ticket.getCreatedAt().toString(),
                ticket.getUpdatedAt().toString(),
                ReadCommentDto.fromEntities(ticket.getComments())
        );
    }
}
