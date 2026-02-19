package entities.filters;

import entities.Ticket;
import java.time.LocalDate;

/**
 * Concrete specification for filtering tickets by their creation date.
 * Implements the Specification pattern for ticket creation date filtering criteria.
 */
public class CreatedBeforeFilter implements Specification<Ticket> {

    private final LocalDate referenceDate;
    public CreatedBeforeFilter(final String date) {
        this.referenceDate = LocalDate.parse(date);
    }

    /**
     * Tests whether a ticket matches the specified type.
     * @param ticket the ticket to test
     * @return true if the ticket's creation date before the filter creation date, false otherwise
     */
    @Override
    public boolean isSatisfiedBy(final Ticket ticket) {
        LocalDate ticketDate = LocalDate.parse(ticket.getCreatedAt());
        return ticketDate.isBefore(referenceDate);
    }
}
