package entities;

import lombok.ToString;
import lombok.Getter;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Concrete implementation of a {@link Ticket} representing Bug.
 * This class is a Concrete Product in the Factory Method pattern and
 * is constructed using the {@link Bug.Builder} following the Builder pattern.
 */
@ToString(callSuper = true)
public final class Bug extends Ticket {
    private String expectedBehaviour;
    private String actualBehaviour;
    @Getter
    private Frequency frequency;
    @Getter
    private Severity severity;
    private String environment;
    private Integer errorCode;

    private Bug(final Builder builder) {
        super(builder.id, "BUG", builder.title, builder.businessPriority,
                builder.status, builder.expertiseArea, builder.description, builder.reportedBy);
        this.expectedBehaviour = builder.expectedBehaviour;
        this.actualBehaviour = builder.actualBehaviour;
        this.frequency = builder.frequency;
        this.severity = builder.severity;
        this.environment = builder.environment;
        this.errorCode = builder.errorCode;
    }

    @Override
    public void logic() {
        System.out.println("Processing bug: " + title);
    }

    /**
     * Builder class for constructing immutable {@link Bug} instances.
     * This class implements the Builder design pattern to allow step by step
     * construction of {@link Bug} objects with optional and mandatory fields.
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

        private final String expectedBehaviour;
        private final String actualBehaviour;
        private final Frequency frequency;
        private final Severity severity;

        private String environment;
        private Integer errorCode;

        public Builder(final String expectedBehaviour, final String actualBehaviour,
                       final Frequency frequency, final Severity severity) {
            this.expectedBehaviour = expectedBehaviour;
            this.actualBehaviour = actualBehaviour;
            this.frequency = frequency;
            this.severity = severity;
        }

        public Bug build() {
            return new Bug(this);
        }
    }
}
