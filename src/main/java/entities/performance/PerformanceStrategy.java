package entities.performance;

import entities.Status;
import entities.Ticket;
import entities.User;
import utils.ErrLogger;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static utils.ErrLogger.DBL3;

/**
 * Interface for Strategy DP. It holds default helper methods for statistics and also
 * abstract method let for each seniority to be filled.
 */
public interface PerformanceStrategy {
    /**
     * computes average resolvedTicketType
     */
    default double averageResolvedTicketType(final int bug, final int feature, final int ui) {
        return (bug + feature + ui) / DBL3;
    }

    /**
     * computes closed pertinent tickets (i.e those valid for command analysis)
     */
    default List<Ticket> closedPertinentTickets(final User user, final int month) {
        List<Ticket> resultedTickets = new ArrayList<>();
        if ((user.getAssignedTickets() != null && user.getAssignedTickets().size() != 0)) {
            for (Ticket ticket : user.getAssignedTickets()) {
                if (ticket.getStatus() == Status.CLOSED) {
                    int ticketSolvedMonth = LocalDate.parse(ticket.
                            getFirstSolvedAt()).getMonthValue();
                    if (ticketSolvedMonth == month) {
                        resultedTickets.add(ticket);
                    }
                }
            }
        }
        return resultedTickets;
    }

    /**
     * computes average resolution time for the valid tickets of a user
     */
    default double averageResolutionTime(final User user,
                                     final int month) {
        int differences = 0;
        List<Ticket> closedTickets = closedPertinentTickets(user, month);

        for (Ticket ticket : closedTickets) {
            LocalDate solvedAtDate = LocalDate.parse(ticket.getSolvedAt());
            LocalDate assignedAtDate = LocalDate.parse(ticket.getAssignedAt());

            differences += (int) java.time.temporal.ChronoUnit.DAYS
                    .between(assignedAtDate, solvedAtDate) + 1;
        }

        if (closedTickets.isEmpty()) {
            return 0.0;
        }
        return (double) differences / closedTickets.size();
    }


    /**
     * standard deviation formula
     */
    default double standardDeviation(final int bug, final int feature, final int ui) {
        double mean = averageResolvedTicketType(bug, feature, ui);
        double variance = (Math.pow(bug - mean, 2) + Math.pow(feature - mean, 2)
                + Math.pow(ui - mean, 2)) / DBL3;
        return Math.sqrt(variance);
    }

    /**
     * computes ticket diversity factor
     */
    default double ticketDiversityFactor(final int bug, final int feature, final int ui) {
        double mean = averageResolvedTicketType(bug, feature, ui);
        if (mean == 0.0) {
            return 0.0;
        }
        double std = standardDeviation(bug, feature, ui);
        return std / mean;
    }

    /**
     * @return number of closed tickets
     */
    default int closedTickets(final User user, final int month) {
        if (user == null || !user.getRole().equals("developer")
                || user.getAssignedTickets().isEmpty()) {
            ErrLogger.getInstance().logException("user object is null / is not okay");
        }
        int closeTickets = closedPertinentTickets(user, month).size();
        return closeTickets;
    }

    /**
     * abstract method implemented by the different seniorities Users
     */
    double  performanceScore(User user, int month);

    /**
     * copmutes no of closed tickets by type
     */
    default int getClosedByType(final User user, final int month,
                                final String type) {
        int num = 0;
        List<Ticket> tickets = closedPertinentTickets(user, month);
        for (Ticket ticket : tickets) {
            if (ticket.getType().equalsIgnoreCase(type)) {
                num++;
            }
        }
        return num;
    }

    /**
     * @return the seniority of the developer handling edge cases
     */
    default String seniorityOfDev(final User user) {
        if (user == null || !user.getRole().equals("developer")
                || user.getAssignedTickets().isEmpty()) {
            ErrLogger.getInstance().logException("user object is null / is not okay");
        }
        return user.getSeniority().toString();
    }
}
