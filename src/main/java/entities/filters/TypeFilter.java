package entities.filters;

import entities.Ticket;

/**
 * Concrete specification for filtering tickets by their type.
 * Implements the Specification pattern for ticket type filtering criteria.
 */
public class TypeFilter implements Specification<Ticket> {
     private final String type;

    public TypeFilter(final String type) {
        this.type = type;
    }

    /**
     * Tests whether a ticket matches the specified type.
     * @param ticket the ticket to test
     * @return true if the ticket's type equals the filter type (case-sensitive), false otherwise
     */
    @Override
    public boolean isSatisfiedBy(final Ticket ticket) {
        return ticket.getType().equals(type);
    }
}
