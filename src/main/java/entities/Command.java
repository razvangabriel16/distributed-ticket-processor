package entities;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import entities.filters.BusinessPriorityFilter;
import entities.filters.CreatedAfterFilter;
import entities.filters.CreatedBeforeFilter;
import entities.filters.CreatedAtFilter;
import entities.filters.ExpertiseAreaFilter;
import entities.filters.Filter;
import entities.filters.FilterParser;
import entities.filters.KeywordFilter;
import entities.filters.PerformanceScoreAboveFilter;
import entities.filters.PerformanceScoreBelowFilter;
import entities.filters.SeniorityFilter;
import entities.filters.TypeFilter;
import entities.filters.Specification;
import entities.metrics.CustomerImpact;
import entities.metrics.EfficiencyType;
import entities.metrics.MetricsManager;
import entities.metrics.MetricStrategy;
import entities.metrics.TicketRisk;
import entities.performance.JuniorPerformance;
import entities.performance.MidPerformance;
import entities.performance.PerformanceManager;
import entities.performance.SeniorPerformance;
import lombok.Data;
import utils.ErrLogger;
import utils.PHASE;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.Comparator;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Collections;
import java.util.Arrays;

import static main.App.MAPPER;
import static utils.ErrLogger.INT10;
import static utils.ErrLogger.INT12;

/**
 * Class that represents a command issued by a user in the engine
 * Helps parsing input commands and unpacking ticket parameters, creating and manipulating
 * tickets of various types generating structured JSON views for tickets, milestones,
 * searches, and reports, enforcing role-based access control for managers, developers,
 * and reporters, handling ticket lifecycle operations such as assignment, status changes
 * comments, and undo actions
 * @see <a href="https://ocw.cs.pub.ro/courses/poo-ca-cd/teme/2025/b73f56dc-17a1-42ac-bd7e-d57f3caaf9fd/tema-2">
 *      Engine FUll Documentation Rules
 *      </a>
 */
@Data
public class Command {

     private String command;
     private String username;
     private String timestamp;
     private Ticket ticket;
     private String error;

    /**
     * Unpacks parameters from a JSON node and creates a corresponding {@link Ticket}
     * based on the specified ticket type.
     * @param node the JSON node containing ticket data and type information
     */
    public void unpackParams(final JsonNode node) {
         ErrLogger errorLogger = ErrLogger.getInstance();
         switch (node.get("type").asText()) {
             case "BUG":
                 BugTicketFactory bugFactory = new BugTicketFactory(
                         node.has("expectedBehavior")
                                 ? node.get("expectedBehavior").asText() : null,
                         node.has("actualBehavior")
                                 ? node.get("actualBehavior").asText() : null,
                         node.has("frequency")
                                 ? Frequency.valueOf(node.get("frequency").asText()) : null,
                         node.has("severity")
                                 ? Severity.valueOf(node.get("severity").asText()) : null
                 );
                 this.ticket = bugFactory
                         .id(errorLogger.getTicketIdGlobal())
                         .title(node.has("title")
                                 ? node.get("title").asText() : null)
                         .businessPriority(node.has("businessPriority")
                                 ? BussinessPriority
                                    .valueOf(node.get("businessPriority").asText()) : null)
                         .status(node.has("status")
                                 ? Status.valueOf(node.get("status").asText()) : null)
                         .expertiseArea(node.has("expertiseArea")
                                 ? ExpertiseArea.valueOf(node.get("expertiseArea").asText()) : null)
                         .description(node.has("description")
                                 ? node.get("description").asText() : null)
                         .reportedBy(node.has("reportedBy")
                                 && !node.get("reportedBy").asText().isEmpty()
                                 ? node.get("reportedBy").asText()
                                 : null)
                         .environment(node.has("environment")
                                 ? node.get("environment").asText() : null)
                         .errorCode(node.has("errorCode")
                                 ? Integer.parseInt(node.get("errorCode").asText()) : -1)
                         .createTicket();
                 break;
             case "FEATURE_REQUEST":
                 FeatureRequestFactory featureRequestFactory = new FeatureRequestFactory(
                         node.has("businessValue")
                                 ? BusinessValue.valueOf(node.get("businessValue").asText())
                                 : null,
                         node.has("customerDemand")
                                 ? CustomerDemand.valueOf(node.get("customerDemand").asText())
                                 : null
                 );
                 this.ticket = featureRequestFactory
                         .id(errorLogger.getTicketIdGlobal())
                         .title(node.has("title") ? node.get("title").asText() : null)
                         .businessPriority(node.has("businessPriority")
                                 ? BussinessPriority.valueOf(node.get("businessPriority").asText())
                                 : null)
                         .status(node.has("status")
                                 ? Status.valueOf(node.get("status").asText())
                                 : null)
                         .expertiseArea(node.has("expertiseArea")
                                 ? ExpertiseArea.valueOf(node.get("expertiseArea").asText())
                                 : null)
                         .description(node.has("description")
                                 ? node.get("description").asText() : null)
                         .reportedBy(node.has("reportedBy")
                                 && !node.get("reportedBy").asText().isEmpty()
                                 ? node.get("reportedBy").asText()
                                 : null)
                         .createTicket();
                 break;
             case  "UI_FEEDBACK":
                 UIFeedbackFactory uiFeedbackFactory = new UIFeedbackFactory(
                         node.has("businessValue")
                                 ? BusinessValue.valueOf(node.get("businessValue").asText()) : null,
                         node.has("usabilityScore")
                                 ? Integer.parseInt(node.get("usabilityScore").asText()) : -1
                 );
                 this.ticket = uiFeedbackFactory
                         .id(errorLogger.getTicketIdGlobal())
                         .title(node.has("title")
                                 ? node.get("title").asText() : null)
                         .businessPriority(node.has("businessPriority")
                                 ? BussinessPriority.valueOf(node.get("businessPriority").asText())
                                 : null)
                         .status(node.has("status")
                                 ? Status.valueOf(node.get("status").asText()) : null)
                         .expertiseArea(node.has("expertiseArea")
                                 ? ExpertiseArea.valueOf(node.get("expertiseArea").asText())
                                 : null)
                         .description(node.has("description")
                                 ? node.get("description").asText() : null)
                         .reportedBy(node.has("reportedBy")
                                 && !node.get("reportedBy").asText().isEmpty()
                                 ? node.get("reportedBy").asText()
                                 : null)
                         .screenshotUrl(node.has("screenshotUrl")
                                 ? node.get("screenshotUrl").asText() : null)
                         .suggestedFix(node.has("suggestedFix")
                                 ? node.get("suggestedFix").asText() : null)
                         .uiElementId(node.has("uiElementId")
                                 ? node.get("uiElementId").asText() : null)
                         .createTicket();
                 break;
             default:
                 break;
         }
         if (ticket != null) {
             ticket.setCreatedAt(timestamp);
             errorLogger.setTicketIdGlobal(errorLogger.getTicketIdGlobal() + 1);
         }
     }

    /**
     * createMilestoneView command helper & wrapper method
     * @param mapper for mapping the output corresponding ObjectNode
     * @param user for which the view is served, depending if it is MANAGER/DEV
     */
    public ObjectNode createMilestoneView(final ObjectMapper mapper, final User user) {
        ErrLogger errorLogger = ErrLogger.getInstance();
        ObjectNode root = mapper.createObjectNode();

        root.put("command", this.command);
        root.put("username", this.username);
        root.put("timestamp", this.timestamp);

        ArrayNode milestonesArray = mapper.createArrayNode();
        List<Milestone> milestones = errorLogger.getMilestones();

        if (milestones == null || user == null) {
            root.set("milestones", milestonesArray);
            return root;
        }

        List<Milestone> visibleMilestones = new ArrayList<>();

        for (Milestone milestone : milestones) {
            boolean canView = false;

            if ("MANAGER".equals(user.getRole())) {
                canView = user.getUsername().equals(milestone.getCreatedBy());
            } else if ("DEVELOPER".equals(user.getRole())) {
                if (milestone.getAssignedDevs() != null) {
                    for (String dev : milestone.getAssignedDevs()) {
                        if (user.getUsername().equals(dev)) {
                            canView = true;
                            break;
                        }
                    }
                }
            }

            if (canView) {
                visibleMilestones.add(milestone);
            }
        }
        visibleMilestones.sort((m1, m2) -> {
            int dateCompare = m1.getDueDate().compareTo(m2.getDueDate());
            if (dateCompare != 0) {
                return dateCompare;
            }
            return m1.getName().compareTo(m2.getName());
        });
        for (Milestone milestone : visibleMilestones) {
            ObjectNode milestoneNode = milestone.toViewNode(mapper);
            milestonesArray.add(milestoneNode);
        }
        root.set("milestones", milestonesArray);
        return root;
    }

