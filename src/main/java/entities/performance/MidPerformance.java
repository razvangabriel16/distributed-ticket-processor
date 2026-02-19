package entities.performance;

import entities.Seniority;
import entities.User;

import java.util.stream.Collectors;

import static utils.ErrLogger.*;

/**
 * Mid class performance caluculation - Specification DP
 */
public class MidPerformance implements PerformanceStrategy {
    @Override
    public double performanceScore(final User user, final int month) {
        int closedTickets = this.closedTickets(user, month);
        if (closedTickets == 0) {
            return 0.0;
        }
        int highPriorityTickets = this.closedPertinentTickets(user, month).stream()
                .filter(ticket -> ticket.getBusinessPriority().toString().equals("CRITICAL")
                        || ticket.getBusinessPriority().toString().equals("HIGH"))
                .collect(Collectors.toList())
                .size();
        double avgResolutionTime = this.averageResolutionTime(user, month);
        double midPerformance = Math.max(0.0, DBL0_5 * closedTickets + DBL0_7
                                * highPriorityTickets - DBL0_3 * avgResolutionTime)
                                + Seniority.MID.getWeight();
        return midPerformance;
    }

}
