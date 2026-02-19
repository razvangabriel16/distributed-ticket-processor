package entities;

/**
 * Concrete factory for creating {@link Bug} tickets.
 * This class acts as a Concrete Creator in the Factory Method pattern
 * and internally uses {@link Bug.Builder} to assemble tickets instances.
 * It combines the Factory Method and Builder design patterns.
 */
public class BugTicketFactory extends TicketFactory {
    private Bug.Builder builder;

    /**
     * Constructs a BugTicketFactory with required Bug attributes.
     */
    public BugTicketFactory(final String expectedBehaviour, final String actualBehaviour,
                            final Frequency frequency, final Severity severity) {
        this.builder = new Bug.Builder(expectedBehaviour, actualBehaviour, frequency, severity);
    }

    /**
     * Sets the given field with the parameter and return an instance of BugTicketFactory
     */
    public BugTicketFactory id(final int id) {
        builder.id(id);
        return this;
    }

    /**
     * Sets the given field with the parameter and return an instance of BugTicketFactory
     */
    public BugTicketFactory title(final String title) {
        builder.title(title);
        return this;
    }

    /**
     * Sets the given field with the parameter and return an instance of BugTicketFactory
     */
    public BugTicketFactory businessPriority(final BussinessPriority priority) {
        builder.businessPriority(priority);
        return this;
    }

    /**
     * Sets the given field with the parameter and return an instance of BugTicketFactory
     */
    public BugTicketFactory status(final Status status) {
        builder.status(status);
        return this;
    }

    /**
     * Sets the given field with the parameter and return an instance of BugTicketFactory
     */
    public BugTicketFactory expertiseArea(final ExpertiseArea area) {
        builder.expertiseArea(area);
        return this;
    }

    /**
     * Sets the given field with the parameter and return an instance of BugTicketFactory
     */
    public BugTicketFactory description(final String description) {
        builder.description(description);
        return this;
    }

    /**
     * Sets the given field with the parameter and return an instance of BugTicketFactory
     */
    public BugTicketFactory reportedBy(final String reportedBy) {
        builder.reportedBy(reportedBy);
        return this;
    }

    /**
     * Sets the given field with the parameter and return an instance of BugTicketFactory
     */
    public BugTicketFactory environment(final String environment) {
        builder.environment(environment);
        return this;
    }

    /**
     * Sets the given field with the parameter and return an instance of BugTicketFactory
     */
    public BugTicketFactory errorCode(final Integer errorCode) {
        builder.errorCode(errorCode);
        return this;
    }

    /**
     * Creates and returns a {@link Bug} ticket using the configured builder
     * @return a fully constructed Bug ticket
     */
    @Override
    public Ticket createTicket() {
        return builder.build();
    }
}
