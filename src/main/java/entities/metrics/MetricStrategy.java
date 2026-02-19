package entities.metrics;

import com.fasterxml.jackson.databind.node.ObjectNode;
import entities.Ticket;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static main.App.MAPPER;
import static utils.ErrLogger.DBL100;

/**
 * Strategy interface for calculating and processing various metrics for tickets.
 * Provides default implementations for common metric calculations and requires
 * implementation of type-specific metric calculations.
 */
public interface MetricStrategy {
    /**
     * Calculates the total number of tickets in the provided ticketlist
     * @param tickets the list of tickets to count
     * @return an ObjectNode containing the total ticket count under key "totalTickets"
     */
    default ObjectNode totalNumber(final List<Ticket> tickets) {
        ObjectNode root = MAPPER.createObjectNode();
        root.put("totalTickets", tickets.size());
        return root;
    }
    /**
     * Calculates the distribution of tickets by their type (BUG, FEATURE_REQUEST, UI_FEEDBACK).
     * @param tickets the list of tickets to analyze
     * @return an ObjectNode containing total count and breakdown by ticket type
     */
    default ObjectNode totalTicketsType(final List<Ticket> tickets) {
        ObjectNode root = MAPPER.createObjectNode();
        root.put("totalTickets", tickets.size());
        Map<String, Integer> ticketsByType = new HashMap<>();
        ticketsByType.put("BUG", 0);
        ticketsByType.put("FEATURE_REQUEST", 0);
        ticketsByType.put("UI_FEEDBACK", 0);
        for (Ticket ticket : tickets) {
            String type = ticket.getType();
            ticketsByType.put(type, ticketsByType.getOrDefault(type, 0) + 1);
        }
        ObjectNode typeNode = MAPPER.createObjectNode();
        for (Map.Entry<String, Integer> entry : ticketsByType.entrySet()) {
            typeNode.put(entry.getKey(), entry.getValue());
        }
        root.set("ticketsByType", typeNode);
        return root;
    }

    /**
     * Calculates the distribution of tickets by their bussiness priority.
     * @param tickets the list of tickets to analyze
     * @return an ObjectNode containing breakdown of tickets by priority level
     */
    default ObjectNode totalTicketsPriority(final List<Ticket> tickets) {
        ObjectNode root = MAPPER.createObjectNode();
        Map<String, Integer> ticketsByPriority = new HashMap<>();
        ticketsByPriority.put("LOW", 0);
        ticketsByPriority.put("MEDIUM", 0);
        ticketsByPriority.put("HIGH", 0);
        ticketsByPriority.put("CRITICAL", 0);

        for (Ticket ticket : tickets) {
            String priority = ticket.getBusinessPriority().toString();
            ticketsByPriority.put(priority, ticketsByPriority.get(priority) + 1);
        }

        ObjectNode typeNodee = MAPPER.createObjectNode();
        for (Map.Entry<String, Integer> entry : ticketsByPriority.entrySet()) {
            typeNodee.put(entry.getKey(), entry.getValue());
        }
        root.set("ticketsByPriority", typeNodee);
        return root;
    }

    /**
     * Calculates a normalized impact score capped at a maximum value (default method)
     * @param baseScore the raw impact score to normalize
     * @param maxValue the maximum possible value for normalization
     * @return the normalized impact score, capped at DBL100
     */
    default double calculateImpactFinal(final double baseScore, final double maxValue) {
        return Math.min(DBL100, (baseScore * DBL100) / maxValue);
    }

    /**
     * Calculates the average impact from a list of scores (default method)
     * @param scores the list of impact scores to average
     * @return the average of the scores, or 0.0 if the list is empty
     */
    default double calculateAverageImpact(final List<Double> scores) {
        return scores.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }

    /**
     * Calculates type-specific metrics for tickets
     * Must be implemented by concrete strategy classes to provide
     * specialized calculations for different metric types
     * @param tickets the list of tickets to analyze
     * @return an ObjectNode containing type-specific metric calculations
     */
     ObjectNode totalTicketsParticular(List<Ticket> tickets);
}
