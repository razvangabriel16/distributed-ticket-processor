package entities;

/**
 * Enum with Business Priorities for a ticket. It provides a cycling next() method
 */
public enum BussinessPriority {
    LOW(1),
    MEDIUM(2),
    HIGH(3),
    CRITICAL(4);
    private final int weight;

    public BussinessPriority next() {
        if (this == CRITICAL) {
            return CRITICAL;
        }
        return values()[(ordinal() + 1)];
    }
    BussinessPriority(final int weight) {
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }
}
