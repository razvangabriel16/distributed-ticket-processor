package main;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import entities.Command;
import entities.ExpertiseArea;
import entities.Seniority;
import entities.User;
import utils.ErrLogger;
import utils.PHASE;

/**
 * main.App represents the main application logic that processes input commands,
 * generates outputs, and writes them to a file
 * @see <a href="https://ocw.cs.pub.ro/courses/poo-ca-cd/teme/2025/b73f56dc-17a1-42ac-bd7e-d57f3caaf9fd/tema-2">
 *      Engine FUll Documentation Rules
 *      </a>
 */
public final class App {
    private App() {
    }

    public static final ObjectMapper MAPPER = new ObjectMapper();

    private static final String INPUT_USERS_FIELD = "input/database/users.json";

    private static final ObjectWriter WRITER =
            new ObjectMapper().writer().withDefaultPrettyPrinter();

    /**
     * Runs the application: reads commands from an input file,
     * processes them, generates results, and writes them to an output file
     *
     * @param inputPath path to the input file containing commands
     * @param outputPath path to the file where results should be written
     */
    public static void run(final String inputPath, final String outputPath) {
        ErrLogger.reset();
        List<ObjectNode> outputs = new ArrayList<>();

        /*
            Load initial user data and commands. we strongly recommend using jackson library.
            you can use the reading from hw1 as a reference.
            however you can use some of the more advanced features of
            jackson library, available here: https://www.baeldung.com/jackson-annotations
        */
        ErrLogger errorLogger = ErrLogger.getInstance();
        errorLogger.logException("New TEST _____________________ ");
        File usersFile = new File(INPUT_USERS_FIELD);
        ArrayList<User> users = new ArrayList<>();
        try {
            JsonNode jsonNode = MAPPER.readTree(usersFile);
            if (jsonNode.isArray()) {
                for (JsonNode userNode : jsonNode) {
                    User user = new User.Builder(
                            userNode.get("username").asText(),
                            userNode.get("email").asText(),
                            userNode.get("role").asText()
                    )
                            .hireDate(userNode.has("hireDate")
                                    ? userNode.get("hireDate").asText() : null)
                            .expertiseArea(userNode.has("expertiseArea")
                                    ? ExpertiseArea.valueOf(userNode.
                                    get("expertiseArea").asText()) : null)
                            .seniority(userNode.has("seniority")
                                    ? Seniority.valueOf(userNode.
                                    get("seniority").asText()) : null)
                            .subordinates(userNode.has("subordinates")
                                    ? MAPPER.convertValue(userNode.
                                    get("subordinates"), String[].class) : null)
                            .build();
                    users.add(user);
                    System.out.println(user.toString());
                }
            }
        } catch (IOException e) {
            System.out.println("Error: File not found");
            errorLogger.logException(e, "File not found: " + INPUT_USERS_FIELD);
        }
        errorLogger.setUsers(users);
        errorLogger.setPhase(PHASE.TESTING);
        errorLogger.setTicketIdGlobal(0);
        errorLogger.initializeTimeManager();
        // processing commands
        File inputFile = new File(inputPath);
        ArrayList<Command> inputCommands = new ArrayList<>();
        try {
            JsonNode jsonNode = MAPPER.readTree(inputFile);
            if (jsonNode.isArray()) {
                for (JsonNode commandNode: jsonNode) {
                    Command command = new Command();
                    command.setCommand(commandNode.get("command").asText());
                    command.setUsername(commandNode.get("username").asText());
                    command.setTimestamp(commandNode.get("timestamp").asText());
                    if (commandNode.has("params")) {
                        command.unpackParams(commandNode.get("params"));
                    }
                    inputCommands.add(command);
                    errorLogger.setInputCommands(inputCommands);
                    errorLogger.setTimestamp(command.getTimestamp());
                    command.handle(outputs, commandNode);
                    System.out.println(command.toString());
                    errorLogger.logException("From App Class: "
                            + command.getTimestamp() + "----"
                            + errorLogger.getPhase() + "----"
                            + errorLogger.getTimeManager().getCurrentDateStr());
                }
            }
        } catch (IOException e) {
            System.out.println("Error: File not found");
            errorLogger.logException(e, "File not found: " + inputFile);
        }
        //created objectnodes for output, added them to outputs list.
        errorLogger.close();
        try {
            File outputFile = new File(outputPath);
            outputFile.getParentFile().mkdirs();
            WRITER.withDefaultPrettyPrinter().writeValue(outputFile, outputs);
        } catch (IOException e) {
            System.out.println("error writing to output file: " + e.getMessage());
        }
    }
}
