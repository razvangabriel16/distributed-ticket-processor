package entities.filters;

import entities.BussinessPriority;
import entities.Ticket;

/**
 * Concrete specification for filtering tickets by their bussiness priority.
 * Implements the Specification pattern for ticket bussiness priority filtering criteria.
 */
public class BusinessPriorityFilter implements Specification<Ticket> {
    private BussinessPriority bussinessPriority;

    public BusinessPriorityFilter(final String bussinessPriority) {
        this.bussinessPriority = BussinessPriority.valueOf(bussinessPriority);
    }

    /**
     * Tests whether a ticket matches the specified bussiness priority.
     * @param ticket the ticket to test
     * @return true if the ticket's bussiness priority equals the filter bussiness priority
     * (case-sensitive), false otherwise
     */
    @Override
    public boolean isSatisfiedBy(final Ticket ticket) {
        return ticket.getBusinessPriority().toString().equals(bussinessPriority.toString());
    }
}
