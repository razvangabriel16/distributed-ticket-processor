package entities;

/**
 * Enumeration representing the customer demand phases of a ticket. It holds coefficients
 * for math calculation in the final reports
 */
public enum CustomerDemand {
    LOW(1),
    MEDIUM(3),
    HIGH(6),
    VERY_HIGH(10);
    private final int weight;

    CustomerDemand(final int weight) {
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }
}
