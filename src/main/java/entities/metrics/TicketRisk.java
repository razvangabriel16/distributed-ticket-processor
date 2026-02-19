package entities.metrics;

import com.fasterxml.jackson.databind.node.ObjectNode;
import entities.Bug;
import entities.FeatureRequest;
import entities.Ticket;
import entities.UIFeedback;

import java.util.ArrayList;
import java.util.List;

import static main.App.MAPPER;
import static utils.ErrLogger.INT24;
import static utils.ErrLogger.INT25;
import static utils.ErrLogger.INT50;
import static utils.ErrLogger.INT49;
import static utils.ErrLogger.INT74;
import static utils.ErrLogger.INT75;
import static utils.ErrLogger.INT100;
import static utils.ErrLogger.INT11;
import static utils.ErrLogger.INT12;
import static utils.ErrLogger.INT20;

/**
 * Concrete strategy for calculating ticket risk metrics.
 * Implements risk assessment for different ticket types based on
 * their specific attributes and provides risk categorization.
 */
public class TicketRisk implements MetricStrategy {
    /**
     * Transforms a numerical risk score into a categorical risk level
     * @param val the numerical risk score (0-100)
     * @return the corresponding risk category: NEGLIGIBLE, MODERATE, SIGNIFICANT, MAJOR, or UNKNOWN
     */
    String transform(final double val) {
        if (val >= 0 && val <= INT24) {
            return "NEGLIGIBLE";
        } else if (val >= INT25 && val <= INT49) {
            return "MODERATE";
        } else if (val >= INT50 && val <= INT74) {
            return "SIGNIFICANT";
        } else if (val >= INT75 && val <= INT100) {
            return "MAJOR";
        }
        return "UNKNOWN";
    }

    /**
     * Calculates type-specific risk metrics for tickets.
     * For BUG tickets: risk based on frequency and severity
     * For UI_FEEDBACK tickets: risk based on bussiness value and usability score
     * For FEATURE_REQUEST tickets: risk based on bussiness value and customer demand
     * @param tickets the list of tickets to analyze for risk
     * @return an ObjectNode containing risk assessments for each ticket type
     */
    @Override
    public ObjectNode totalTicketsParticular(final List<Ticket> tickets) {
        ObjectNode root = MAPPER.createObjectNode();
        List<Double> list1 = new ArrayList<>();
        List<Double> list2 = new ArrayList<>();
        List<Double> list3 = new ArrayList<>();
        for (Ticket ticket : tickets) {
            double aux = 0.0;
            switch (ticket.getType()) {
                case "BUG":
                    aux = ((Bug) ticket).getFrequency().getWeight();
                    aux *= ((Bug) ticket).getSeverity().getWeight();
                    aux = calculateImpactFinal(aux, INT12);
                    list1.add(aux);
                    break;

                case "UI_FEEDBACK":
                    aux = ((UIFeedback) ticket).getBusinessValue().getWeight();
                    aux *= INT11 - ((UIFeedback) ticket).getUsabilityScore();
                    aux = calculateImpactFinal(aux, INT100);
                    list2.add(aux);
                    break;

                case "FEATURE_REQUEST":
                    aux = ((FeatureRequest) ticket).getBussinessValue().getWeight();
                    aux += ((FeatureRequest) ticket).getCustomerDemand().getWeight();
                    aux = calculateImpactFinal(aux, INT20);
                    list3.add(aux);
                    break;
                default:
                    break;
            }
        }
        double val1 = calculateAverageImpact(list1);
        double val2 = calculateAverageImpact(list2);
        double val3 = calculateAverageImpact(list3);
        root.put("BUG", transform(val1));
        root.put("UI_FEEDBACK", transform(val2));
        root.put("FEATURE_REQUEST", transform(val3));
        return root;
    }
}
