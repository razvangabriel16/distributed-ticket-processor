package entities.filters;

import entities.User;

/**
 * Concrete specification for filtering users by their performanceScore.
 * Implements the Specification pattern for user performanceScore filtering criteria.
 */
public class PerformanceScoreBelowFilter implements Specification<User> {
    private final double threshold;

    public PerformanceScoreBelowFilter(final double threshold) {
        this.threshold = threshold;
    }

    /**
     * Tests whether a user matches the specified performanceScore reqs.
     * @param user the ticket to test
     * @return true if the user's performanceScore is below the threshold, false otherwise
     */
    @Override
    public boolean isSatisfiedBy(final User user) {
        return user.getPerformanceScore() <= threshold;
    }
}
