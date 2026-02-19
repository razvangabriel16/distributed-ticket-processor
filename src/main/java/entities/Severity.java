package entities;

/**
 * Enum for severity of a ticket. It also holds
 * weights used in statistics (final reports) depending
 * on this ticket field
 */
public enum Severity {
    MINOR(1),
    MODERATE(2),
    SEVERE(3);
    private final int weight;

    Severity(final int weight) {
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }
}
