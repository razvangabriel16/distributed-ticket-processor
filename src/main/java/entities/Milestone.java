package entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import utils.ErrLogger;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static utils.ErrLogger.DBL100;
import static utils.ErrLogger.INT3;

/**
 * Represents a project milestone grouping multiple tickets under a shared deadline.
 * This class encapsulates milestone lifecycle management, including due dates,
 * ticket completion tracking, blocking relationships between milestones,
 * and automatic priority escalation of associated tickets.
 * It acts as a Concrete Subject in the Observer design pattern by extending
 * {@link Subject}, notifying assigned developers of milestone-related events
 * such as creation, blocking, unblocking, and coming deadlines etc.
 */
@NoArgsConstructor @Data @JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class Milestone extends Subject {
    @Getter
    private Status status;
    @JsonIgnore @Getter
    private String username;
    @JsonIgnore
    private String timestamp;
    private String createdAt;
    private String name;
    private String dueDate;
    private String[] blockingFor;
    private int[] tickets;
    @Getter
    private String[] assignedDevs;
    private String createdBy;
    private boolean isBlocked = false;
    private LocalDate lastPriorityIncreaseDate;
    @JsonIgnore
    private boolean notifiedOneDayBefore = false;
    @JsonIgnore
    private boolean wasBlockedBeforeDueDate = false;
    @Getter
    private List<Ticket> milestoneTickets = new ArrayList<>();
    @JsonIgnore
    private int daysUntilDue;
    @JsonIgnore
    private int overdueBy;
    @JsonIgnore
    private List<Integer> openTickets = new ArrayList<>();
    @JsonIgnore
    private List<Integer> closedTickets = new ArrayList<>();
    @JsonIgnore
    private double completionPercentage;
    @JsonIgnore
    private LocalDate lastUpdateDate;
    @JsonIgnore
    private boolean wasCompleted = false;
    @JsonIgnore
    private int frozenDaysUntilDue = 0;
    @JsonIgnore
    private int frozenOverdueBy = 0;

    /**
     * Sets the current status of the milestone.
     * @param status the new status value as a string and stores it as a Status Enum
     */

    public void setStatus(final String status) {
        this.status = Status.valueOf(status);
    }

    /**
     * Creates a new Milestone instance from serialized input.
     * This constructor initializes milestone metadata, assigns observers
     * based on the provided developers, establishes blocking relationships,
     * and sets the initial lifecycle state.
     * @param username the creator of the milestone
     * @param timestamp creation timestamp in {@code yyyy-MM-dd} format
     * @param name unique milestone name
     * @param dueDate milestone due date
     * @param blockingFor names of milestones blocked by this milestone
     * @param tickets identifiers of tickets associated with this milestone
     * @param assignedDevs developers assigned to this milestone
     */
    @JsonCreator
    public Milestone(@JsonProperty("username") final String username,
                     @JsonProperty("timestamp") final String timestamp,
                     @JsonProperty("name") final String name,
                     @JsonProperty("dueDate") final String dueDate,
                     @JsonProperty("blockingFor") final String[] blockingFor,
                     @JsonProperty("tickets") final int[] tickets,
                     @JsonProperty("assignedDevs") final String[] assignedDevs) {
        ErrLogger errLogger = ErrLogger.getInstance();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        this.username = username;
        this.timestamp = timestamp;
        this.name = name;
        this.dueDate = dueDate;
        this.blockingFor = blockingFor;
        this.tickets = tickets;
        this.assignedDevs = assignedDevs;
        this.createdBy = username;
        this.lastPriorityIncreaseDate = LocalDate.parse(timestamp, formatter);
        this.createdAt = timestamp;
        this.lastUpdateDate = LocalDate.parse(timestamp, formatter);
        this.status = Status.ACTIVE;

        if (assignedDevs != null && errLogger.getUsers() != null) {
            for (String dev : assignedDevs) {
                for (User usr : errLogger.getUsers()) {
                    if (usr.getUsername().equals(dev)) {
                        this.addObserver(usr);
                    }
                }
            }
            notifyObservers("New milestone "
                    + name + " has been created with due date " + dueDate + ".");
        }

        if (blockingFor != null && blockingFor.length > 0
                && errLogger.getMilestones() != null) {
            for (String blockedMilestoneName : blockingFor) {
                for (Milestone m : errLogger.getMilestones()) {
                    if (m.getName().equals(blockedMilestoneName)) {
                        m.setBlockedSilently(true);
                        break;
                    }
                }
            }
        }
    }

    /**
     * updates the blocked state of the milestone without notifying observers
     * @param blocked whether the milestone should be marked as blocke
     */
    private void setBlockedSilently(final boolean blocked) {
        this.isBlocked = blocked;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        ErrLogger errLogger = ErrLogger.getInstance();
        LocalDate currentDate = LocalDate.parse(errLogger.getCurrentDate(), formatter);
        LocalDate due = LocalDate.parse(dueDate, formatter);

        if (blocked && (currentDate.isBefore(due) || currentDate.isEqual(due))) {
            wasBlockedBeforeDueDate = true;
        }
    }

    /**
     * Resolves ticket identifiers into actual {@link Ticket} instances.
     */
    public void initializeTickets(final List<User> allUsers) {
        milestoneTickets.clear();
        if (allUsers == null || tickets == null) {
            return;
        }

        for (int ticketId : tickets) {
            boolean found = false;
            for (User user : allUsers) {
                if (user.getTickets() != null) {
                    for (Ticket ticket : user.getTickets()) {
                        if (ticket != null && ticket.getId() == ticketId) {
                            milestoneTickets.add(ticket);
                            found = true;
                            break;
                        }
                    }
                }
                if (found) {
                    break;
                }
            }
        }
    }

    /**
     * Updates the milestone state for the given date, helper function
     */
    public void updateDailyState(final String currentDateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate currentDate = LocalDate.parse(currentDateStr, formatter);

        checkOneDayBeforeDue(currentDate);

        if (!isBlocked) {
            long daysSinceLastIncrease = ChronoUnit.DAYS.between(lastPriorityIncreaseDate,
                                                                    currentDate);
            if (daysSinceLastIncrease >= INT3) {
                increaseAllTicketPriorities();
                lastPriorityIncreaseDate = currentDate;
            }
        }

        updateViewData(currentDateStr);
        lastUpdateDate = currentDate;
    }

    /**
     * Recomputes all derived milestone view data.
     */
    public void updateViewData(final String currentDateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate currentDate = LocalDate.parse(currentDateStr, formatter);
        LocalDate due = LocalDate.parse(dueDate, formatter);

        openTickets.clear();
        closedTickets.clear();

        for (Ticket ticket : milestoneTickets) {
            if (ticket.getStatus() == Status.CLOSED) {
                closedTickets.add(ticket.getId());
            } else {
                openTickets.add(ticket.getId());
            }
        }

        boolean isCompleted = openTickets.isEmpty() && !milestoneTickets.isEmpty();

        if (isCompleted && !wasCompleted) {
            wasCompleted = true;
            long daysDiff = ChronoUnit.DAYS.between(currentDate, due);

            if (daysDiff >= 0) {
                frozenDaysUntilDue = (int) daysDiff + 1;
                frozenOverdueBy = 0;
            } else {
                frozenDaysUntilDue = 0;
                frozenOverdueBy = (int) Math.abs(daysDiff);
            }
        }

        if (isCompleted) {
            this.status = Status.COMPLETED;
        } else {
            this.status = Status.ACTIVE;
        }

        if (wasCompleted) {
            daysUntilDue = frozenDaysUntilDue;
            overdueBy = frozenOverdueBy;
        } else {
            long daysDiff = ChronoUnit.DAYS.between(currentDate, due);
            if (daysDiff >= 0) {
                daysUntilDue = (int) daysDiff + 1;
                overdueBy = 0;
            } else {
                daysUntilDue = 0;
                overdueBy = (int) Math.abs(daysDiff) + 1;
            }
        }

        int totalTickets = tickets.length;
        completionPercentage = totalTickets > 0
                ? (double) closedTickets.size() / totalTickets
                : 0.0;
        completionPercentage = Math.round(completionPercentage * DBL100) / DBL100;
    }

    /**
     * Handles logic executed one day before the milestone due date.
     * If the milestone is not blocked all unresolved tickets are
     * escalated to {@link BussinessPriority#CRITICAL} and observers
     * are notified.
     */
    private void checkOneDayBeforeDue(final LocalDate currentDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate due = LocalDate.parse(dueDate, formatter);

        if (ChronoUnit.DAYS.between(currentDate, due) == 1
                && !notifiedOneDayBefore && !isBlocked) {
            for (Ticket ticket : milestoneTickets) {
                if (ticket.getStatus() != Status.CLOSED
                        && ticket.getStatus() != Status.RESOLVED) {
                    ticket.setBusinessPriority(BussinessPriority.CRITICAL);
                }
            }
            notifyObservers("Milestone " + name
                    + " is due tomorrow. All unresolved tickets are now CRITICAL.");
            notifiedOneDayBefore = true;
        }

        if (isBlocked && !currentDate.isAfter(due)) {
            wasBlockedBeforeDueDate = true;
        }
    }

    /**
     * Checks whether all milestone tickets are closed and unblocks
     * dependent milestones if possible.
     */
    public void checkAndUnblock(final int closedTicketId) {
        boolean allTicketsClosed = true;
        for (Ticket ticket : milestoneTickets) {
            if (ticket.getStatus() != Status.CLOSED) {
                allTicketsClosed = false;
                break;
            }
        }

        if (!allTicketsClosed) {
            return;
        }

        if (blockingFor != null && blockingFor.length > 0) {
            ErrLogger errLogger = ErrLogger.getInstance();
            List<Milestone> allMilestones = errLogger.getMilestones();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate currentDate = LocalDate.parse(errLogger.getCurrentDate(), formatter);

            for (String blockedMilestoneName : blockingFor) {
                for (Milestone blockedMilestone : allMilestones) {
                    if (blockedMilestone.getName().equals(blockedMilestoneName)
                            && blockedMilestone.isBlocked()) {
                        LocalDate blockedDueDate =
                                LocalDate.parse(blockedMilestone.getDueDate(), formatter);
                        blockedMilestone.setBlockedSilently(false);
                        blockedMilestone.lastPriorityIncreaseDate = currentDate;
                        if (currentDate.isAfter(blockedDueDate)
                                && blockedMilestone.wasBlockedBeforeDueDate) {
                            if (blockedMilestone.isBlocked()) {
                                return;
                            }
                            for (Ticket ticket : blockedMilestone.getMilestoneTickets()) {
                                if (ticket.getStatus() != Status.CLOSED
                                        && ticket.getStatus() != Status.RESOLVED) {
                                    ticket.setBusinessPriority(BussinessPriority.CRITICAL);
                                }
                            }
                            blockedMilestone.notifyObservers("Milestone "
                                    + blockedMilestone.getName()
                                    + " was unblocked after due date."
                                    + " All active tickets are now CRITICAL.");
                        } else {
                            blockedMilestone.notifyObservers("Milestone "
                                    + blockedMilestone.getName()
                                    + " is now unblocked as ticket "
                                    + closedTicketId + " has been CLOSED.");
                        }
                        break;
                    }
                }
            }
        }
    }

    /**
     * Builds a JSON representation for milestone rendering
     */
    public ObjectNode toViewNode(final ObjectMapper mapper) {
        ObjectNode milestoneNode = mapper.createObjectNode();
        milestoneNode.put("name", name);
        milestoneNode.set("blockingFor", mapper.valueToTree(blockingFor));
        milestoneNode.put("dueDate", dueDate);
        milestoneNode.put("createdAt", createdAt);
        milestoneNode.set("tickets", mapper.valueToTree(tickets));
        milestoneNode.set("assignedDevs", mapper.valueToTree(assignedDevs));
        milestoneNode.put("createdBy", createdBy);
        milestoneNode.put("status", status.name());
        milestoneNode.put("isBlocked", isBlocked);
        milestoneNode.put("daysUntilDue", daysUntilDue);
        milestoneNode.put("overdueBy", overdueBy);
        milestoneNode.set("openTickets", mapper.valueToTree(openTickets));
        milestoneNode.set("closedTickets", mapper.valueToTree(closedTickets));
        milestoneNode.put("completionPercentage", completionPercentage);
        milestoneNode.set("repartition", buildRepartition(mapper));

        return milestoneNode;
    }

    /**
     *Yoy
     * @param
     */
    private ArrayNode buildRepartition(final ObjectMapper mapper) {
        ArrayNode repartitionArray = mapper.createArrayNode();
        if (assignedDevs == null) {
            return repartitionArray;
        }
        Map<String, List<Integer>> devTickets = new HashMap<>();
        for (String dev : assignedDevs) {
            devTickets.put(dev, new ArrayList<>());
        }
        for (Ticket ticket : milestoneTickets) {
            String assignedTo = ticket.getAssignedTo();
            if (assignedTo != null && devTickets.containsKey(assignedTo)) {
                devTickets.get(assignedTo).add(ticket.getId());
            }
        }
        List<String> sortedDevs = Arrays.stream(assignedDevs)
                .sorted((dev1, dev2) -> {
                    int count1 = devTickets.get(dev1).size();
                    int count2 = devTickets.get(dev2).size();

                    if (count1 != count2) {
                        return Integer.compare(count1, count2);
                    }
                    return dev1.compareTo(dev2);
                })
                .collect(Collectors.toList());

        for (String dev : sortedDevs) {
            ObjectNode devNode = mapper.createObjectNode();
            devNode.put("developer", dev);
            devNode.set("assignedTickets", mapper.valueToTree(devTickets.get(dev)));
            repartitionArray.add(devNode);
        }

        return repartitionArray;
    }

    /**
     *Yoy
     * @param
     */
    public void updateTicketPriorities(final String currentDateStr) {
        if (isBlocked) {
            return;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate currentDate = LocalDate.parse(currentDateStr, formatter);
        long daysSinceLastIncrease =
                ChronoUnit.DAYS.between(lastPriorityIncreaseDate, currentDate);
        if (daysSinceLastIncrease >= INT3) {
            increaseAllTicketPriorities();
            lastPriorityIncreaseDate = currentDate;
        }
    }

    /**
     *Yoy
     * @param
     */
    private void increaseAllTicketPriorities() {
        for (Ticket ticket : milestoneTickets) {
            if (ticket.getStatus() != Status.CLOSED) {
                BussinessPriority currentPriority = ticket.getBusinessPriority();
                ticket.setBusinessPriority(currentPriority.next());
            }
        }
    }

    /**
     *Yoy
     * @param
     */
    public static Comparator<Milestone> getViewComparator() {
        return (m1, m2) -> {
            int dateCompare = m1.dueDate.compareTo(m2.dueDate);
            if (dateCompare != 0) {
                return dateCompare;
            }
            return m1.name.compareTo(m2.name);
        };
    }
}
