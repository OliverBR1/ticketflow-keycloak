package tech.oliver.ticketflowapi.controller.dto;

import org.jspecify.annotations.NonNull;
import tech.oliver.ticketflowapi.entities.TicketComment;

import java.util.List;

public record ReadCommentDto(String id,
                             String authorId,
                             String authorType,
                             String message,
                             String createdAt) {

    public static List<ReadCommentDto> fromEntities(List<TicketComment> comments) {
        return comments.stream()
                .map(ReadCommentDto::fromEntity)
                .toList();
    }

    private static ReadCommentDto fromEntity(TicketComment comment) {
        return new ReadCommentDto(
                comment.getId().toString(),
                comment.getAuthorId().toString(),
                comment.getAuthorType().name(),
                comment.getMessage(),
                comment.getCreatedAt().toString()
        );
    }
}
