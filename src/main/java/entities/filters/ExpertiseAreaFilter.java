package entities.filters;

import entities.ExpertiseArea;
import entities.User;

/**
 * Concrete specification for filtering users by their expertiseArea.
 * Implements the Specification pattern for user expertiseArea filtering criteria.
 */
public class ExpertiseAreaFilter implements Specification<User> {
    private final ExpertiseArea type;
    public ExpertiseAreaFilter(final String type) {
        this.type = ExpertiseArea.valueOf(type);
    }
    /**
     * Tests whether a user matches the specified expertiseArea.
     * @param user the ticket to test
     * @return true if the user's expertiseArea equals the filter expertiseArea, false otherwise
     */
    @Override
    public boolean isSatisfiedBy(final User user) {
        return type.toString().equals(user.getExpertiseArea().toString());
    }
}
