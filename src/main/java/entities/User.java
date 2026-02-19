package entities;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * Observer interface used for receiving notification updates
 * Implementing classes are notified through messages when
 * "observable" events occur
 */
interface Observer {
    void update(String message);
}

/**
 * Immutable user entity representing a system participant (REPORTER/ DEV/ MANAGER)
 * This class implements the {@link Observer} interface to receive
 * notifications and uses the Builder design pattern for controlled
 * object creation.
 * @param
 */
@ToString
public final class User implements Observer {
    @Getter
    private String username;
    @Getter
    private String email;
    @Getter
    private String role;
    @Getter
    private String hireDate; //yyyy-mm-dd format
    @Getter
    private ExpertiseArea expertiseArea;
    @Getter
    private Seniority seniority;
    @Getter @Setter
    private String[] subordinates;
    @Getter @Setter
    private List<Ticket> tickets;
    @Getter @Setter
    private List<Ticket> assignedTickets;
    @Getter @Setter
    private double performanceScore;
    @Getter @Setter
    private List<String> notifications;

    /**
     * Receives and stores a notification message
     */
    @Override
    public void update(final String message) {
        notifications.add(message);
    }

    /**
     * Clears all stored notification messages
     */
    public void clearNotifications() {
        if (notifications != null) {
            notifications.clear();
        }
    }


    /**
     * Builder class used to construct {@link User} instances.
     * Mandatory fields are provided via the constructor, while
     * optional fields can be configured through "fluent" methods.
     */
    public static class Builder {
        /* mandatory fields common for all 3 types of user */
        private String username;
        private String email;
        private String role;
        /* type-dependent fields */
        private String hireDate = null;
        private ExpertiseArea expertiseArea = null;
        private Seniority seniority = null;
        private String[] subordinates = null;

        /**
         * Creates a builder with mandatory user attributes.
         * @param usernamee the username of the user
         * @param emaile the email address of the user
         * @param rolee the role of the user
         */
        public Builder(final String usernamee, final String emaile, final String rolee) {
            this.username = usernamee;
            this.email = emaile;
            this.role = rolee;
        }

        /**
         * Sets the hire date of the user.
         * @param hireDatee the hire date in {@code yyyy-MM-dd} format
         * @return the current builder instance
         */
        public Builder hireDate(final String hireDatee) {
            this.hireDate = hireDatee;
            return this;
        }

        /**
         * Same as above
         */
        public Builder subordinates(final String[] subordinatess) {
            this.subordinates = subordinatess;
            return this;
        }

        /**
         * Same as above
         */
        public Builder seniority(final Seniority sseniority) {
            this.seniority = sseniority;
            return this;
        }

        /**
         * Same as above
         */
        public Builder expertiseArea(final ExpertiseArea expertiseAreaa) {
            this.expertiseArea = expertiseAreaa;
            return this;
        }

        /**
         * Builds and returns a new {@link User} instance.
         * @return a fully constructed {@code User} object
         */
        public User build() {
            return new User(this); //this calls the constructor from outside, this is first
        }
    }
    /**
     * Constructs a {@link User} instance from a {@link Builder}
     * This constructor is private to enforce the use of the Builder DesignP
     * @param builder the builder containing user configuration
     */
    private User(final Builder builder) {
        /* this is the second constructor called */
        this.username = builder.username;
        this.email = builder.email;
        this.role = builder.role;
        this.hireDate = builder.hireDate;
        this.expertiseArea = builder.expertiseArea;
        this.seniority = builder.seniority;
        this.subordinates = builder.subordinates;
        this.notifications = new ArrayList<>();
    }
}
