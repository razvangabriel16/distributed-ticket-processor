package entities.filters;

import entities.Ticket;

/**
 * Concrete specification for filtering tickets by their availability.
 * Implements the Specification pattern for ticket availability filtering criteria.
 */
public class AvailableForAssignmentFilter implements Specification<Ticket> {
    private int isAvailable;

    public AvailableForAssignmentFilter(final boolean isAvailable) {
        if (isAvailable == true) {
            this.isAvailable = 1;
        } else {
            this.isAvailable = 0;
        }
    }

    /**
     * Tests whether a ticket matches the specified availability
     * @param ticket the ticket's availability to test
     * @return true if the ticket's  availability equals the filter availability, false otherwise
     */
    @Override
    public boolean isSatisfiedBy(final Ticket ticket) {
        return ticket.getIsAssigned() == isAvailable;
    }
}

