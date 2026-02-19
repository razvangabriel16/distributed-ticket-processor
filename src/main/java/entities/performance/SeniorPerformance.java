package entities.performance;

import entities.Seniority;
import entities.User;

import java.util.stream.Collectors;

import static utils.ErrLogger.DBL0_5;

/**
 * Senior class performance caluculation - Specification DP
 */
public class SeniorPerformance implements PerformanceStrategy {
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
        double seniorPerformance = Math.max(0.0, DBL0_5 * closedTickets + 1.0
                * highPriorityTickets - DBL0_5 * avgResolutionTime)
                + Seniority.SENIOR.getWeight();
        return seniorPerformance;
    }

}
