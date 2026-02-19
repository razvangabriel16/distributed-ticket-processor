package entities;

/**
 * Enum for seniority of a developer. It also holds
 * weights used in statistics (final reports) depending
 * on this user field
 */
public enum Seniority {
    JUNIOR(5),
    MID(15),
    SENIOR(30);
    private final int weight;

    Seniority(final int weight) {
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }
}