    /**
     * viewTickets command helper & wrapper method
     * @param mapper for mapping the output corresponding ObjectNode
     * @param user for which the view is served, depending if it is MANAGER/DEV/REPORTER
     */
    public ObjectNode viewTickets(final ObjectMapper mapper, final User user) {
        ErrLogger errorLogger = ErrLogger.getInstance();
        ObjectNode root = mapper.createObjectNode();

        root.put("command", this.command);
        root.put("username", this.username);
        root.put("timestamp", this.timestamp);

        ArrayNode ticketsArray = mapper.createArrayNode();

        if (user == null) {
            root.set("tickets", ticketsArray);
            return root;
        }

        List<Ticket> visibleTickets = new ArrayList<>();

        switch (user.getRole()) {

            case "MANAGER":
                for (User u : errorLogger.getUsers()) {
                    if (u.getTickets() != null) {
                        visibleTickets.addAll(u.getTickets());
                    }
                }
                break;

            case "DEVELOPER":
                Set<Integer> milestoneTicketIds =
                        getMilestoneTicketIdsForDeveloper(user.getUsername(), errorLogger);

                for (User u : errorLogger.getUsers()) {
                    if (u.getTickets() != null) {
                        for (Ticket tickett : u.getTickets()) {
                            if (tickett != null
                                    && tickett.getStatus() == Status.OPEN
                                    && milestoneTicketIds.contains(tickett.getId())) {

                                visibleTickets.add(tickett);
                            }
                        }
                    }
                }
                break;

            case "REPORTER":
                for (User u : errorLogger.getUsers()) {
                    if (u.getTickets() != null) {
                        for (Ticket tickett : u.getTickets()) {
                            if (tickett != null
                                    && user.getUsername().equals(tickett.getReportedBy())) {

                                visibleTickets.add(tickett);
                            }
                        }
                    }
                }
                break;
            default:
                break;
        }

        visibleTickets.sort(
                Comparator.comparing(Ticket::getCreatedAt)
                        .thenComparing(Ticket::getId)
        );

        for (Ticket tickett : visibleTickets) {
            ObjectNode ticketNode = mapper.createObjectNode();
            ticketNode.put("id", tickett.getId());
            ticketNode.put("type", tickett.getType());
            ticketNode.put("title", tickett.getTitle());
            ticketNode.put("businessPriority", tickett.getBusinessPriority().name());
            ticketNode.put("status", tickett.getStatus().name());
            ticketNode.put("createdAt", tickett.getCreatedAt());
            ticketNode.put("assignedAt", tickett.getAssignedAt() != null
                    ? tickett.getAssignedAt() : "");
            ticketNode.put("solvedAt", tickett.getSolvedAt() != null
                    ? tickett.getSolvedAt() : "");
            ticketNode.put("assignedTo", tickett.getAssignedTo() != null
                    ? tickett.getAssignedTo() : "");
            ticketNode.put("reportedBy", tickett.getReportedBy());
            ticketNode.set("comments", mapper.createArrayNode());

            ticketsArray.add(ticketNode);
        }

        root.set("tickets", ticketsArray);
        return root;
    }

    /**
     * viewAssignedTickets command helper & wrapper method
     * @param mapper for mapping the output corresponding ObjectNode
     * @param user for which the view is served
     */
    public ObjectNode viewAssignedTickets(final ObjectMapper mapper, final User user) {
        ObjectNode root = mapper.createObjectNode();
        root.put("command", this.command);
        root.put("username", this.username);
        root.put("timestamp", this.timestamp);

        ArrayNode ticketsArray = mapper.createArrayNode();
        if (user.getAssignedTickets() != null) {
            List<Ticket> sortedTickets = new ArrayList<>(user.getAssignedTickets());
            sortedTickets.sort(Comparator
                    .comparing(Ticket::getBusinessPriority).reversed()
                    .thenComparing(Ticket::getId));

            for (Ticket tickett : sortedTickets) {
                ObjectNode ticketNode = mapper.createObjectNode();
                ticketNode.put("id", tickett.getId());
                ticketNode.put("type", tickett.getType());
                ticketNode.put("title", tickett.getTitle());
                ticketNode.put("businessPriority", tickett.getBusinessPriority().name());
                ticketNode.put("status", tickett.getStatus().name());
                ticketNode.put("createdAt", tickett.getCreatedAt());
                if (tickett.getAssignedAt() != null && !tickett.getAssignedAt().isEmpty()) {
                    ticketNode.put("assignedAt", tickett.getAssignedAt());
                }
                ticketNode.put("reportedBy", tickett.getReportedBy());

                List<Comment> comments = tickett.getComments();
                if (comments != null && !comments.isEmpty()) {
                    ArrayNode commentsArray = mapper.createArrayNode();
                    for (Comment comment : comments) {
                        ObjectNode commentNode = mapper.createObjectNode();
                        commentNode.put("author", comment.getAuthor());
                        commentNode.put("content", comment.getContent());
                        commentNode.put("createdAt", comment.getCreatedAt());
                        commentsArray.add(commentNode);
                    }
                    ticketNode.set("comments", commentsArray);
                } else {
                    ticketNode.set("comments", mapper.createArrayNode());
                }

                ticketsArray.add(ticketNode);
            }
        }
        root.set("assignedTickets", ticketsArray);
        return root;
    }

    /**
     * Converts the command to an ObjectNode representation based on user role and permissions.
     * @param mapper the ObjectMapper used to create JSON nodes
     * @param user the user requesting the view (determines which tickets are visible)
     * @return an ObjectNode containing command metadata and filtered tickets
     */
    public ObjectNode toObjectNode(final ObjectMapper mapper, final User user) {
        ErrLogger errorLogger = ErrLogger.getInstance();
        ObjectNode root = mapper.createObjectNode();

        root.put("command", this.command);
        root.put("username", this.username);
        root.put("timestamp", this.timestamp);

        ArrayNode ticketsArray = mapper.createArrayNode();
        boolean bypass = false;
        for (Command commandd : errorLogger.getInputCommands()) {
            if (user != null) {
                switch (user.getRole()) {
                    case "MANAGER":
                        if (commandd.ticket != null && commandd.ticket.getIsAssigned() == 0) {
                            ticketsArray.add(mapper.valueToTree(commandd.ticket));
                        }
                        break;
                    case "DEVELOPER":
                        if (commandd.ticket != null && commandd.ticket.getIsAssigned() == 0
                                && commandd.ticket.getStatus() == Status.OPEN
                                && (commandd.ticket.getAssignedTo() == null
                                || commandd.ticket.getAssignedTo().isEmpty()
                                || commandd.ticket.getAssignedTo().equals(user.getUsername()))) {
                            ticketsArray.add(mapper.valueToTree(commandd.ticket));
                        }
                        break;
                    case "REPORTER":
                        if (commandd.ticket != null && commandd.ticket.getIsAssigned() == 0
                                && user.getUsername().equals(commandd.ticket.getReportedBy())) {
                            ticketsArray.add(mapper.valueToTree(commandd.ticket));
                        }
                        break;
                    default:
                        break;
                }
            } else {
                root.put("error", this.error);
                bypass = true;
                break;
            }
        }
        if (!bypass) {
            root.set("tickets", ticketsArray);
        }
        return root;
    }

    /**
     * Finds a user by username in the global user list in ErrLogger
     * @param usernamee the username to search for
     * @return the User object if found, null otherwise
     */
   User foundUser(final String usernamee) {
         ErrLogger errorLogger = ErrLogger.getInstance();
         User found = null;
         List<User> users = errorLogger.getUsers();
         for (User user : users) {
              if (user.getUsername().equals(usernamee)) {
                  found = user;
                  break;
              }
         }
         return found;
   }

    /**
     * Unassigns a ticket from the current user, reverting it to OPEN status.
     * @param usernamee the username of the user performing the unassignment
     * @param commandNode the JSON node containing the ticket ID to unassign
     */
    void unassignTicket(final String usernamee, final JsonNode commandNode) {
        int wantedId = commandNode.get("ticketID").asInt();
        ErrLogger errorLogger = ErrLogger.getInstance();
        User user = foundUser(usernamee);
        if (user == null) {
            return;
        }
        if (user.getAssignedTickets() != null) {
            Iterator<Ticket> iterator = user.getAssignedTickets().iterator();
            while (iterator.hasNext()) {
                Ticket tickett = iterator.next();
                if (tickett != null && tickett.getId() == wantedId
                        && tickett.getStatus() == Status.IN_PROGRESS) {
                    tickett.setIsAssigned(0);
                    tickett.setStatus(Status.OPEN);
                    tickett.setAssignedAt("");
                    tickett.setAssignedTo("");
                    tickett.setSolvedAt("");
                    tickett.addHistoryAction(TicketAction.deAssigned(usernamee, timestamp));
                    tickett.clearHistoryAfterTimestamp(timestamp);
                    iterator.remove();
                    break;
                }
            }
        }
    }

