package utils;

/**
 * Enumeration representing the lifecycle phases of the application
 */
public enum PHASE {
    TESTING, DEVELOPING, DECIDING;
    /**
     *  Returns the next phase in the application lifecycle. transition is cyclic:
     *  calling this method on the last phase returns the first phase.
     */
    public PHASE next() {
        return values()[(ordinal() + 1) % values().length];
    }
}
