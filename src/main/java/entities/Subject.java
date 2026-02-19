package entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Subject class for Observer Design Pattern extended by
 * {@link Milestone#}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Subject {

    private List<Observer> observers = new ArrayList<>();
    /**
     * Add an observer to the list for beeing notified
     */
    public void addObserver(final Observer o) {
        observers.add(o);
    }
    /**
     * Remove an observer from the list.
     */
    public void removeObserver(final Observer o) {
        observers.remove(o);
    }
    /**
     * Triggers a notification to the observers recorded in the list
     */
    protected void notifyObservers(final String status) {
        for (Observer o : observers) {
            o.update(status);
        }
    }
}
