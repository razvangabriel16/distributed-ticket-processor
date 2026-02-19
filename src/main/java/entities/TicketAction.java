package entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
 /**
  *  This class captures audit information such as status changes, assignments,
  *  milestone updates, and timestamps.  Static factory methods are provided to create
  *  commonly used action types in a clear and expressive way.
  */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"milestone", "from", "to", "by", "timestamp", "action"})
public class TicketAction {

    @JsonProperty("action")
    private String action;

    @JsonProperty("by")
    private String by;

    @JsonProperty("from")
    private String from;

    @JsonProperty("to")
    private String to;

    @JsonProperty("milestone")
    private String milestone;

    @JsonProperty("timestamp")
    private String timestamp;

     /**
      * Creates a ticket action representing assignment to a dev
      * @param developerName the name of the assigned developer
      * @param timestamp the time of assignment
      * @return a TicketAction representing an assignment
      */

     public static TicketAction assigned(final String developerName, final String timestamp) {
        TicketAction action = new TicketAction();
        action.setAction("ASSIGNED");
        action.setBy(developerName);
        action.setTimestamp(timestamp);
        return action;
    }

     /**
      * deAssigned action
      */
    public static TicketAction deAssigned(final String developerName,
                                          final String timestamp) {
        TicketAction action = new TicketAction();
        action.setAction("DE-ASSIGNED");
        action.setBy(developerName);
        action.setTimestamp(timestamp);
        return action;
    }

     /**
      * Creates a ticket action representing a status changing.
      * @param fromStatus the previous status
      * @param toStatus the new status
      * @param developerName the user performing the change
      * @param timestamp the time of the status change
      * @return a TicketAction representing a status change
      */

     public static TicketAction statusChanged(final String fromStatus,
                                             final String toStatus,
                                             final String developerName,
                                             final String timestamp) {
        TicketAction action = new TicketAction();
        action.setAction("STATUS_CHANGED");
        action.setFrom(fromStatus);
        action.setTo(toStatus);
        action.setBy(developerName);
        action.setTimestamp(timestamp);
        return action;
    }

     /**
      * addToMilestone action
      */
    public static TicketAction addedToMilestone(final String milestoneName,
                                                final String managerName,
                                                final String timestamp) {
        TicketAction action = new TicketAction();
        action.setAction("ADDED_TO_MILESTONE");
        action.setMilestone(milestoneName);
        action.setBy(managerName);
        action.setTimestamp(timestamp);
        return action;
    }

     /**
      * removeFromDev action
      */
    public static TicketAction removedFromDev(final String developerName,
                                              final String timestamp) {
        TicketAction action = new TicketAction();
        action.setAction("REMOVED_FROM_DEV");
        action.setBy("system");
        action.setFrom(developerName);
        action.setTimestamp(timestamp);
        return action;
    }
}
