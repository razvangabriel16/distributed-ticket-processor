package entities;

/**
 * Enumeration representing the status phases of a ticket (extended also for a milestone)
 */
public enum Status {
    OPEN,
    IN_PROGRESS,
    RESOLVED,
    CLOSED,
    COMPLETED,
    ACTIVE;
    /**
     * For ticket it claps .next to CLOSED
     */
    public Status next() {
        if (this == CLOSED) {
            return CLOSED;
        }
        return values()[(ordinal() + 1)];
    }
}