    /**
     * Checks if a developer's expertise area is compatible with a ticket's required expertise area.
     * @param developerArea the developer's expertise area
     * @param ticketArea the ticket's required expertise area
     * @return true if the developer can work on the ticket, false otherwise
     */
    private boolean checkExpertiseCompatibility(final ExpertiseArea developerArea,
                                                final ExpertiseArea ticketArea) {
        switch (developerArea) {
            case FRONTEND:
                return ticketArea == ExpertiseArea.FRONTEND || ticketArea == ExpertiseArea.DESIGN;
            case BACKEND:
                return ticketArea == ExpertiseArea.BACKEND || ticketArea == ExpertiseArea.DB;
            case FULLSTACK:
                return ticketArea == ExpertiseArea.FRONTEND || ticketArea == ExpertiseArea.BACKEND
                        || ticketArea == ExpertiseArea.DEVOPS || ticketArea == ExpertiseArea.DESIGN
                        || ticketArea == ExpertiseArea.DB;
            case DEVOPS:
                return ticketArea == ExpertiseArea.DEVOPS;
            case DESIGN:
                return ticketArea == ExpertiseArea.DESIGN || ticketArea == ExpertiseArea.FRONTEND;
            case DB:
                return ticketArea == ExpertiseArea.DB;
            default:
                return false;
        }
    }

    /**
     * Gets the list of required expertise areas for a given ticket expertise area.
     * @param ticketArea the ticket's expertise area
     * @return a list of expertise area names that are acceptable for this ticket
     */
    private List<String> getRequiredExpertiseAreas(final ExpertiseArea ticketArea) {
        List<String> requiredAreas = new ArrayList<>();

        switch (ticketArea) {
            case FRONTEND:
                requiredAreas.add("FRONTEND");
                requiredAreas.add("FULLSTACK");
                requiredAreas.add("DESIGN");
                break;
            case BACKEND:
                requiredAreas.add("BACKEND");
                requiredAreas.add("FULLSTACK");
                break;
            case DEVOPS:
                requiredAreas.add("DEVOPS");
                requiredAreas.add("FULLSTACK");
                break;
            case DESIGN:
                requiredAreas.add("DESIGN");
                requiredAreas.add("FRONTEND");
                requiredAreas.add("FULLSTACK");
                break;
            case DB:
                requiredAreas.add("DB");
                requiredAreas.add("BACKEND");
                requiredAreas.add("FULLSTACK");
                break;
            default:
                requiredAreas.add(ticketArea.name());
        }

        return requiredAreas;
    }

    /**
     * Checks if a developer's seniority level is compatible with a ticket requirements
     * @param developerSeniority the developer's seniority level
     * @param tickett the ticket to check compatibility with
     * @return true if the developer can work on the ticket, false otherwise
     */
    private boolean checkSeniorityCompatibility(final Seniority developerSeniority,
                                                final Ticket tickett) {
        BussinessPriority ticketPriority = tickett.getBusinessPriority();

        switch (developerSeniority) {
            case JUNIOR:
                if (ticketPriority == BussinessPriority.HIGH
                        || ticketPriority == BussinessPriority.CRITICAL) {
                    return false;
                }
                return tickett.getType().equals("BUG")
                        || tickett.getType().equals("UI_FEEDBACK");

            case MID:
                if (ticketPriority == BussinessPriority.CRITICAL) {
                    return false;
                }
                return tickett.getType().equals("BUG")
                        || tickett.getType().equals("UI_FEEDBACK")
                        || tickett.getType().equals("FEATURE_REQUEST");

            case SENIOR:
                return true;

            default:
                return false;
        }
    }

    /**
     * Gets the list of required seniority levels for a given ticket.
     * @param tickett the ticket to analyze
     * @return a list of seniority level names that are acceptable for this ticket
     */
    private List<String> getRequiredSeniorityLevels(final Ticket tickett) {
        List<String> requiredLevels = new ArrayList<>();

        BussinessPriority priority = tickett.getBusinessPriority();
        String type = tickett.getType();

        if ((priority == BussinessPriority.LOW || priority == BussinessPriority.MEDIUM)
                && (type.equals("BUG") || type.equals("UI_FEEDBACK"))) {
            requiredLevels.add("JUNIOR");
        }

        if ((priority == BussinessPriority.LOW || priority == BussinessPriority.MEDIUM
                || priority == BussinessPriority.HIGH)
                && (type.equals("BUG") || type.equals("UI_FEEDBACK")
                || type.equals("FEATURE_REQUEST"))) {
            requiredLevels.add("MID");
        }

        if (priority == BussinessPriority.CRITICAL
                || (priority == BussinessPriority.HIGH && type.equals("FEATURE_REQUEST"))
                || (priority == BussinessPriority.LOW && type.equals("FEATURE_REQUEST"))
                || (priority == BussinessPriority.MEDIUM && type.equals("FEATURE_REQUEST"))) {
            requiredLevels.add("SENIOR");
        }

        return requiredLevels;
    }

    /**
     * Adds a comment to a specific ticket with validation check
     * @param ticketID the ID of the ticket to comment on
     * @param content the content of the comment
     * @param outputs the list to add output nodes to
     */
    public void addComment(final int ticketID, final String content,
                           final List<ObjectNode> outputs) {
        ErrLogger errorLogger = ErrLogger.getInstance();
        for (User u : errorLogger.getUsers()) {
            if (u.getTickets() != null) {
                for (Ticket tickett : u.getTickets()) {
                    if (tickett != null && tickett.getId() == ticketID) {
                        if (tickett.getComments() == null) {
                            tickett.setComments(new ArrayList<>());
                        }
                        User commandUser = foundUser(username);
                        if (tickett.reportedBy == null || tickett.reportedBy.isEmpty()) {
                            error = "Comments are not allowed on anonymous tickets.";
                            outputs.add(toObjectNode(MAPPER, null));
                            return;
                        }

                        if (content.length() < INT10) {
                            error = "Comment must be at least 10 characters long.";
                            outputs.add(toObjectNode(MAPPER, null));
                            return;
                        }

                        if (commandUser.getRole().equals("DEVELOPER")
                                && tickett.getIsAssigned() == 1
                                && !tickett.getAssignedTo().equals(username)) {
                            error = "Ticket " + ticketID + " is not assigned to the developer "
                                    + username + ".";
                            outputs.add(toObjectNode(MAPPER, null));
                            return;
                        }
                        if (commandUser.getRole().equals("REPORTER")
                                && !tickett.reportedBy.equals(username)) {
                            error = "Reporter " + username + " cannot comment on ticket "
                                    + ticketID + ".";
                            outputs.add(toObjectNode(MAPPER, null));
                            return;
                        }
                        if (commandUser.getRole().equals("REPORTER")
                                && tickett.getStatus().name().equals("CLOSED")) {
                            error = "Reporters cannot comment on CLOSED tickets.";
                            outputs.add(toObjectNode(MAPPER, null));
                            return;
                        }

                        Comment comment = new Comment(content, username, timestamp);
                        tickett.addComment(comment);
                    }
                }
            }
        }
    }

    /**
     * Removes the most recent comment from a specific ticket (undo op)
     * @param ticketID the ID of the ticket to remove the comment from
     * @param outputs the list to add output nodes to
     */
    public void undoaddComment(final int ticketID, final List<ObjectNode> outputs) {
        ErrLogger errorLogger = ErrLogger.getInstance();
        for (User u : errorLogger.getUsers()) {
            if (u.getTickets() != null) {
                for (Ticket tickett : u.getTickets()) {
                    if (tickett != null && tickett.getId() == ticketID) {
                        if (tickett.getComments() == null) {
                            return;
                        }
                        if (tickett.reportedBy == null || tickett.reportedBy.isEmpty()) {
                            error = "Comments are not allowed on anonymous tickets.";
                            outputs.add(toObjectNode(MAPPER, null));
                            return;
                        }
                        Comment last = tickett.removeLastComment();
                    }
                }
            }
        }
    }

