package entities;

/**
 * Enum for frequency of a ticket, describing how often the reported problem happend It also holds
 * weights used in statistics (final reports) depending on this ticket field
 */
public enum Frequency {
    RARE(1),
    OCCASIONAL(2),
    FREQUENT(3),
    ALWAYS(4);

    private final int weight;

    Frequency(final int weight) {
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }
}
