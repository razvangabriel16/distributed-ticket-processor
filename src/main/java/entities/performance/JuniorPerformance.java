package entities.performance;

import entities.Seniority;
import entities.User;

import static utils.ErrLogger.DBL0_5;

/**
 * Junior class performance caluculation - Specification DP
 */
public class JuniorPerformance implements PerformanceStrategy {
    @Override
    public double performanceScore(final User user, final int month) {
        int closedTickets = this.closedTickets(user, month);
        if (closedTickets == 0) {
            return 0.0;
        }

        int bugs = this.getClosedByType(user, month, "BUG");
        int uiFeeds = this.getClosedByType(user, month, "UI_FEEDBACK");
        int featureReqs = this.getClosedByType(user, month, "FEATURE_REQUEST");

        double ticketDiversityFactor = this.ticketDiversityFactor(bugs, uiFeeds, featureReqs);

        double juniorPerformance = Math.max(0.0, DBL0_5 * closedTickets - ticketDiversityFactor)
                + Seniority.JUNIOR.getWeight();
        return juniorPerformance;
    }
}
