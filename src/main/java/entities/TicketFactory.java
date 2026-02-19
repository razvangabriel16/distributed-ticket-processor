package entities;

/**
 * Abstract factory defining the Factory Method for creating {@link Ticket} objects.
 * This class represents the Creator in the Factory Method design pattern.
 * Concrete factories extend this class and implement {@link #createTicket()}
 * to instantiate specific {@link Ticket} subtypes.
 */
public abstract class TicketFactory {

    /**
     * Creates and returns a {@link Bug} or {@link FeatureRequest} or {@link UIFeedback} ticket
     * using the intern configured builder. @return a fully constructed ticket of that type
     */
    public abstract Ticket createTicket();
    /**
     * Executes the logic associated with a newly created ticket.
     */
    public void logicTicket() {
        Ticket t = createTicket();
        t.logic();
    }
}
