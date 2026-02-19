package entities.performance;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Setter;

import static main.App.MAPPER;
import static utils.ErrLogger.DBL100;

/**
 * Wrapper & helper for output in that command' specific format.
 */
public class PerformanceManager {
    @Setter
    private PerformanceStrategy performanceStrategy;
    public PerformanceManager(final PerformanceStrategy performanceStrategy) {
        this.performanceStrategy = performanceStrategy;
    }
    /**
     * Serializes the input params in an ObjectNode
     */
    public ObjectNode processPerformance(
            final String username,
            final int closedTickets,
            final double performanceScore,
            final double avgResolutionTime,
            final String seniority
    ) {
        ObjectNode report = MAPPER.createObjectNode();
        report.put("username", username);
        report.put("closedTickets", closedTickets);
        report.put("performanceScore",
                Math.round(performanceScore * DBL100) / DBL100);
        report.put("averageResolutionTime",
                Math.round(avgResolutionTime * DBL100) / DBL100);
        report.put("seniority", seniority);
        return report;
    }

}
