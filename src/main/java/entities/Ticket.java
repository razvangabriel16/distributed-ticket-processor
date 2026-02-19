package entities;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import utils.ErrLogger;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
/**
 *  Abstract base class representing a generic Ticket in the engine
 *  This class serves as the Product in the Factory Method pattern and as
 *  the superclass for all concrete ticket types. Subclasses are required to implement the\
 *  {@link #logic()} method to define ticket-type-specific processing behavior.
 */
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "type",
        "title",
        "businessPriority",
        "status",
        "createdAt",
        "assignedAt",
        "solvedAt",
        "assignedTo",
        "reportedBy",
        "comments"
})
@Getter @Setter
public abstract class Ticket {

    @JsonProperty("id")
    protected int id;
    @JsonProperty("type")
    protected String type;
    @JsonProperty("title")
    protected String title;
    @JsonProperty("businessPriority")
    protected BussinessPriority businessPriority;
    @JsonProperty("status")
    protected Status status;
    @JsonIgnore
    protected ExpertiseArea expertiseArea;
    @JsonIgnore
    protected String description;
    @JsonProperty("reportedBy")
    protected String reportedBy;
    @JsonProperty("assignedAt")
    private String assignedAt;
    @JsonProperty("solvedAt")
    private String solvedAt;
    @JsonProperty("assignedTo")
    private String assignedTo;
    @JsonProperty("comments")
    private List<Comment> comments;
    @JsonProperty("createdAt")
    private String createdAt;
    @Getter @Setter @JsonIgnore
    private int isAssigned;
    @JsonIgnore
    private String firstSolvedAt;
    @Getter
    private int daysToResolve;
    @JsonIgnore
    private BussinessPriority priorityWhenResolved;

    /**
     * Constructs a base Ticket with the mandatory core attributes.
     * This constructor is protected to enforce controlled instantiation
     * through concrete subclasses and factory implementations.
     * @param id unique identifier of the ticket
     * @param type logical ticket type (BUG, UI_FEEDBACK, FEATURE_REQUEST)
     * @param title short title describing
     * @param businessPriority business priority assigned to ticket
     * @param status initial status of the ticket
     * @param expertiseArea required expertise area for handling the ticket
     * @param description detailed ticket description
     * @param reportedBy identifier of the reporter
     */
    protected Ticket(final int id, final String type, final String title,
                     final BussinessPriority businessPriority,
                     final Status status, final ExpertiseArea expertiseArea,
                     final String description, final String reportedBy) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.businessPriority = businessPriority;
        this.status = status;
        this.expertiseArea = expertiseArea;
        this.description = description;
        this.reportedBy = reportedBy;
    }
    /**
     * Updates the ticket status and triggers lifecycle side effects.
     * Records resolution timestamps when the ticket is resolved,
     * Calculates the number of days required to resolve the ticket, and also
     * unblocks dependent milestones when ticket is closed
     * @param newStatus the new status to apply
     */
    public void setStatus(final Status newStatus) {
        Status oldStatus = this.status;
        this.status = newStatus;
        ErrLogger errorLogger = ErrLogger.getInstance();
        if (newStatus == Status.RESOLVED) {
            this.solvedAt = errorLogger.getGlobalTimestamp();
            if (this.firstSolvedAt == null) {
                this.firstSolvedAt = errorLogger.getGlobalTimestamp();
            }
        }
        if (newStatus == Status.RESOLVED || newStatus == Status.CLOSED) {
            if (this.assignedAt != null && !this.assignedAt.isEmpty()
                    && this.solvedAt != null && !this.solvedAt.isEmpty()) {

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate startDate = LocalDate.parse(this.assignedAt, formatter);
                LocalDate endDate = LocalDate.parse(this.solvedAt, formatter);

                long daysDifference = Math.abs(ChronoUnit.DAYS.between(startDate, endDate)) + 1;
                this.daysToResolve = (int) Math.max(0, daysDifference);
            }
        }

        if (newStatus == Status.CLOSED && oldStatus != Status.CLOSED) {
            ErrLogger errLogger = ErrLogger.getInstance();
            List<Milestone> allMilestones = errLogger.getMilestones();

            if (allMilestones != null) {
                Milestone parentMilestone = null;
                for (Milestone milestone : allMilestones) {
                    if (milestone.getTickets() != null) {
                        for (int ticketId : milestone.getTickets()) {
                            if (ticketId == this.id) {
                                parentMilestone = milestone;
                                break;
                            }
                        }
                    }
                    if (parentMilestone != null) {
                        break;
                    }
                }

                if (parentMilestone != null) {
                    parentMilestone.checkAndUnblock(this.id);
                }
            }
        }
    }

    /**
     * Executes ticket-specific logic.This method must be implemented by concrete ticket subclasses
     * to define their custom process behavior.
     */
    public abstract void logic();
    /**
     * @return assignment date as a string or empty string
     */
    @JsonGetter("assignedAt")
    private String getAssignedAtSafe() {
        return assignedAt == null ? "" : assignedAt;
    }
    /**
     * @return resolution date as a string or empty string
     */
    @JsonGetter("solvedAt")
    private String getSolvedAtSafe() {
        return solvedAt == null ? "" : solvedAt;
    }
    /**
     * @return assignment developer as a string or empty string if unassigned
     */
    @JsonGetter("assignedTo")
    private String getAssignedToSafe() {
        return assignedTo == null ? "" : assignedTo;
    }
    /**
     * Returns the list of comments associated with the ticket, handling empty case.
     */
    @JsonGetter("comments")
    private List<Comment> getCommentsSafe() {
        return comments == null ? new ArrayList<>() : comments;
    }
    /**
     * Returns the reporter identifier.
     */
    @JsonGetter("reportedBy")
    public String getReportedBy() {
        return (reportedBy == null && type == "BUG") ? "" : (reportedBy == null ? "" : reportedBy);
    }
    /**
     *Adds a new comment to the ticket.
     *The comment is inserted at the beginning of the comment list, making it the most recent.
     */
    public void addComment(final Comment comment) {
        if (this.comments == null) {
            this.comments = new ArrayList<>();
        }
        this.comments.add(0, comment);
    }
    /**
     * @return the removed comment, or {@code null} if no comments exist
     */
    public Comment removeLastComment() {
        if (this.comments == null || this.comments.isEmpty()) {
            return null;
        }
        return this.comments.remove(0);
    }
    /**
     * Returns the most recent comment without removing it
     */
    public Comment peekLastComment() {
        if (this.comments == null || this.comments.isEmpty()) {
            return null;
        }
        return this.comments.get(0);
    }

    @JsonIgnore
    private List<TicketAction> history = new ArrayList<>();

    /**
     * @param action the ticket action to record
     */
    public void addHistoryAction(final TicketAction action) {
        if (this.history == null) {
            this.history = new ArrayList<>();
        }
        this.history.add(action);
    }
    /**
     * @return list of ticket actions
     */
    public List<TicketAction> getHistory() {
        return history != null ? history : new ArrayList<>();
    }

    /**
     * Removes all history actions that happened after the given timestamp.
     * @param timestamp cutoff date in {@code yyyy-MM-dd} format
     */
    public void clearHistoryAfterTimestamp(final String timestamp) {
        if (history == null) {
            return;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate cutoffDate = LocalDate.parse(timestamp, formatter);

        history.removeIf(action -> {
            LocalDate actionDate = LocalDate.parse(action.getTimestamp(), formatter);
            return actionDate.isAfter(cutoffDate);
        });
    }
}
