package entities;

/**
 *Yoy
 * @param
 */
public class FeatureRequestFactory extends TicketFactory {
    private FeatureRequest.Builder builder;

    public FeatureRequestFactory(final BusinessValue bussinessValue,
                                 final CustomerDemand customerDemand) {
        this.builder = new FeatureRequest.Builder(bussinessValue, customerDemand);
    }

    /**
     * Sets the given field with the parameter and return an instance of FeatureRequestFactory
     */
    public FeatureRequestFactory id(final int id) {
        builder.id(id);
        return this;
    }

    /**
     * Sets the given field with the parameter and return an instance of FeatureRequestFactory
     */
    public FeatureRequestFactory title(final String title) {
        builder.title(title);
        return this;
    }

    /**
     * Sets the given field with the parameter and return an instance of FeatureRequestFactory
     */
    public FeatureRequestFactory businessPriority(final BussinessPriority priority) {
        builder.businessPriority(priority);
        return this;
    }

    /**
     * Sets the given field with the parameter and return an instance of FeatureRequestFactory
     */
    public FeatureRequestFactory status(final Status status) {
        builder.status(status);
        return this;
    }

    /**
     * Sets the given field with the parameter and return an instance of FeatureRequestFactory
     */
    public FeatureRequestFactory expertiseArea(final ExpertiseArea area) {
        builder.expertiseArea(area);
        return this;
    }

    /**
     * Sets the given field with the parameter and return an instance of FeatureRequestFactory
     */
    public FeatureRequestFactory description(final String description) {
        builder.description(description);
        return this;
    }

    /**
     * Sets the given field with the parameter and return an instance of FeatureRequestFactory
     */
    public FeatureRequestFactory reportedBy(final String reportedBy) {
        builder.reportedBy(reportedBy);
        return this;
    }

    /**
     * Creates and returns a {@link FeatureRequest} ticket using the configured builder
     * @return a fully constructed FeatureRequest ticket
     */
    @Override
    public Ticket createTicket() {
        return builder.build();
    }
}