    /**
     * Undoes the most recent status change for a ticket with ID passed in call
     * @param usernamee the username of the user performing the undo
     * @param ticketID the ID of the ticket to revert status for
     * @param outputs the list to add output nodes to
     */
    public void undoChangeStatus(final String usernamee, final int ticketID,
                                 final List<ObjectNode> outputs) {
        ErrLogger errorLogger = ErrLogger.getInstance();
        this.error = null;

        for (User u : errorLogger.getUsers()) {
            if (u.getTickets() != null) {
                for (Ticket tickett : u.getTickets()) {
                    if (tickett != null && tickett.getId() == ticketID) {
                        User commandUser = foundUser(usernamee);

                        if (tickett.getIsAssigned() == 0) {
                            error = "Ticket " + ticketID + " is not assigned.";
                            outputs.add(toObjectNode(MAPPER, null));
                            return;
                        }

                        if (commandUser.getRole().equals("DEVELOPER")
                                && tickett.getIsAssigned() == 1
                                && !tickett.getAssignedTo().equals(usernamee)) {
                            error = "Ticket " + ticketID + " is not assigned to developer "
                                    + usernamee + ".";
                            outputs.add(toObjectNode(MAPPER, null));
                            return;
                        }

                        List<TicketAction> history = tickett.getHistory();
                        TicketAction mostRecentStatusChange = null;
                        int mostRecentIndex = -1;

                        if (history != null && !history.isEmpty()) {
                            for (int i = history.size() - 1; i >= 0; i--) {
                                TicketAction action = history.get(i);
                                if ("STATUS_CHANGED".equals(action.getAction())) {
                                    mostRecentStatusChange = action;
                                    mostRecentIndex = i;
                                    break;
                                }
                            }
                        }

                        if (mostRecentStatusChange != null) {
                            String previousStatus = mostRecentStatusChange.getFrom();
                            String currentStatus = tickett.getStatus().name();

                            tickett.setStatus(Status.valueOf(previousStatus));
                            tickett.addHistoryAction(
                                    TicketAction.statusChanged(currentStatus,
                                            previousStatus, usernamee, timestamp)
                            );
                        } else {
                            return;
                        }

                        return;
                    }
                }
            }
        }
    }

    /**
     * Changes the status of a ticket to the next logical state in the workflow.
     * @param usernamee the username of the user changing the status
     * @param ticketID the ID of the ticket to update
     * @param outputs the list to add output nodes to
     */
    public void changeStatus(final String usernamee, final int ticketID,
                             final List<ObjectNode> outputs) {
        ErrLogger errorLogger = ErrLogger.getInstance();
        for (User u : errorLogger.getUsers()) {
            if (u.getTickets() != null) {
                for (Ticket tickett : u.getTickets()) {
                    if (tickett != null && tickett.getId() == ticketID) {
                        User commandUser = foundUser(usernamee);
                        if (tickett.getIsAssigned() == 0) {
                            return;
                        }

                        if (tickett.status.name().equals("CLOSED")) {
                            return;
                        }

                        if (commandUser.getRole().equals("DEVELOPER")
                                && tickett.getIsAssigned() == 1
                                && !tickett.getAssignedTo().equals(usernamee)) {
                            error = "Ticket " + ticketID + " is not assigned to developer "
                                    + usernamee + ".";
                            outputs.add(toObjectNode(MAPPER, null));
                            return;
                        }

                        String oldStatus = tickett.getStatus().name();
                        tickett.setStatus(tickett.status.next());
                        String newStatus = tickett.getStatus().name();

                        if ((newStatus.equals("CLOSED") || newStatus.equals("RESOLVED"))
                                && tickett.getSolvedAt() == null) {
                            tickett.setSolvedAt(timestamp);
                        }
                        tickett.addHistoryAction(
                                TicketAction.statusChanged(oldStatus, newStatus,
                                        usernamee, timestamp)
                        );
                    }
                }
            }
        }
    }

    /**
     * Creates a view of ticket history based on user role and permission
     * @param mapper the ObjectMapper used to create JSON nodes
     * @param user the user requesting the history view
     * @return an ObjectNode containing command metadata and ticket history
     */
    public ObjectNode viewTicketHistory(final ObjectMapper mapper, final User user) {
        ErrLogger errorLogger = ErrLogger.getInstance();
        ObjectNode root = mapper.createObjectNode();

        root.put("command", this.command);
        root.put("username", this.username);
        root.put("timestamp", this.timestamp);

        ArrayNode ticketsArray = mapper.createArrayNode();

        if (user == null) {
            root.set("ticketHistory", ticketsArray);
            return root;
        }

        List<Ticket> visibleTickets = new ArrayList<>();

        if ("DEVELOPER".equals(user.getRole())) {
            for (User u : errorLogger.getUsers()) {
                if (u.getTickets() != null) {
                    for (Ticket tickett : u.getTickets()) {
                        if (tickett != null && tickett.getHistory() != null) {
                            boolean hasInteraction = tickett.getHistory().stream()
                                    .anyMatch(action -> user.getUsername().equals(action.getBy()));
                            if (hasInteraction) {
                                visibleTickets.add(tickett);
                            }
                        }
                    }
                }
            }

        } else if ("MANAGER".equals(user.getRole())) {
            List<Milestone> milestones = errorLogger.getMilestones();
            if (milestones != null) {
                for (Milestone milestone : milestones) {
                    if (user.getUsername().equals(milestone.getCreatedBy())) {
                        visibleTickets.addAll(milestone.getMilestoneTickets());
                    }
                }
            }
        }
        Set<Integer> seenIds = new HashSet<>();
        List<Ticket> uniqueTickets = new ArrayList<>();
        for (Ticket tickett : visibleTickets) {
            if (!seenIds.contains(tickett.getId())) {
                seenIds.add(tickett.getId());
                uniqueTickets.add(tickett);
            }
        }

        uniqueTickets.sort(Comparator
                .comparing(Ticket::getCreatedAt)
                .thenComparing(Ticket::getId));
        for (Ticket tickett : uniqueTickets) {
            ObjectNode ticketNode = mapper.createObjectNode();
            ticketNode.put("id", tickett.getId());
            ticketNode.put("title", tickett.getTitle());
            ticketNode.put("status", tickett.getStatus().name());
            ArrayNode actionsArray = mapper.createArrayNode();
            List<TicketAction> actions = tickett.getHistory();
            if (actions != null) {
                for (TicketAction action : actions) {
                    actionsArray.add(mapper.valueToTree(action));
                }
            }
            ticketNode.set("actions", actionsArray);
            ArrayNode commentsArray = mapper.createArrayNode();
            List<Comment> comments = tickett.getComments();
            if (comments != null) {
                for (Comment comment : comments) {
                    ObjectNode commentNode = mapper.createObjectNode();
                    commentNode.put("author", comment.getAuthor());
                    commentNode.put("content", comment.getContent());
                    commentNode.put("createdAt", comment.getCreatedAt());
                    commentsArray.add(commentNode);
                }
            }
            ticketNode.set("comments", commentsArray);
            ticketsArray.add(ticketNode);
        }

        root.set("ticketHistory", ticketsArray);
        return root;
    }

    /**
     * Builds a composite specification for filtering tickets based on a filter
     * @param filter the filter containing criteria for ticket selection
     * @return a Specification<Ticket> that combines all filter conditions
     */
    private Specification<Ticket> buildCompositeSpecificationTicket(final Filter filter) {
        Specification<Ticket> compositeSpec = null;

        if (filter.hasCreatedAt()) {
            String createdAt = filter.getCreatedAt();
            CreatedAtFilter createdAtFilter = new CreatedAtFilter(createdAt);
            compositeSpec = (compositeSpec == null)
                    ? createdAtFilter : compositeSpec.and(createdAtFilter);
        }

        if (filter.hasCreatedBefore()) {
            String createdBefore = filter.getCreatedBefore();
            CreatedBeforeFilter createdBeforeFilter = new CreatedBeforeFilter(createdBefore);
            compositeSpec = (compositeSpec == null)
                    ? createdBeforeFilter : compositeSpec.and(createdBeforeFilter);
        }

        if (filter.hasCreatedAfter()) {
            String createdAfter = filter.getCreatedAfter();
            CreatedAfterFilter createdAfterFilter = new CreatedAfterFilter(createdAfter);
            compositeSpec = (compositeSpec == null)
                    ? createdAfterFilter : compositeSpec.and(createdAfterFilter);
        }

        if (filter.hasBusinessPriority()) {
            String businessPriority = filter.getBusinessPriority();
            try {
                BussinessPriority priority =
                        BussinessPriority.valueOf(businessPriority.toUpperCase());
                BusinessPriorityFilter priorityFilter =
                        new BusinessPriorityFilter(priority.toString());
                compositeSpec = (compositeSpec == null)
                        ? priorityFilter : compositeSpec.and(priorityFilter);
            } catch (IllegalArgumentException e) {
            }
        }

        if (filter.hasType()) {
            String type = filter.getType();
            TypeFilter typeFilter = new TypeFilter(type);
            compositeSpec = (compositeSpec == null)
                    ? typeFilter : compositeSpec.and(typeFilter);
        }

        if (filter.hasKeywords()) {
            List<String> keywords = filter.getKeywordsList();

            Specification<Ticket> keywordSpec = null;
            for (String keyword : keywords) {
                KeywordFilter keywordFilter = new KeywordFilter(keyword);
                keywordSpec = (keywordSpec == null)
                        ? keywordFilter : keywordSpec.or(keywordFilter);
            }
            if (keywordSpec != null) {
                compositeSpec = (compositeSpec == null)
                        ? keywordSpec : compositeSpec.and(keywordSpec);
            }
        }
        return compositeSpec != null ? compositeSpec : ticket -> true;
    }

