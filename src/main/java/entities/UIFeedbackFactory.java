package entities;

/**
 * Concrete factory for creating {@link UIFeedback} tickets.
 * This class acts as a Concrete Creator in the Factory Method pattern
 * and internally uses {@link UIFeedback.Builder} to assemble tickets instances.
 * It combines the Factory Method and Builder design patterns.
 */
public class UIFeedbackFactory extends TicketFactory {
    private final UIFeedback.Builder builder;

    /**
     * Constructs a UIFeedbackFactory with required UI feedback attributes.
     * @param businessValue the business value of the UI feedback
     * @param usabilityScore the usability score of the feedback
     */
    public UIFeedbackFactory(final BusinessValue businessValue,
                             final int usabilityScore) {
        this.builder = new UIFeedback.Builder(businessValue, usabilityScore);
    }

    /**
     * Sets the given field with the parameter and return an instance of UIFeedbackFactory
     */
    public UIFeedbackFactory id(final int id) {
        builder.id(id);
        return this;
    }

    /**
     * Sets the given field with the parameter and return an instance of UIFeedbackFactory
     */
    public UIFeedbackFactory title(final String title) {
        builder.title(title);
        return this;
    }

    /**
     * Sets the given field with the parameter and return an instance of UIFeedbackFactory
     */
    public UIFeedbackFactory businessPriority(final BussinessPriority priority) {
        builder.businessPriority(priority);
        return this;
    }

    /**
     * Sets the given field with the parameter and return an instance of UIFeedbackFactory
     */
    public UIFeedbackFactory status(final Status status) {
        builder.status(status);
        return this;
    }

    /**
     * Sets the given field with the parameter and return an instance of UIFeedbackFactory
     */
    public UIFeedbackFactory expertiseArea(final ExpertiseArea area) {
        builder.expertiseArea(area);
        return this;
    }

    /**
     * Sets the given field with the parameter and return an instance of UIFeedbackFactory
     */
    public UIFeedbackFactory description(final String description) {
        builder.description(description);
        return this;
    }

    /**
     * Sets the given field with the parameter and return an instance of UIFeedbackFactory
     */
    public UIFeedbackFactory reportedBy(final String reportedBy) {
        builder.reportedBy(reportedBy);
        return this;
    }

    /**
     * Sets the given field with the parameter and return an instance of UIFeedbackFactory
     */
    public UIFeedbackFactory screenshotUrl(final String screenshotUrl) {
        builder.screenshotUrl(screenshotUrl);
        return this;
    }

    /**
     * Sets the given field with the parameter and return an instance of UIFeedbackFactory
     */
    public UIFeedbackFactory suggestedFix(final String suggestedFix) {
        builder.suggestedFix(suggestedFix);
        return this;
    }

    /**
     * Sets the given field with the parameter and return an instance of UIFeedbackFactory
     */
    public UIFeedbackFactory uiElementId(final String uiElementId) {
        builder.uiElementID(uiElementId);
        return this;
    }

    /**
     * Creates and returns a {@link UIFeedback} ticket using the configured builder
     * @return a fully constructed UIFeedback ticket
     */
    @Override
    public Ticket createTicket() {
        return builder.build();
    }
}
