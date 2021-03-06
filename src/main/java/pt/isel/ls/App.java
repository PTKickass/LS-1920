package pt.isel.ls;

import pt.isel.ls.model.Router;
import pt.isel.ls.model.commands.DeleteBookingInRoomCommand;
import pt.isel.ls.model.commands.ExitCommand;
import pt.isel.ls.model.commands.GetBookingByRoomAndBookingIdCommand;
import pt.isel.ls.model.commands.GetBookingsByRoomIdCommand;
import pt.isel.ls.model.commands.GetBookingsByUserIdCommand;
import pt.isel.ls.model.commands.GetBookingsCreateCommand;
import pt.isel.ls.model.commands.GetHomeCommand;
import pt.isel.ls.model.commands.GetLabelByIdCommand;
import pt.isel.ls.model.commands.GetLabelsCommand;
import pt.isel.ls.model.commands.GetLabelsCreateCommand;
import pt.isel.ls.model.commands.GetRoomByIdCommand;
import pt.isel.ls.model.commands.GetRoomsCommand;
import pt.isel.ls.model.commands.GetRoomsCreateCommand;
import pt.isel.ls.model.commands.GetRoomsSearchCommand;
import pt.isel.ls.model.commands.GetRoomsWithLabelCommand;
import pt.isel.ls.model.commands.GetTimeCommand;
import pt.isel.ls.model.commands.GetUserByIdCommand;
import pt.isel.ls.model.commands.GetUsersCommand;
import pt.isel.ls.model.commands.GetUsersCreateCommand;
import pt.isel.ls.model.commands.ListenCommand;
import pt.isel.ls.model.commands.OptionCommand;
import pt.isel.ls.model.commands.PostBookingInRoomCommand;
import pt.isel.ls.model.commands.PostLabelCommand;
import pt.isel.ls.model.commands.PostRoomCommand;
import pt.isel.ls.model.commands.PostUserCommand;
import pt.isel.ls.model.commands.PutBookingInRoomCommand;
import pt.isel.ls.model.commands.common.CommandHandler;
import pt.isel.ls.model.commands.common.CommandRequest;
import pt.isel.ls.model.commands.common.CommandResult;
import pt.isel.ls.model.commands.common.Headers;
import pt.isel.ls.model.commands.common.Method;
import pt.isel.ls.model.commands.common.Parameters;
import pt.isel.ls.model.commands.common.exceptions.ExitException;
import pt.isel.ls.model.commands.common.exceptions.ValidationException;
import pt.isel.ls.model.commands.sql.TransactionManager;
import pt.isel.ls.model.paths.Path;
import pt.isel.ls.model.paths.PathTemplate;
import pt.isel.ls.model.commands.common.ExitRoutine;
import pt.isel.ls.view.View;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Scanner;

import static pt.isel.ls.model.commands.sql.TransactionManager.CONNECTION_REFUSED_ERROR;
import static pt.isel.ls.model.commands.sql.TransactionManager.DUPLICATE_COLUMN_ERROR;

public class App {
    private static final TransactionManager trans = new TransactionManager(System.getenv("JDBC_DATABASE_URL"));
    private static final Router router = new Router();
    private static final LinkedList<ExitRoutine> exitRoutines = new LinkedList<>();

    private static final String VALID_COMMAND_FORMAT =
            "Please either use:"
                    + "\n{method} {path}"
                    + "\n{method} {path} {headers}"
                    + "\n{method} {path} {parameters}"
                    + "\n{method} {path} {headers} {parameters}";


    public static void main(String[] args) {
        addCommands();

        if (args.length > 0) {
            processCommand(args);
        } else {
            run();
        }

    }

    private static void run() {
        Scanner in = new Scanner(System.in);
        boolean running = true;
        while (running) {
            System.out.print("> ");
            String[] commands = in.nextLine().split(" ");

            if (!isCommandValid(commands)) {
                System.out.println("Wrong format. " + VALID_COMMAND_FORMAT);
            } else {
                running = processCommand(commands);
            }
        }
    }


    // Returned value determines if the app should continue running or not

    /**
     * Processes a command by getting the respective method, headers, parameters and path
     * @param commands The String array containing the command's information
     * @return whether the app should continue running or not
     */
    private static boolean processCommand(String[] commands) {
        Method method;
        try {
            method = Method.valueOf(commands[0].toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("Method \"" + commands[0] + "\" does not exist");
            System.out.println(VALID_COMMAND_FORMAT);
            return true;
        }

        Headers headers = null;
        Parameters params = null;
        Path path;

        try {
            switch (commands.length) {
                case 4:
                    headers = new Headers(commands[2]);
                    params = new Parameters(commands[3]);
                    break;
                case 3:
                    if (isHeader(commands[2])) {
                        headers = new Headers(commands[2]);
                    } else {
                        params = new Parameters(commands[2]);
                    }
                    break;
                default:
            }
            path = new Path(commands[1]);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage() + VALID_COMMAND_FORMAT);
            return true;
        }

        CommandRequest cmd = new CommandRequest(path, params, trans, router);

        CommandHandler handler = router.findRoute(method, cmd.getPath());
        if (handler == null) {
            System.out.println("Command not found");
            return true;
        }

        return executeCommand(headers, cmd, handler);
    }

