package utils;

import entities.Command;
import entities.Milestone;
import entities.User;
import lombok.Getter;
import lombok.Setter;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility Singleton class responsible for centralized error logging and
 * global time/phase management within the application. It holds application wide
 * timestamps, phase transitions based on elapsed time, logging of exceptions and
 * custom messages to a file
 */
public final class ErrLogger {
    /* Constants for math logic and metadata defining */
    public static final String LOG_FILE_PATH = "err_log2.txt";
    public static final int INT10 = 10;
    public static final int INT12 = 12;
    public static final int INT48 = 48;
    public static final int INT100 = 100;
    public static final double DBL100 = 100.0;
    public static final int INT70 = 70;
    public static final int INT20 = 20;
    public static final int INT24 = 24;
    public static final int INT25 = 25;
    public static final int INT49 = 49;
    public static final int INT50 = 50;
    public static final int INT74 = 74;
    public static final int INT75 = 75;
    public static final int INT11 = 11;
    public static final int INT3 = 3;
    public static final double DBL3 = 3.0;
    public static final double DBL0_5 = 0.5;
    public static final double DBL0_3 = 0.3;
    public static final double DBL0_7 = 0.7;

    @Getter
    private String globalTimestamp;
    private static ErrLogger singleInstance;
    private PrintWriter writer;
    private boolean writerInit = false;
    @Getter @Setter
    private LocalDate phaseStartDate;
    @Getter @Setter
    private TimeManager timeManager = new TimeManager();
    @Setter @Getter
    private int ticketIdGlobal;
    @Setter @Getter
    private List<User> users;
    @Getter @Setter
    private PHASE phase = PHASE.TESTING;
    @Setter @Getter
    private List<Command> inputCommands;
    @Getter @Setter
    private List<Milestone> milestones = new ArrayList<>();

    /**
     * Update global Timestamp of the App, wrapping logic for changing the current state
     * @param timestamp is the new timestamp, parsed from the command loop
     * {@link main.App#run(String, String)}
     * It notifies the {@link TimeManager} to update users and milestones
     */
    public void setTimestamp(final String timestamp) {
        this.globalTimestamp = timestamp;
        if (timeManager != null && milestones != null && users != null) {
            timeManager.updateDate(timestamp, milestones, users);
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate currentDate = LocalDate.parse(timestamp, formatter);

        if (phaseStartDate == null) {
            phaseStartDate = currentDate;
            return;
        }

        long daysBetween = ChronoUnit.DAYS.between(phaseStartDate, currentDate);

        if (daysBetween >= INT12) {
            phase = phase.next();
            phaseStartDate = currentDate;
        }

    }

    /**
     * Private constructor without parameters
     * Initializes the log file writer in append mode.
     * If the file cannot be opened, logging is silently disabled.
     */
    private ErrLogger() {
        try {
            writer = new PrintWriter(new FileWriter(LOG_FILE_PATH, true));
            writerInit = true;
        } catch (IOException e) {
            writerInit = false;
        }
    }

    /**
     * Singleton helper with lazy initialization, returns the unique instance {@code ErrLogger}
     */
    public static ErrLogger getInstance() {
        if (singleInstance == null) {
            singleInstance = new ErrLogger();
        }
        return singleInstance;
    }

    /**
     * Reset helper for Singleton unique instance, to use it at each new independent test parsing
     * {@see main.App#run(String, String)}
     */
    public static void reset() {
        if (singleInstance != null) {
            singleInstance.close();
        }
        singleInstance = null;
    }

    /**
     * Writes to the logFile the stackTrace with a message of a user for explicity increase
     * @param e the exception whose stack trace will be logged
     * @param message a custom message describing the context of the error
     */
    public void logException(final Exception e, final String message) {
        if (!writerInit) {
            return;
        }
        writer.println(message);
        e.printStackTrace(writer);
        writer.println();
        writer.flush();
    }

    /**
     * Writes a custom message to the error log file without an exception
     */
    public void logException(final String message) {
        if (!writerInit) {
            return;
        }
        writer.println(message);
        writer.println();
        writer.flush();
    }

    /**
     * Safely closes the log file writer and releases all associated resources
     */
    public void close() {
        if (!writerInit) {
            return;
        }
        if (writerInit && writer != null) {
            try {
                writer.flush();
                writer.close();
                writerInit = false;
            } catch (Exception e) {
                System.err.println(e);
            }
        }
    }

    /**
     * Returns the current application date as maintained by the {@link TimeManager}
     */
    public String getCurrentDate() {
        return timeManager.getCurrentDateStr();
    }

    /**
     * Initializes the {@link TimeManager} with the current users and milestones
     */
    public void initializeTimeManager() {
        timeManager.setMilestones(milestones);
        timeManager.setUsers(users);
    }
}
