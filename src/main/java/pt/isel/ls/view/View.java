package pt.isel.ls.view;

import pt.isel.ls.model.commands.common.CommandResult;
import pt.isel.ls.view.commandviews.html.DeleteBookingInRoomHtmlView;
import pt.isel.ls.view.commandviews.html.EmptyHtmlView;
import pt.isel.ls.view.commandviews.html.GetBookingByRoomAndBookingIdHtmlView;
import pt.isel.ls.view.commandviews.html.GetBookingsByRoomIdHtmlView;
import pt.isel.ls.view.commandviews.html.GetBookingsByUserIdHtmlView;
import pt.isel.ls.view.commandviews.html.GetBookingsCreateHtmlView;
import pt.isel.ls.view.commandviews.html.GetHomeHtmlView;
import pt.isel.ls.view.commandviews.html.GetLabelByIdHtmlView;
import pt.isel.ls.view.commandviews.html.GetLabelsCreateHtmlView;
import pt.isel.ls.view.commandviews.html.GetLabelsHtmlView;
import pt.isel.ls.view.commandviews.html.GetRoomByIdHtmlView;
import pt.isel.ls.view.commandviews.html.GetRoomsCreateHtmlView;
import pt.isel.ls.view.commandviews.html.GetRoomsHtmlView;
import pt.isel.ls.view.commandviews.html.GetRoomsSearchHtmlView;
import pt.isel.ls.view.commandviews.html.GetRoomsWithLabelHtmlView;
import pt.isel.ls.view.commandviews.html.GetTimeHtmlView;
import pt.isel.ls.view.commandviews.html.GetUserByIdHtmlView;
import pt.isel.ls.view.commandviews.html.GetUsersCreateHtmlView;
import pt.isel.ls.view.commandviews.html.GetUsersHtmlView;
import pt.isel.ls.view.commandviews.html.HttpResponseHtmlView;
import pt.isel.ls.view.commandviews.html.ListenHtmlView;
import pt.isel.ls.view.commandviews.html.NoRouteHtmlView;
import pt.isel.ls.view.commandviews.html.OptionHtmlView;
import pt.isel.ls.view.commandviews.html.PostBookingInRoomHtmlView;
import pt.isel.ls.view.commandviews.html.PostLabelHtmlView;
import pt.isel.ls.view.commandviews.html.PostRoomHtmlView;
import pt.isel.ls.view.commandviews.html.PostUserHtmlView;
import pt.isel.ls.view.commandviews.html.PutBookingInRoomHtmlView;
import pt.isel.ls.view.commandviews.plain.DeleteBookingInRoomPlainView;
import pt.isel.ls.view.commandviews.plain.EmptyPlainView;
import pt.isel.ls.view.commandviews.plain.GetBookingByRoomAndBookingIdPlainView;
import pt.isel.ls.view.commandviews.plain.GetBookingsByRoomIdPlainView;
import pt.isel.ls.view.commandviews.plain.GetBookingsByUserIdPlainView;
import pt.isel.ls.view.commandviews.plain.GetHomePlainView;
import pt.isel.ls.view.commandviews.plain.GetLabelByIdPlainView;
import pt.isel.ls.view.commandviews.plain.GetLabelsPlainView;
import pt.isel.ls.view.commandviews.plain.GetRoomByIdPlainView;
import pt.isel.ls.view.commandviews.plain.GetRoomsPlainView;
import pt.isel.ls.view.commandviews.plain.GetRoomsWithLabelPlainView;
import pt.isel.ls.view.commandviews.plain.GetTimePlainView;
import pt.isel.ls.view.commandviews.plain.GetUserByIdPlainView;
import pt.isel.ls.view.commandviews.plain.GetUsersPlainView;
import pt.isel.ls.view.commandviews.plain.HttpResponsePlainView;
import pt.isel.ls.view.commandviews.plain.ListenPlainView;
import pt.isel.ls.view.commandviews.plain.NoRoutePlainView;
import pt.isel.ls.view.commandviews.plain.OptionPlainView;
import pt.isel.ls.view.commandviews.plain.PostBookingInRoomPlainView;
import pt.isel.ls.view.commandviews.plain.PostLabelPlainView;
import pt.isel.ls.view.commandviews.plain.PostRoomPlainView;
import pt.isel.ls.view.commandviews.plain.PostUserPlainView;
import pt.isel.ls.view.commandviews.plain.PutBookingInRoomPlainView;

import java.io.IOException;
import java.io.OutputStream;

public abstract class View {

    protected static final String HtmlViewFormat = "text/html";
    protected static final String PlainViewFormat = "text/plain";

    /**
     * Used to determine whether the View has a route or not, in case the View doesn't have
     * an HTML or textual representation
     */
    protected boolean foundRoute = true;

    /**
     * Determines the representation for the result parameter based on the viewFormat String
     */
    public static View findView(CommandResult commandResult, String viewFormat) {
        if (viewFormat != null && viewFormat.equals(HtmlViewFormat)) {
            return findHtmlView(commandResult);
        }

        //Since the text/plain view is the default viewFormat we only need to specifically check the other view formats
        return findTextPlainView(commandResult);
    }

