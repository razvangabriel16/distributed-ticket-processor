package entities;

import lombok.Data;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * Concrete implementation of a {@link Ticket} representing UI feedback.
 * This class is a Concrete Product in the Factory Method pattern and
 * is constructed using the {@link Builder} following the Builder pattern.
 */
@ToString(callSuper = true)
public final class UIFeedback extends Ticket {
    private String uiElementID;
    @Getter
    private BusinessValue businessValue;
    @Getter
    private int usabilityScore;
    private String screenshotUrl;
    private String suggestedFix;

    /**
     * Builds and returns a fully initialized {@link UIFeedback} instance
     * @return a new UIFeedback ticket
     */
    private UIFeedback(final Builder builder) {
        super(builder.id, "UI_FEEDBACK", builder.title, builder.businessPriority,
                builder.status, builder.expertiseArea, builder.description, builder.reportedBy);
        this.uiElementID = builder.uiElementID;
        this.businessValue = builder.businessValue;
        this.usabilityScore = builder.usabilityScore;
        this.screenshotUrl = builder.screenshotUrl;
        this.suggestedFix = builder.suggestedFix;
    }

    /**
     * Message printing
     */
    @Override
    public void logic() {
        System.out.println("Processing bug: " + title);
    }

    /**
     * Builder class for constructing immutable {@link UIFeedback} instances.
     * This class implements the Builder design pattern to allow step by step
     * construction of {@link UIFeedback} objects with optional and mandatory fields.
     */
    @Data
    @Accessors(fluent = true, chain = true)
    public static class Builder {
        private int id;
        private String title;
        private BussinessPriority businessPriority;
        private Status status;
        private ExpertiseArea expertiseArea;
        private String description;
        private String reportedBy;

        private String uiElementID;
        private BusinessValue businessValue;
        private int usabilityScore;
        private String screenshotUrl;
        private String suggestedFix;

        public Builder(final BusinessValue businessValue, final int usabilityScore) {
            this.businessValue = businessValue;
            this.usabilityScore = usabilityScore;
        }

        public UIFeedback build() {
            return new UIFeedback(this);
        }
    }
}