    /**
     * Builds a composite specification for filtering users based on filter criteria.
     * @param filter the filter containing criteria for user selection
     * @return a Specification<User> that combines all filter conditions
     */
    private Specification<User> buildCompositeSpecificationUser(final Filter filter) {
        Specification<User> compositeSpec = null;

        if (filter.hasExpertiseArea()) {
            String expertiseArea = filter.getExpertiseArea();
            try {
                ExpertiseArea area = ExpertiseArea.valueOf(expertiseArea.toUpperCase());
                ExpertiseAreaFilter expertiseFilter = new ExpertiseAreaFilter(area.toString());
                compositeSpec = (compositeSpec == null)
                        ? expertiseFilter : compositeSpec.and(expertiseFilter);
            } catch (IllegalArgumentException e) {
            }
        }

        if (filter.hasPerformanceScoreAbove()) {
            compositeSpec = (compositeSpec == null)
                    ? new PerformanceScoreAboveFilter(filter.getPerformanceScoreAbove())
                    : compositeSpec.
                    and(new PerformanceScoreAboveFilter(filter.getPerformanceScoreAbove()));
        }

        if (filter.hasPerformanceScoreBelow()) {
            compositeSpec = (compositeSpec == null)
                    ? new PerformanceScoreBelowFilter(filter.getPerformanceScoreBelow())
                    : compositeSpec.
                    and(new PerformanceScoreBelowFilter(filter.getPerformanceScoreBelow()));
        }

        if (filter.hasSeniority()) {
            String seniority = filter.getSeniority();
            try {
                Seniority seniorityLevel = Seniority.valueOf(seniority.toUpperCase());
                SeniorityFilter seniorityFilter = new SeniorityFilter(seniorityLevel.toString());
                compositeSpec = (compositeSpec == null)
                        ? seniorityFilter : compositeSpec.and(seniorityFilter);
            } catch (IllegalArgumentException e) {
            }
        }
        return compositeSpec != null ? compositeSpec : ticket -> true;
    }

    /**
     * Performs a search operation based on filter criteria and user role.
     * @param outputs the list to add search results to
     * @param filter the filter containing search criteria
     */
    public void searchFilters(final List<ObjectNode> outputs, final Filter filter) {
        ErrLogger errorLogger = ErrLogger.getInstance();
        ObjectNode root = MAPPER.createObjectNode();

        root.put("command", this.command);
        root.put("username", this.username);
        root.put("timestamp", this.timestamp);
        root.put("searchType", filter.getSearchType());

        User currentUser = foundUser(username);
        if (currentUser == null) {
            error = "User " + username + " not found.";
            outputs.add(toObjectNode(MAPPER, null));
            return;
        }

        String searchType = filter.getSearchType();
        if (searchType == null || searchType.isEmpty()) {
            searchType = "TICKET";
        }

        if (searchType.equals("DEVELOPER")) {
            if (!currentUser.getRole().equals("MANAGER")) {
                error = "Only managers can search for developers.";
                outputs.add(toObjectNode(MAPPER, null));
                return;
            }
            Specification<User> searchSpecU = buildCompositeSpecificationUser(filter);
            List<User> visibleUsers = new ArrayList<>();
            if (currentUser.getSubordinates() != null) {
                for (String subordinateUsername: currentUser.getSubordinates()) {
                    for (User glbusr: errorLogger.getUsers()) {
                        if (glbusr.getUsername().equals(subordinateUsername)) {
                            visibleUsers.add(glbusr);
                        }
                    }
                }
            }
            List<User> filteredUsers = new ArrayList<>();
            for (User user: visibleUsers) {
                if (searchSpecU.isSatisfiedBy(user)) {
                    filteredUsers.add(user);
                }
            }
            filteredUsers.sort(Comparator.comparing(User::getUsername));
            ArrayNode usersArray = MAPPER.createArrayNode();
            for (User user : filteredUsers) {
                ObjectNode userNode = MAPPER.createObjectNode();
                userNode.put("username", user.getUsername());
                userNode.put("expertiseArea", user.getExpertiseArea().toString());
                userNode.put("seniority", user.getSeniority().toString());
                userNode.put("performanceScore", user.getPerformanceScore());
                userNode.put("hireDate", user.getHireDate().toString());
                usersArray.add(userNode);
            }
            root.set("results", usersArray);
            outputs.add(root);
            return;
        } else {
            Specification<Ticket> searchSpec = buildCompositeSpecificationTicket(filter);
            List<Ticket> visibleTickets = getVisibleTickets(currentUser);
            if (currentUser.getRole().equals("DEVELOPER")) {
                Specification<Ticket> openTicketsOnly = ticket -> ticket.getStatus() == Status.OPEN;
                searchSpec = (searchSpec == null)
                        ? openTicketsOnly : searchSpec.and(openTicketsOnly);
            }
            List<Ticket> filteredTickets = new ArrayList<>();
            for (Ticket tickett : visibleTickets) {
                if (!searchSpec.isSatisfiedBy(tickett)) {
                    continue;
                }
                if (filter.hasAvailableForAssignment()
                        && filter.getAvailableForAssignment()
                        && currentUser.getRole().equals("DEVELOPER")) {

                    if (!canBeAssignedInSearch(currentUser, tickett)) {
                        continue;
                    }
                }
                filteredTickets.add(tickett);
            }

            filteredTickets.sort(Comparator
                    .comparing(Ticket::getCreatedAt)
                    .thenComparing(Ticket::getId));

            ArrayNode ticketsArray = MAPPER.createArrayNode();
            for (Ticket tickett : filteredTickets) {
                ObjectNode ticketNode = MAPPER.createObjectNode();
                ticketNode.put("id", tickett.getId());
                ticketNode.put("type", tickett.getType());
                ticketNode.put("title", tickett.getTitle());
                ticketNode.put("businessPriority", tickett.getBusinessPriority().toString());
                ticketNode.put("status", tickett.getStatus().toString());
                ticketNode.put("createdAt", tickett.getCreatedAt());
                ticketNode.put("solvedAt", tickett.getSolvedAt() == null
                        ? "" : tickett.getSolvedAt());
                ticketNode.put("reportedBy", tickett.getReportedBy());

                if (currentUser.getRole().equals("MANAGER")) {
                    ArrayNode matchingWordsArray = MAPPER.createArrayNode();
                    if (filter.hasKeywords()) {
                        List<String> matchingWords = new ArrayList<>();
                        String titleLower = tickett.getTitle() != null
                                ? tickett.getTitle().toLowerCase() : "";
                        String descriptionLower = tickett.getDescription() != null
                                ? tickett.getDescription().toLowerCase() : "";

                        for (String keyword : filter.getKeywordsList()) {
                            String keywordLower = keyword.toLowerCase();
                            if (titleLower.contains(keywordLower)
                                    || descriptionLower.contains(keywordLower)) {
                                matchingWords.add(keyword);
                            }
                        }

                        Collections.sort(matchingWords);
                        for (String word : matchingWords) {
                            matchingWordsArray.add(word);
                        }
                    }

                    ticketNode.set("matchingWords", matchingWordsArray);
                }

                ticketsArray.add(ticketNode);
            }

            root.set("results", ticketsArray);
            outputs.add(root);
        }
    }