    /**
     * Executes a command with the given information
     * @param headers Command headers to be used
     * @param cmd Command Request containing the command's parameters
     * @param handler Command Handler that's responsible for executing the given command
     * @return whether the app should continue running or not
     */
    private static boolean executeCommand(Headers headers, CommandRequest cmd, CommandHandler handler) {
        CommandResult result;
        try {
            result = handler.execute(cmd);
            if (result != null) {
                displayResult(result, headers);
                addExitRoutine(result);
            }
        } catch (SQLException e) {
            switch (e.getSQLState()) {
                case DUPLICATE_COLUMN_ERROR:
                    System.out.println("Error: Inserted data conflicts with an existing entry in the database.\n");
                    break;
                case CONNECTION_REFUSED_ERROR:
                    System.out.println("Error: Could not connect to database.\n");
                    break;
                default:
                    System.out.println(e.getMessage() + "\n");
            }
            return true;
        } catch (ValidationException e) {
            System.out.println(e.getMessage() + " (in: '" + e.getValidatedParam() + "')\n");
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage() + "\n");
            return true;
        }

        boolean isNull = result == null;
        if (isNull) {
            try {
                executeExitRoutines();
            } catch (ExitException e) {
                System.out.println(e.getMessage() + "\n");
            }
        }
        return !isNull;
    }

    /**
     * Displays the result obtained after executing a command
     * @param result The result of the command's execution
     * @param headers Command headers to be used
     */
    private static void displayResult(CommandResult result, Headers headers) {
        try {
            String filename = null;
            String viewFormat = null;

            // Get headers
            if (headers != null) {
                filename = headers.getFirst("file-name");
                viewFormat = headers.getFirst("accept");
            }
            // Get output stream
            OutputStream out = filename == null ? System.out : getFileStream(filename);

            // Present each entity
            View view = View.findView(result, viewFormat);
            if (view != null) {
                view.render(out);
            }

            // Close output stream only if it isn't System.out
            if (out != System.out) {
                out.close();
            }
        } catch (IOException e) {
            System.out.println("Failed to open output stream");
        }
    }

    private static OutputStream getFileStream(String fileName) throws FileNotFoundException {
        return new FileOutputStream(fileName);
    }

    private static boolean isCommandValid(String[] commands) {
        return commands.length > 1 && commands.length <= 4;
    }

    private static boolean isHeader(String command) {
        return command.contains(":") && !command.contains("=") && !command.contains("&");
    }

    private static void addExitRoutine(CommandResult result) {
        ExitRoutine exitRoutine = result.getExitRoutine();
        if (exitRoutine != null) {
            exitRoutines.add(exitRoutine);
        }
    }

    private static void executeExitRoutines() throws ExitException {
        for (ExitRoutine exitRoutine : exitRoutines) {
            exitRoutine.close();
        }
    }

    /**
     * Adds commands to the Router
     * New commands must be added here along with their Methods and Paths
     */
    private static void addCommands() {
        // GET commands
        router.addRoute(Method.GET, new PathTemplate("/rooms"), new GetRoomsCommand());
        router.addRoute(Method.GET, new PathTemplate("/rooms/search"), new GetRoomsSearchCommand());
        router.addRoute(Method.GET, new PathTemplate("/rooms/create"), new GetRoomsCreateCommand());
        router.addRoute(Method.GET, new PathTemplate("/rooms/{rid}"), new GetRoomByIdCommand());
        router.addRoute(Method.GET, new PathTemplate("/rooms/{rid}/bookings"), new GetBookingsByRoomIdCommand());
        router.addRoute(Method.GET, new PathTemplate("/rooms/{rid}/bookings/create"), new GetBookingsCreateCommand());
        router.addRoute(Method.GET, new PathTemplate("/rooms/{rid}/bookings/{bid}"),
                new GetBookingByRoomAndBookingIdCommand());

        router.addRoute(Method.GET, new PathTemplate("/users"), new GetUsersCommand());
        router.addRoute(Method.GET, new PathTemplate("/users/create"), new GetUsersCreateCommand());
        router.addRoute(Method.GET, new PathTemplate("/users/{uid}"), new GetUserByIdCommand());
        router.addRoute(Method.GET, new PathTemplate("/users/{uid}/bookings"), new GetBookingsByUserIdCommand());

        router.addRoute(Method.GET, new PathTemplate("/labels"), new GetLabelsCommand());
        router.addRoute(Method.GET, new PathTemplate("/labels/create"), new GetLabelsCreateCommand());
        router.addRoute(Method.GET, new PathTemplate("/labels/{lid}"), new GetLabelByIdCommand());
        router.addRoute(Method.GET, new PathTemplate("/labels/{lid}/rooms"), new GetRoomsWithLabelCommand());
        router.addRoute(Method.GET, new PathTemplate("/time"), new GetTimeCommand());
        router.addRoute(Method.GET, new PathTemplate("/"), new GetHomeCommand());


        // POST commands
        router.addRoute(Method.POST, new PathTemplate("/rooms/create"), new PostRoomCommand());
        router.addRoute(Method.POST, new PathTemplate("/rooms/{rid}/bookings/create"), new PostBookingInRoomCommand());
        router.addRoute(Method.POST, new PathTemplate("/users/create"), new PostUserCommand());
        router.addRoute(Method.POST, new PathTemplate("/labels/create"), new PostLabelCommand());

        // DELETE command
        router.addRoute(Method.DELETE, new PathTemplate("/rooms/{rid}/bookings/{bid}"),
                new DeleteBookingInRoomCommand());

        // PUT command
        router.addRoute(Method.PUT, new PathTemplate("/rooms/{rid}/bookings/{bid}"),
                new PutBookingInRoomCommand());

        // EXIT command
        router.addRoute(Method.EXIT, new PathTemplate("/"), new ExitCommand());

        // OPTION command
        router.addRoute(Method.OPTION, new PathTemplate("/"), new OptionCommand());

        // LISTEN command
        router.addRoute(Method.LISTEN, new PathTemplate("/"), new ListenCommand());
    }
}
