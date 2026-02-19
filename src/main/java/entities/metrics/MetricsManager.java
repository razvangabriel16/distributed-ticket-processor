package entities.metrics;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Setter;

import static main.App.MAPPER;

/**
 * Manager class that coordinates the processing of metrics using a specific strategy.
 * Acts as a context in the Strategy pattern, delegating metric calculations to
 * the configured MetricStrategy implementation.
 */
public class MetricsManager {
    @Setter
    private final MetricStrategy strategy;
    /**
     * Constructs a MetricsManager with the specified strategy
     * @param strategy the metric strategy to use for calculations
     */
    public MetricsManager(final MetricStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * Processes and aggregates multiple metric calculations into a comprehensive analyss.
     * Combines total counts, type distributions, priority distributions, and
     * strategy-specific metrics into a single report {@code {ObjectNode}}
     * @param total the total ticket count metric
     * @param byType the ticket distribution by type metric
     * @param byPriority the ticket distribution by priority metric
     * @param customerImpact the strategy-specific impact/risk metric
     * @param label the key to use for storing the strategy-specific metric in the report
     * @return an ObjectNode containing all aggregated metrics in a structured report format
     */
    public ObjectNode processMetrics(
            final ObjectNode total,
            final ObjectNode byType,
            final ObjectNode byPriority,
            final ObjectNode customerImpact,
            final String label
    ) {
        ObjectNode report = MAPPER.createObjectNode();

        report.setAll(total);
        report.setAll(byType);
        report.setAll(byPriority);

        ObjectNode impactWrapper = MAPPER.createObjectNode();
        impactWrapper.setAll(customerImpact);
        report.set(label, impactWrapper);

        return report;
    }
}
