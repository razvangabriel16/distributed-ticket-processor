package entities;

/**
 * Enumeration representing the business value of a ticket. It holds coefficients
 * for math calculation in the final reports
 */
public enum BusinessValue {
    S(1),
    M(3),
    L(6),
    XL(10);
    private final int weight;

    BusinessValue(final int weight) {
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }
}