    /**
     * Gets the list of tickets visible to a specific user based on their role
     * @param user the user requesting the tickets
     * @return a list of tickets visible to the user
     */
    private List<Ticket> getVisibleTickets(final User user) {
        ErrLogger errorLogger = ErrLogger.getInstance();
        List<Ticket> visibleTickets = new ArrayList<>();

        switch (user.getRole()) {
            case "MANAGER":
                for (User u : errorLogger.getUsers()) {
                    if (u.getTickets() != null) {
                        visibleTickets.addAll(u.getTickets());
                    }
                }
                break;

            case "DEVELOPER":
                Set<Integer> milestoneTicketIds =
                        getMilestoneTicketIdsForDeveloper(user.getUsername(), errorLogger);

                for (User u : errorLogger.getUsers()) {
                    if (u.getTickets() != null) {
                        for (Ticket tickett : u.getTickets()) {
                            if (tickett != null
                                    && tickett.getStatus() == Status.OPEN
                                    && milestoneTicketIds.contains(tickett.getId())
                                    && (tickett.getIsAssigned() == 0
                                    || (tickett.getAssignedTo() != null
                                    && tickett.getAssignedTo().equals(user.getUsername())))) {
                                visibleTickets.add(tickett);
                            }
                        }
                    }
                }
                break;
            default: {
                break;
            }
        }

        return visibleTickets;
    }

    /**
     * Gets the set of ticket IDs from milestones assigned to a specific dev
     * @param developerUsername the username of the developer
     * @param errorLogger the error logger containing milestone data
     * @return a set of ticket IDs assigned to the developer via milestones
     */
    private Set<Integer> getMilestoneTicketIdsForDeveloper(final String developerUsername,
                                                           final ErrLogger errorLogger) {
        Set<Integer> ticketIds = new HashSet<>();

        if (errorLogger.getMilestones() == null) {
            return ticketIds;
        }

        for (Milestone milestone : errorLogger.getMilestones()) {
            if (milestone.getAssignedDevs() != null) {
                boolean isAssigned = false;
                for (String dev : milestone.getAssignedDevs()) {
                    if (dev.equals(developerUsername)) {
                        isAssigned = true;
                        break;
                    }
                }

                if (isAssigned && milestone.getTickets() != null) {
                    for (int ticketId : milestone.getTickets()) {
                        ticketIds.add(ticketId);
                    }
                }
            }
        }

        return ticketIds;
    }

    /**
     * Assigns a ticket to the current user with validation check
     * @param usernamee the username of the user assigning the ticket to themselves
     * @param commandNode the JSON node containing the ticket ID to assign
     * @param outputs the list to add output nodes to
     */
    void assignTicket2(final String usernamee, final JsonNode commandNode,
                       final List<ObjectNode> outputs) {
        int wantedId = commandNode.get("ticketID").asInt();
        ErrLogger errorLogger = ErrLogger.getInstance();
        User user = foundUser(usernamee);

        if (user == null) {
            return;
        }

        if (user.getAssignedTickets() == null) {
            user.setAssignedTickets(new ArrayList<>());
        }

        boolean alreadyAssigned = user.getAssignedTickets().stream()
                .anyMatch(t -> t != null && t.getId() == wantedId);
        if (alreadyAssigned) {
            return;
        }

        Ticket ticketToAssign = null;
        for (User u : errorLogger.getUsers()) {
            if (u.getTickets() != null) {
                for (Ticket tickett : u.getTickets()) {
                    if (tickett != null && tickett.getId() == wantedId) {
                        ticketToAssign = tickett;
                        break;
                    }
                }
            }
            if (ticketToAssign != null) {
                break;
            }
        }

        if (ticketToAssign == null) {
            return;
        }

        if (ticketToAssign.getStatus() != Status.OPEN) {
            error = "Only OPEN tickets can be assigned.";
            outputs.add(toObjectNode(MAPPER, null));
            return;
        }

        if (!checkExpertiseCompatibility(user.getExpertiseArea(),
                ticketToAssign.getExpertiseArea())) {
            List<String> requiredAreas = getRequiredExpertiseAreas(ticketToAssign.
                    getExpertiseArea());
            Collections.sort(requiredAreas);

            error = "Developer " + usernamee
                    + " cannot assign ticket " + wantedId
                    + " due to expertise area. Required: "
                    + String.join(", ", requiredAreas)
                    + "; Current: " + user.getExpertiseArea() + ".";

            outputs.add(toObjectNode(MAPPER, null));
            return;
        }

        if (!checkSeniorityCompatibility(user.getSeniority(), ticketToAssign)) {
            List<String> requiredLevels = getRequiredSeniorityLevels(ticketToAssign);
            Collections.sort(requiredLevels);

            error = "Developer " + usernamee
                    + " cannot assign ticket " + wantedId
                    + " due to seniority level. Required: "
                    + String.join(", ", requiredLevels)
                    + "; Current: " + user.getSeniority() + ".";

            outputs.add(toObjectNode(MAPPER, null));
            return;
        }

        List<Milestone> milestones = errorLogger.getMilestones();
        boolean canAssign = true;
        System.out.println("=== DEBUG assignTicket2 for ticket " + wantedId + " ===");
        System.out.println("Checking milestones...");
        if (milestones != null) {
            canAssign = false;

            for (Milestone milestone : milestones) {
                if (milestone.getMilestoneTickets() == null) {
                    continue;
                }
                boolean ticketInMilestone = milestone.getMilestoneTickets().stream()
                        .anyMatch(t -> t.getId() == wantedId);

                if (milestone.getMilestoneTickets() != null) {
                    System.out.println("    Ticket IDs in milestoneTickets:");
                    for (Ticket t : milestone.getMilestoneTickets()) {
                        System.out.println("      - ID: " + t.getId()
                                + " (looking for: " + wantedId + ")");
                    }

                    boolean ticketInMilestonee = milestone.getMilestoneTickets().stream()
                            .anyMatch(t -> t.getId() == wantedId);
                    System.out.println("    Stream anyMatch result: " + ticketInMilestonee);
                }
                if (!ticketInMilestone) {
                    continue;
                }
                if (milestone.isBlocked()) {
                    error = "Cannot assign ticket " + wantedId
                            + " from blocked milestone " + milestone.getName() + ".";
                    outputs.add(toObjectNode(MAPPER, null));
                    return;
                }

                if (milestone.getAssignedDevs() == null
                        || Arrays.stream(milestone.getAssignedDevs()).
                        noneMatch(usernamee::equals)) {

                    error = "Developer " + usernamee
                            + " is not assigned to milestone " + milestone.getName() + ".";
                    outputs.add(toObjectNode(MAPPER, null));
                    return;
                }

                canAssign = true;
                break;
            }
        }

        if (!canAssign) {
            return;
        }
        String oldStatus = ticketToAssign.getStatus().name();
        ticketToAssign.setStatus(Status.IN_PROGRESS);
        ticketToAssign.setAssignedAt(timestamp);
        ticketToAssign.setAssignedTo(usernamee);
        ticketToAssign.setIsAssigned(1);
        user.getAssignedTickets().add(ticketToAssign);

        ticketToAssign.addHistoryAction(
                TicketAction.assigned(usernamee, timestamp)
        );
        ticketToAssign.addHistoryAction(
                TicketAction.statusChanged(oldStatus, "IN_PROGRESS", usernamee, timestamp)
        );
    }

    /**
     * Checks if a ticket can be assigned to a developer in search results.
     * @param dev the developer attempting to assign the ticket
     * @param tickett the ticket to check assignment eligibility for
     * @return true if the ticket can be assigned to the developer, false otherwise
     */
    private boolean canBeAssignedInSearch(final User dev, final Ticket tickett) {
        ErrLogger errorLogger = ErrLogger.getInstance();
        if (tickett.getStatus() != Status.OPEN) {
            return false;
        }
        if (!checkExpertiseCompatibility(
                dev.getExpertiseArea(),
                tickett.getExpertiseArea())) {
            return false;
        }
        if (!checkSeniorityCompatibility(
                dev.getSeniority(),
                tickett)) {
            return false;
        }
        List<Milestone> milestones = errorLogger.getMilestones();
        if (milestones == null) {
            return false;
        }

        for (Milestone milestone : milestones) {
            if (milestone.getMilestoneTickets() == null) {
                continue;
            }

            boolean containsTicket = milestone.getMilestoneTickets()
                    .stream()
                    .anyMatch(t -> t.getId() == tickett.getId());

            if (!containsTicket) {
                continue;
            }
            if (milestone.isBlocked()) {
                return false;
            }
            if (milestone.getAssignedDevs() == null) {
                return false;
            }

            for (String devName : milestone.getAssignedDevs()) {
                if (devName.equals(dev.getUsername())) {
                    return true;
                }
            }

            return false;
        }

        return false;
    }

