package entities.metrics;

import com.fasterxml.jackson.databind.node.ObjectNode;
import entities.Bug;
import entities.FeatureRequest;
import entities.Ticket;
import entities.UIFeedback;

import java.util.ArrayList;
import java.util.List;

import static main.App.MAPPER;
import static utils.ErrLogger.INT48;
import static utils.ErrLogger.INT100;
import static utils.ErrLogger.DBL100;


/**
 * Concrete strategy for calculating ticket customer impact metrics.
 * Implements impact assessment for different ticket types
 */
public class CustomerImpact implements MetricStrategy {
    /**
     * Calculates type-specific customer impact metrics for tickets.
     * For completness:
     * @see <a href="https://ocw.cs.pub.ro/courses/poo-ca-cd/teme/2025/b73f56dc-17a1-42ac-bd7e-d57f3caaf9fd/tema-2">
     *      Engine FUll Documentation Rules
     *      </a>
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
                    aux *= ((Bug) ticket).getBusinessPriority().getWeight();
                    aux *= ((Bug) ticket).getSeverity().getWeight();
                    aux = calculateImpactFinal(aux, INT48);
                    list1.add(aux);
                    break;

                case "UI_FEEDBACK":
                    aux = ((UIFeedback) ticket).getBusinessValue().getWeight();
                    aux *= ((UIFeedback) ticket).getUsabilityScore();
                    aux = calculateImpactFinal(aux, INT100);
                    list2.add(aux);
                    break;

                case "FEATURE_REQUEST":
                    aux = ((FeatureRequest) ticket).getBussinessValue().getWeight();
                    aux *= ((FeatureRequest) ticket).getCustomerDemand().getWeight();
                    aux = calculateImpactFinal(aux, INT100);
                    list3.add(aux);
                    break;
                default:
                    break;
            }
        }
        double val1 = calculateAverageImpact(list1);
        val1 = Math.round(val1 * DBL100) / DBL100;
        double val2 = calculateAverageImpact(list2);
        val2 = Math.round(val2 * DBL100) / DBL100;
        double val3 = calculateAverageImpact(list3);
        val3 = Math.round(val3 * DBL100) / DBL100;
        root.put("BUG", val1);
        root.put("UI_FEEDBACK", val2);
        root.put("FEATURE_REQUEST", val3);
        return root;
    }
}
