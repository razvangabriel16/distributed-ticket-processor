package entities;

import lombok.Data;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * Concrete implementation of a {@link Ticket} representing FeatureRequest.
 * This class is a Concrete Product in the Factory Method pattern and
 * is constructed using the {@link FeatureRequest.Builder} following the Builder pattern.
 */
@ToString(callSuper = true)
public final class FeatureRequest extends Ticket {
    @Getter
    private BusinessValue bussinessValue;
    @Getter
    private CustomerDemand customerDemand;

    /**
     * Builds and returns a fully initialized {@link FeatureRequest} instance
     * @return a new FeatureRequest ticket
     */
    private FeatureRequest(final Builder builder) {
        super(builder.id, "FEATURE_REQUEST", builder.title, builder.businessPriority,
                builder.status, builder.expertiseArea, builder.description, builder.reportedBy);
        this.bussinessValue = builder.bussinessValue;
        this.customerDemand = builder.customerDemand;
    }

    /**
     * Message printing
     */
    @Override
    public void logic() {
        System.out.println("Processing bug: " + title);
    }

    /**
     * Builder class for constructing immutable {@link FeatureRequest} instances.
     * This class implements the Builder design pattern to allow step by step
     * construction of {@link FeatureRequest} objects with optional and mandatory fields.
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

        private BusinessValue bussinessValue;
        private CustomerDemand customerDemand;


        public Builder(final BusinessValue bussinessValue,
                       final CustomerDemand customerDemand) {
            this.bussinessValue = bussinessValue;
            this.customerDemand = customerDemand;
        }

        public FeatureRequest build() {
            return new FeatureRequest(this);
        }
    }
}
