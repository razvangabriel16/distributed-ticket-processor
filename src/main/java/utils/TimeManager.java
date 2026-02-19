package utils;

import entities.Milestone;
import entities.User;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.ArrayList;

import static utils.ErrLogger.INT3;

/**
 *  Utility class responsible for managing application time progress.
 *  Tracks the current date and processes all time-dependent
 *  updates such as: milestone state changes and scheduled events,
 *  advancing day by day when the date change
 */
public class TimeManager {
    @Getter
    private LocalDate currentDate;

    @Getter @Setter
    private List<User> users;

    @Getter @Setter
    private List<Milestone> milestones = new ArrayList<>();

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private LocalDate previousDate;

    /**
     * Constructs a {@code TimeManager} with no initial date set
     */
    public TimeManager() {
        this.currentDate = null;
        this.previousDate = null;
    }

    /**
     * Initializes the current application date
     * @param dateStr the initial date in {@code yyyy-MM-dd} format
     */
    public void setInitialDate(final String dateStr) {
        this.currentDate = LocalDate.parse(dateStr, formatter);
        this.previousDate = currentDate;
    }

    /**
     * Updates the current date and processes all intermediate days
     * If the new date is after the current date each intervening day
     * is processed sequentially to ensure correct time-based updates
     * @param newDateStr the new date in {@code yyyy-MM-dd} format
     * @param milestoness the list of milestones to be updated daily
     * @param users the list of users involved in time-based processing
     */
    public void updateDate(final String newDateStr, final List<Milestone> milestoness,
                           final List<User> users) {
        LocalDate newDate = LocalDate.parse(newDateStr, formatter);

        if (currentDate == null) {
            currentDate = newDate;
            previousDate = newDate;
            return;
        }

        if (newDate.isAfter(currentDate)) {
            LocalDate dateToProcess = currentDate.plusDays(1);
            while (!dateToProcess.isAfter(newDate)) {
                processDay(dateToProcess, milestoness, users);
                dateToProcess = dateToProcess.plusDays(1);
            }
        }

        previousDate = currentDate;
        currentDate = newDate;

    }

    /**
     * Processes all daily updates for a specific date
     * @param day the date being processed
     * @param milestoness the milestones affected on this day
     * @param userss the users involved in daily processing
     */
    private void processDay(final LocalDate day, final List<Milestone> milestoness,
                            final List<User> userss) {
        String dayStr = day.format(formatter);

        if (userss == null || userss.isEmpty()) {
            return;
        }

        if (milestoness == null || milestoness.isEmpty()) {
            return;
        }

        ErrLogger errorLogger = ErrLogger.getInstance();
        errorLogger.logException("From TimeManager Class: "
                + currentDate.toString() + "      "
                + previousDate.toString());

        for (Milestone milestone : milestoness) {
            milestone.updateDailyState(dayStr);
        }

        checkTimeBasedEvents(day);
    }

    /**
     * To be completed
     */
    private void checkTimeBasedEvents(final LocalDate day) {
        //to be completed
    }

    public String getCurrentDateStr() {
        return currentDate.format(formatter);
    }

    /**
     * Calculates the number of days between two dates, inclusive
     * @param start the start date
     * @param end the end date
     * @return the number of days between the two dates, inclusive
     */
    public int daysBetweenInclusive(final LocalDate start, final LocalDate end) {
        return (int) Math.abs(ChronoUnit.DAYS.between(start, end)) + 1;
    }

    /**
     * Boolean method to check if passed 3 days from a reference date
     * @param referenceDate the date from which to measure elapsed time
     * @return {@code true} if three or more days have passed, {@code false} otherwise
     */
    public boolean isThreeDaysPassed(final LocalDate referenceDate) {
        long daysPassed = ChronoUnit.DAYS.between(referenceDate, currentDate);
        return daysPassed >= INT3;
    }

    /**
     * Returns the number of days elapsed since a reference date
     */
    public long getDaysPassed(final LocalDate referenceDate) {
        return ChronoUnit.DAYS.between(referenceDate, currentDate);
    }
}
