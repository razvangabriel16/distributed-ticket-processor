package entities.filters;

import entities.Ticket;

/**
 * Concrete specification for filtering tickets by a keyword.
 * Implements the Specification pattern for ticket containing keyword filtering criteria.
 */
public class KeywordFilter implements Specification<Ticket> {
    private final String wordToFind;

    public KeywordFilter(final String wordToFind) {
        this.wordToFind = wordToFind;
    }
    @Override

    /**
     * Tests whether a ticket matches the specified type.
     * @param ticket the ticket to test
     * @return true if the ticket's title/description contains the keyword, false otherwise
     */
    public boolean isSatisfiedBy(final Ticket item) {
        String title = item.getTitle();
        String description = item.getDescription();
        if (title != null && title.contains(wordToFind)) {
            return true;
        }

        if (description != null && description.contains(wordToFind)) {
            return true;
        }
        return false;
    }
}
