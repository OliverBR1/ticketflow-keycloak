package tech.oliver.ticketflowapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.oliver.ticketflowapi.entities.TicketComment;

import java.util.UUID;

public interface TicketCommentRepository extends JpaRepository<TicketComment, UUID> {

}