    /**
     * Creates a new instance of a plain text View corresponding to the result type of the commandResult parameter
     */
    private static View findTextPlainView(CommandResult commandResult) {
        if (!commandResult.hasResults()) {
            return new EmptyPlainView();
        }

        switch (commandResult.getResultType()) {
            case DeleteBookingInRoom:
                return new DeleteBookingInRoomPlainView(commandResult);
            case GetBookingByRoomAndBookingId:
                return new GetBookingByRoomAndBookingIdPlainView(commandResult);
            case GetBookingsByRoomId:
                return new GetBookingsByRoomIdPlainView(commandResult);
            case GetBookingsByUserId:
                return new GetBookingsByUserIdPlainView(commandResult);
            case GetHome:
                return new GetHomePlainView();
            case GetLabelById:
                return new GetLabelByIdPlainView(commandResult);
            case GetLabels:
                return new GetLabelsPlainView(commandResult);
            case GetRoomById:
                return new GetRoomByIdPlainView(commandResult);
            case GetRooms:
                return new GetRoomsPlainView(commandResult);
            case GetRoomsWithLabel:
                return new GetRoomsWithLabelPlainView(commandResult);
            case GetTime:
                return new GetTimePlainView(commandResult);
            case GetUserById:
                return new GetUserByIdPlainView(commandResult);
            case GetUsers:
                return new GetUsersPlainView(commandResult);
            case Listen:
                return new ListenPlainView(commandResult);
            case Option:
                return new OptionPlainView(commandResult);
            case PostBookingInRoom:
                return new PostBookingInRoomPlainView(commandResult);
            case PostLabel:
                return new PostLabelPlainView(commandResult);
            case PostRoom:
                return new PostRoomPlainView(commandResult);
            case PostUser:
                return new PostUserPlainView(commandResult);
            case PutBookingInRoom:
                return new PutBookingInRoomPlainView(commandResult);
            case HttpResponse:
                return new HttpResponsePlainView(commandResult);
            default:
                return new NoRoutePlainView();
        }
    }

    /**
     * Creates a new instance of an HTML View corresponding to the result type of the commandResult parameter
     */
    private static View findHtmlView(CommandResult commandResult) {
        if (!commandResult.hasResults()) {
            return new EmptyHtmlView();
        }

        switch (commandResult.getResultType()) {
            case DeleteBookingInRoom:
                return new DeleteBookingInRoomHtmlView(commandResult);
            case GetBookingByRoomAndBookingId:
                return new GetBookingByRoomAndBookingIdHtmlView(commandResult);
            case GetBookingsByRoomId:
                return new GetBookingsByRoomIdHtmlView(commandResult);
            case GetBookingsByUserId:
                return new GetBookingsByUserIdHtmlView(commandResult);
            case GetBookingsCreate:
                return new GetBookingsCreateHtmlView(commandResult);
            case GetHome:
                return new GetHomeHtmlView();
            case GetLabelById:
                return new GetLabelByIdHtmlView(commandResult);
            case GetLabels:
                return new GetLabelsHtmlView(commandResult);
            case GetLabelsCreate:
                return new GetLabelsCreateHtmlView(commandResult);
            case GetRoomById:
                return new GetRoomByIdHtmlView(commandResult);
            case GetRooms:
                return new GetRoomsHtmlView(commandResult);
            case GetRoomsCreate:
                return new GetRoomsCreateHtmlView(commandResult);
            case GetRoomsWithLabel:
                return new GetRoomsWithLabelHtmlView(commandResult);
            case GetRoomsSearch:
                return new GetRoomsSearchHtmlView(commandResult);
            case GetTime:
                return new GetTimeHtmlView(commandResult);
            case GetUserById:
                return new GetUserByIdHtmlView(commandResult);
            case GetUsersCreate:
                return new GetUsersCreateHtmlView(commandResult);
            case GetUsers:
                return new GetUsersHtmlView(commandResult);
            case Listen:
                return new ListenHtmlView(commandResult);
            case Option:
                return new OptionHtmlView(commandResult);
            case PostBookingInRoom:
                return new PostBookingInRoomHtmlView(commandResult);
            case PostLabel:
                return new PostLabelHtmlView(commandResult);
            case PostRoom:
                return new PostRoomHtmlView(commandResult);
            case PostUser:
                return new PostUserHtmlView(commandResult);
            case PutBookingInRoom:
                return new PutBookingInRoomHtmlView(commandResult);
            case HttpResponse:
                return new HttpResponseHtmlView(commandResult);
            default:
                return new NoRouteHtmlView();
        }
    }

    /**
     * Renders the String corresponding to the View to the OutputStream
     */
    public void render(OutputStream out) throws IOException {
        out.write((getDisplay()).getBytes());
    }

    /**
     * Returns the String corresponding to the View representation
     */
    public String getDisplay() {
        return display() + "\n\n";
    }

    protected abstract String display();

    public abstract String getViewFormat();

    public boolean foundRoute() {
        return foundRoute;
    }
}
