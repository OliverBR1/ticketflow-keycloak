package tech.oliver.ticketflowapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.oliver.ticketflowapi.entities.Ticket;

import java.util.List;
import java.util.UUID;

public interface TicketRepository extends JpaRepository<Ticket, UUID> {

    List<Ticket> findByOwnerId(UUID ownerId);
}
