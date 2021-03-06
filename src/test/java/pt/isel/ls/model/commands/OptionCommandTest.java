package pt.isel.ls.model.commands;

import org.junit.BeforeClass;
import org.junit.Test;
import pt.isel.ls.model.Router;
import pt.isel.ls.model.commands.common.CommandHandler;
import pt.isel.ls.model.commands.common.CommandRequest;
import pt.isel.ls.model.commands.common.Method;
import pt.isel.ls.model.commands.results.OptionResult;
import pt.isel.ls.model.paths.Path;
import pt.isel.ls.model.paths.PathTemplate;
import pt.isel.ls.view.View;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class OptionCommandTest {
    private static Router router = new Router();

    @BeforeClass
    public static void addRoutes() {
        //Path Templates (shared between different commands)
        PathTemplate roomsTemplate = new PathTemplate("/rooms");
        PathTemplate labelsTemplate = new PathTemplate("/labels");

        //GET commands
        router.addRoute(Method.GET, roomsTemplate, new GetRoomsCommand());
        router.addRoute(Method.GET, new PathTemplate("/rooms/{rid}"), new GetRoomByIdCommand());
        router.addRoute(Method.GET, new PathTemplate("/rooms/{rid}/bookings/{bid}"),
                new GetBookingByRoomAndBookingIdCommand());
        router.addRoute(Method.GET, new PathTemplate("/users/{uid}"), new GetUserByIdCommand());
        router.addRoute(Method.GET, new PathTemplate("/users/{uid}/bookings"), new GetBookingsByUserIdCommand());
        router.addRoute(Method.GET, labelsTemplate, new GetLabelsCommand());
        router.addRoute(Method.GET, new PathTemplate("/labels/{lid}/rooms"), new GetRoomsWithLabelCommand());

        //POST commands
        router.addRoute(Method.POST, roomsTemplate, new PostRoomCommand());
        router.addRoute(Method.POST, new PathTemplate("/rooms/{rid}/bookings"), new PostBookingInRoomCommand());
        router.addRoute(Method.POST, new PathTemplate("/users"), new PostUserCommand());
        router.addRoute(Method.POST, labelsTemplate, new PostLabelCommand());

        //EXIT command
        router.addRoute(Method.EXIT, new PathTemplate("/"), new ExitCommand());

        // OPTION command
        router.addRoute(Method.OPTION, new PathTemplate("/"), new OptionCommand());
    }


    @Test
    public void optionExecuteTest() throws Exception {
        CommandRequest cmd = new CommandRequest(new Path("/"), null,null, router);
        CommandHandler handler = router.findRoute(Method.OPTION, cmd.getPath());

        OptionResult res = (OptionResult) handler.execute(cmd);
        assertNotNull(res);
        assertTrue(res.hasResults());
        View view = View.findView(res, null);
        view.render(System.out);
    }
}