    /**
     * viewNotifications command helper & wrapper method
     * @param outputs for the output printing node list
     */
    public void viewNotifications(final List<ObjectNode> outputs) {
        User user = foundUser(username);
        if (user == null) {
            return;
        }
        ObjectNode root = MAPPER.createObjectNode();
        root.put("command", this.command);
        root.put("username", this.username);
        root.put("timestamp", this.timestamp);
        root.set("notifications", MAPPER.valueToTree(user.getNotifications()));
        outputs.add(root);
        user.clearNotifications();
    }


    /**
     * generateCustomerImpactReport command helper & wrapper method
     * @param outputs for the output printing node list
     */
    void generateCustomerImpactReport(final List<ObjectNode> outputs) {
        ObjectNode root = MAPPER.createObjectNode();
        root.put("command", this.command);
        root.put("username", this.username);
        root.put("timestamp", this.timestamp);
        ErrLogger errorLogger = ErrLogger.getInstance();
        Set<Ticket> okayTickets = new HashSet<>();
        for (User usr: errorLogger.getUsers()) {
             if (usr.getTickets() != null) {
                 for (Ticket tickett: usr.getTickets()) {
                     if (tickett != null && tickett.getStatus().toString().equals("OPEN")
                             || tickett.getStatus().toString().equals("IN_PROGRESS")) {
                         okayTickets.add(tickett);
                     }
                 }
             }
         }
        MetricStrategy customerImpact = new CustomerImpact();
        MetricsManager metricsManager = new MetricsManager(customerImpact);
        List<Ticket> ticketList = new ArrayList<>(okayTickets);
        ObjectNode reportNode = metricsManager.processMetrics(
                customerImpact.totalNumber(ticketList),
                customerImpact.totalTicketsType(ticketList),
                customerImpact.totalTicketsPriority(ticketList),
                customerImpact.totalTicketsParticular(ticketList),
                "customerImpactByType"
        );
        root.set("report", reportNode);
        outputs.add(root);
    }

    /**
     * generateTicketRiskReport command helper & wrapper method
     * @param outputs for the output printing node list
     */
    void generateTicketRiskReport(final List<ObjectNode> outputs) {
        ObjectNode root = MAPPER.createObjectNode();
        root.put("command", this.command);
        root.put("username", this.username);
        root.put("timestamp", this.timestamp);
        ErrLogger errorLogger = ErrLogger.getInstance();
        Set<Ticket> okayTickets = new HashSet<>();
        for (User usr: errorLogger.getUsers()) {
            if (usr.getTickets() != null) {
                for (Ticket tickett: usr.getTickets()) {
                    if (tickett != null && tickett.getStatus().toString().equals("OPEN")
                            || tickett.getStatus().toString().equals("IN_PROGRESS")) {
                        okayTickets.add(tickett);
                    }
                }
            }
        }
        MetricStrategy ticketRisk = new TicketRisk();
        MetricsManager metricsManager = new MetricsManager(ticketRisk);
        List<Ticket> ticketList = new ArrayList<>(okayTickets);
        ObjectNode reportNode = metricsManager.processMetrics(
                ticketRisk.totalNumber(ticketList),
                ticketRisk.totalTicketsType(ticketList),
                ticketRisk.totalTicketsPriority(ticketList),
                ticketRisk.totalTicketsParticular(ticketList),
                "riskByType"
        );
        root.set("report", reportNode);
        outputs.add(root);
    }

    /**
     * generateResolutionEfficiencyReport command helper & wrapper method
     * @param outputs for the output printing node list
     */
    void generateResolutionEfficiencyReport(final List<ObjectNode> outputs) {
        ObjectNode root = MAPPER.createObjectNode();
        root.put("command", this.command);
        root.put("username", this.username);
        root.put("timestamp", this.timestamp);
        ErrLogger errorLogger = ErrLogger.getInstance();
        Set<Ticket> okayTickets = new HashSet<>();
        for (User usr: errorLogger.getUsers()) {
            if (usr.getTickets() != null) {
                for (Ticket tickett: usr.getTickets()) {
                    if (tickett != null && tickett.getStatus().toString().equals("CLOSED")
                            || tickett.getStatus().toString().equals("RESOLVED")) {
                        okayTickets.add(tickett);
                    }
                }
            }
        }
        MetricStrategy efficiencyType = new EfficiencyType();
        MetricsManager metricsManager = new MetricsManager(efficiencyType);
        List<Ticket> ticketList = new ArrayList<>(okayTickets);
        ObjectNode reportNode = metricsManager.processMetrics(
                efficiencyType.totalNumber(ticketList),
                efficiencyType.totalTicketsType(ticketList),
                efficiencyType.totalTicketsPriority(ticketList),
                efficiencyType.totalTicketsParticular(ticketList),
                "efficiencyByType"
        );
        root.set("report", reportNode);
        outputs.add(root);
    }

    /**
     * appStabilityReport command helper & wrapper method
     * @param outputs for the output printing node list
     */
    public void appStabilityReport(final List<ObjectNode> outputs) {
        ObjectNode root = MAPPER.createObjectNode();
        root.put("command", this.command);
        root.put("username", this.username);
        root.put("timestamp", this.timestamp);

        ErrLogger errorLogger = ErrLogger.getInstance();
        Set<Ticket> activeTickets = new HashSet<>();

        for (User usr : errorLogger.getUsers()) {
            if (usr.getTickets() == null) {
                continue;
            }

            for (Ticket ticket : usr.getTickets()) {
                if (ticket == null) {
                    continue;
                }

                String status = ticket.getStatus().toString();
                if ("OPEN".equals(status) || "IN_PROGRESS".equals(status)) {
                    activeTickets.add(ticket);
                }
            }
        }

        List<Ticket> ticketList = new ArrayList<>(activeTickets);

        MetricStrategy customerImpact = new CustomerImpact();
        MetricStrategy ticketRisk = new TicketRisk();
        String stabilityLabel = "PARTIALLY STABLE";

        if (ticketList.isEmpty()) {
            stabilityLabel = "STABLE";
        } else {
            ObjectNode riskByType = ticketRisk.totalTicketsParticular(ticketList);
            boolean allNegligible = true;

            Iterator<JsonNode> values = riskByType.elements();
            while (values.hasNext()) {
                String value = values.next().asText();

                if ("SIGNIFICANT".equals(value)) {
                    stabilityLabel = "UNSTABLE";
                    allNegligible = false;
                    break;
                }

                if (!"NEGLIGIBLE".equals(value)) {
                    allNegligible = false;
                }
            }
            if (allNegligible) {
                stabilityLabel = "STABLE";
            }
        }
        ObjectNode report = MAPPER.createObjectNode();
        report.put("totalOpenTickets", customerImpact.
                totalNumber(ticketList).get("totalTickets").asInt());
        report.set("openTicketsByType", customerImpact.
                totalTicketsType(ticketList).get("ticketsByType"));
        report.set("openTicketsByPriority", customerImpact.
                totalTicketsPriority(ticketList).get("ticketsByPriority"));

        report.set("riskByType", ticketRisk.totalTicketsParticular(ticketList));
        report.set("impactByType", customerImpact.totalTicketsParticular(ticketList));

        report.put("appStability", stabilityLabel);

        root.set("report", report);
        outputs.add(root);
    }

    /**
     * generatePerformanceReport command helper & wrapper method
     * @param outputs for the output printing node list
     */
    void generatePerformanceReport(final List<ObjectNode> outputs) {
        ObjectNode root = MAPPER.createObjectNode();
        root.put("command", this.command);
        root.put("username", this.username);
        root.put("timestamp", this.timestamp);

        String[] sortedAssignedDevs = null;
        ErrLogger errorLogger = ErrLogger.getInstance();
        User user = foundUser(this.username);
        sortedAssignedDevs =
                        Arrays.stream(user.getSubordinates())
                                .sorted((a, b) -> a.compareTo(b))
                                .toArray(String[]::new);
        List<User> devs = Collections.synchronizedList(new ArrayList<>());
        if (sortedAssignedDevs == null) {
            return;
        }
        for (String devInSorted: sortedAssignedDevs) {
            for (User u: errorLogger.getUsers()) {
                if (u.getUsername().equals(devInSorted)) {
                    devs.add(u);
                }
            }
        }
        int monthToLook = LocalDate.parse(this.timestamp).getMonthValue();
        if (monthToLook == 1) {
            monthToLook = INT12;
        } else {
            monthToLook--;
        }

        ArrayNode reportArray = MAPPER.createArrayNode();
        for (User u: devs) {
            JuniorPerformance junior = null;
            MidPerformance mid = null;
            SeniorPerformance senior = null;
            PerformanceManager perfManager = null;
            if (u.getSeniority().toString().equalsIgnoreCase("JUNIOR")) {
                junior = new JuniorPerformance();
                perfManager = new PerformanceManager(junior);
                reportArray.add(perfManager.processPerformance(
                        u.getUsername(),
                        junior.closedTickets(u, monthToLook),
                        junior.performanceScore(u, monthToLook),
                        junior.averageResolutionTime(u, monthToLook),
                        junior.seniorityOfDev(u)
                ));
                u.setPerformanceScore(junior.performanceScore(u, monthToLook));
            } else if (u.getSeniority().toString().equalsIgnoreCase("MID")) {
                mid = new MidPerformance();
                perfManager = new PerformanceManager(mid);
                reportArray.add(perfManager.processPerformance(
                        u.getUsername(),
                        mid.closedTickets(u, monthToLook),
                        mid.performanceScore(u, monthToLook),
                        mid.averageResolutionTime(u, monthToLook),
                        mid.seniorityOfDev(u)
                ));
                u.setPerformanceScore(mid.performanceScore(u, monthToLook));
            } else if (u.getSeniority().toString().equalsIgnoreCase("SENIOR")) {
                senior = new SeniorPerformance();
                perfManager = new PerformanceManager(senior);
                reportArray.add(perfManager.processPerformance(
                        u.getUsername(),
                        senior.closedTickets(u, monthToLook),
                        senior.performanceScore(u, monthToLook),
                        senior.averageResolutionTime(u, monthToLook),
                        senior.seniorityOfDev(u)
                ));
                u.setPerformanceScore(senior.performanceScore(u, monthToLook));
            }
        }
        root.set("report", reportArray);
        outputs.add(root);
    }

