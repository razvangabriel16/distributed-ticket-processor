package entities.filters;

/**
 * Generic interface defining the Specification pattern for filtering objects
 * The Specification pattern allows building complex filter criteria by combning
 * simple predicates using logical operators (AND, OR, NOT).
 * This interface is generic allowing it to be compatible with different entity types:
 * - {@code Specification<Ticket>} for filtering tickets
 * - {@code Specification<User>} for filtering users
 * The pattern enables creating reusable, composable filter criteria following the
 * Open/Closed principle.
 * @param <T> the type of entity being filtered ({@link entities.Ticket} or {@link entities.User})
 */
public interface Specification<T> {
    /**
     * Tests if the given item satisfies the specification criteria.
     * @param item the entity to test against the specification
     * @return true if the item satisfies all criteria, false otherwise
     */
    boolean isSatisfiedBy(T item);

    /**
     *  A new specification that represents the logical AND of this
     *  specification and another specification
     */
    default Specification<T> and(final Specification<T> other) {
        return new AndSpecification<>(this, other);
    }

    /**
     *  A new specification that represents the logical OR of this
     *  specification and another specification
     */
    default Specification<T> or(final Specification<T> other) {
        return new OrSpecification<>(this, other);
    }

    /**
     *  A new specification that represents the logical NOT of this
     *  specification and another specification
     */
    default Specification<T> not() {
        return new NotSpecification<>(this);
    }
}

/**
 * Concrete implementation of Specification that represents a logical AND operation.
 * Combines two specifications and requires both to be satisfied for the composite specification
 * @param <T> the type of entity being filtered
 */
class AndSpecification<T> implements Specification<T> {
    private final Specification<T> left;
    private final Specification<T> right;

    AndSpecification(final Specification<T> left, final Specification<T> right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean isSatisfiedBy(final T item) {
        return left.isSatisfiedBy(item) && right.isSatisfiedBy(item);
    }
}

/**
 * Concrete implementation of Specification that represents a logical OR operation.
 * Combines two specifications and requires both to be satisfied for the composite specification
 * @param <T> the type of entity being filtered
 */
class OrSpecification<T> implements Specification<T> {
    private final Specification<T> left;
    private final Specification<T> right;

    OrSpecification(final Specification<T> left, final Specification<T> right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean isSatisfiedBy(final T item) {
        return left.isSatisfiedBy(item) || right.isSatisfiedBy(item);
    }
}

/**
 * Concrete implementation of Specification that represents a logical NOT operation.
 * Combines two specifications and requires both to be satisfied for the composite specification
 * @param <T> the type of entity being filtered
 */
class NotSpecification<T> implements Specification<T> {
    private final Specification<T> spec;

    NotSpecification(final Specification<T> spec) {
        this.spec = spec;
    }

    @Override
    public boolean isSatisfiedBy(final T item) {
        return !spec.isSatisfiedBy(item);
    }
}
