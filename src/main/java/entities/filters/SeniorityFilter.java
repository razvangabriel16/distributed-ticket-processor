package entities.filters;

import entities.Seniority;
import entities.User;

/**
 * Concrete specification for filtering users by their seniority.
 * Implements the Specification pattern for user seniority filtering criteria.
 */
public class  SeniorityFilter implements Specification<User> {
    private final Seniority type;

    public SeniorityFilter(final String type) {
        this.type = Seniority.valueOf(type);
    }

    /**
     * Tests whether a user matches the specified seniority.
     * Used this equal check for increased support in cases of the assigning of not an unique enum.
     * @param user the ticket to test
     * @return true if the user's seniority equals the user type, false otherwise
     */
    @Override
    public boolean isSatisfiedBy(final User user) {
        return type.toString().equals(user.getSeniority().toString());
    }
}