    /**
     * Command manager handler for entire engine. Directly used by {@link main.App}
     * @param outputs for the output printing node list
     * @param commandNode input JSON command deserialized by {@code treetoValue() method}
     */
    public void handle(final List<ObjectNode> outputs, final JsonNode commandNode) {
         ErrLogger errorLogger = ErrLogger.getInstance();
         switch (this.command) {
             case "reportTicket":
                 handleReportTicket(outputs);
                 break;
             case "lostInvestors":
                 break;
             case "viewAssignedTickets":
                 outputs.add(viewAssignedTickets(MAPPER, foundUser(username)));
                 break;
             case "assignTicket":
                 assignTicket2(username, commandNode, outputs);
                 break;
             case "viewTickets":
                 outputs.add(viewTickets(MAPPER, foundUser(username)));
                 break;
             case "createMilestone":
                 handleCreateMilestone(outputs, commandNode);
                 break;
             case "viewMilestones":
                 outputs.add(createMilestoneView(MAPPER, foundUser(username)));
                 break;
             case "undoAssignTicket":
                 unassignTicket(username, commandNode);
                 break;
             case "addComment": {
                 int ticketID = commandNode.get("ticketID").asInt();
                 String content = commandNode.get("comment").asText();
                 addComment(ticketID, content, outputs);
                 break;
             }
             case "undoAddComment": {
                 int ticketID = commandNode.get("ticketID").asInt();
                 undoaddComment(ticketID, outputs);
                 break;
             }
             case "changeStatus": {
                 int ticketID = commandNode.get("ticketID").asInt();
                 changeStatus(username, ticketID, outputs);
                 break;
             }
             case "viewNotifications":
                 viewNotifications(outputs);
                 break;
             case "viewTicketHistory":
                 outputs.add(viewTicketHistory(MAPPER, foundUser(username)));
                 break;
             case "undoChangeStatus":
                 int ticketID = commandNode.get("ticketID").asInt();
                 System.out.println("HANDLE: undoChangeStatus for ticket: " + ticketID);
                 undoChangeStatus(username, ticketID, outputs);
                 System.out.println("HANDLE: After undoChangeStatus, error is: " + this.error);
                 break;
             case "search":
                 try {
                     JsonNode filtersNode = commandNode.get("filters");
                     Filter filter = FilterParser.parseFilter(filtersNode);
                     searchFilters(outputs, filter);
                 } catch (Exception e) {
                     errorLogger.logException(e, "Failed to parse the filter hahahaha");
                 }
                 break;
             case "generateCustomerImpactReport":
                 generateCustomerImpactReport(outputs);
                 break;
             case "generateTicketRiskReport":
                 generateTicketRiskReport(outputs);
                 break;
             case "generateResolutionEfficiencyReport":
                 generateResolutionEfficiencyReport(outputs);
                 List<Ticket> ticks = new ArrayList<>();
                 for (User usr: errorLogger.getUsers()) {
                     if (usr.getTickets() != null) {
                         for (Ticket ticket: usr.getTickets()) {
                             if (ticket != null) {
                                 ticks.add(ticket);
                             }
                         }
                     }
                 }
                 break;
             case "appStabilityReport":
                 appStabilityReport(outputs);
                 break;
             case "generatePerformanceReport":
                 generatePerformanceReport(outputs);
                 break;
             default:
                 break;
         }
     }

    /**
     * createMilestone command helper & wrapper method
     * @param outputs for the output printing node list
     * @param commandNode input JSON command deserialized by {@code treetoValue() method}
     */
    private void handleCreateMilestone(final List<ObjectNode> outputs,
                                       final JsonNode commandNode) {
        ErrLogger errorLogger = ErrLogger.getInstance();
        boolean hasError = false;

        try {
            Milestone milestone = MAPPER.treeToValue(commandNode, Milestone.class);
            milestone.initializeTickets(errorLogger.getUsers());
            User creator = foundUser(username);
            if (creator != null) {
                milestone.setCreatedBy(username);
            }

            if (foundUser(username) != null && !"MANAGER".
                    equals(foundUser(username).getRole())) {
                error = "The user does not have permission to execute this command: "
                        + "required role MANAGER; user role "
                        + foundUser(username).getRole() + ".";
                outputs.add(toObjectNode(MAPPER, null));
                return;
            }

            milestone.setStatus(Status.OPEN.name());
            if (milestone.getBlockingFor() != null && milestone.getBlockingFor().length > 0) {
                List<Milestone> existingMilestones = errorLogger.getMilestones();
                if (existingMilestones != null) {
                    for (Milestone existing : existingMilestones) {
                        for (String blocked : milestone.getBlockingFor()) {
                            if (existing.getName().equals(blocked)) {
                                existing.setBlocked(true);
                            }
                        }

                        for (int tick: existing.getTickets()) {
                            for (int mytick: milestone.getTickets()) {
                                if (tick == mytick) {
                                    error = "Tickets " + tick + " already assigned to milestone "
                                            + existing.getName() + ".";
                                    outputs.add(toObjectNode(MAPPER, null));
                                    hasError = true;
                                    break;
                                }
                            }
                            if (hasError) {
                                break;
                            }
                        }
                        if (hasError) {
                            break;
                        }
                    }
                }
                if (hasError) {
                    return;
                }
            }

            for (Ticket ticket : milestone.getMilestoneTickets()) {
                if (ticket != null) {
                    ticket.addHistoryAction(
                            TicketAction.addedToMilestone(milestone.getName(), username, timestamp)
                    );
                }
            }

            List<Milestone> currentMilestones = errorLogger.getMilestones();
            if (currentMilestones == null) {
                currentMilestones = new ArrayList<>();
            }
            currentMilestones.add(milestone);
            errorLogger.setMilestones(currentMilestones);
            milestone.updateDailyState(timestamp);

        } catch (Exception e) {
            error = "Failed to create milestone: " + e.getMessage();
            outputs.add(toObjectNode(MAPPER, null));
        }
    }

    /**
     * handleReportTicket command helper & wrapper method
     * @param outputs for the output printing node list
     */
    private void handleReportTicket(final List<ObjectNode> outputs) {
        ErrLogger errorLogger = ErrLogger.getInstance();

        if (ticket != null && ticket.reportedBy == null) {
            ticket.businessPriority = BussinessPriority.LOW;
        }

        if (ticket != null && !"BUG".equals(ticket.type) && ticket.reportedBy == null) {
            error = "Anonymous reports are only allowed for tickets of type BUG.";
            outputs.add(toObjectNode(MAPPER, null));
            ticket = null;
            return;
        }

        if (errorLogger.getPhase() != PHASE.TESTING) {
            error = "Tickets can only be reported during testing phases.";
            outputs.add(toObjectNode(MAPPER, null));
            ticket = null;
            return;
        }

        User user = foundUser(username);
        if (user == null) {
            error = "The user " + username + " does not exist.";
            outputs.add(toObjectNode(MAPPER, null));
            ticket = null;
            return;
        }

        ticket.setStatus(Status.OPEN);
        if (user.getTickets() == null) {
            user.setTickets(new ArrayList<>());
        }
        user.getTickets().add(ticket);
    }
}

