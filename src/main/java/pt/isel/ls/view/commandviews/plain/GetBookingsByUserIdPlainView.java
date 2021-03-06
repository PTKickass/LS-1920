package pt.isel.ls.view.commandviews.plain;

import pt.isel.ls.model.commands.common.CommandResult;
import pt.isel.ls.model.commands.results.GetBookingsByUserIdResult;
import pt.isel.ls.model.entities.Booking;

import java.util.Iterator;

import static pt.isel.ls.view.commandviews.helpers.BookingHelpers.appendBeginInst;
import static pt.isel.ls.view.commandviews.helpers.BookingHelpers.appendBid;
import static pt.isel.ls.view.commandviews.helpers.BookingHelpers.appendEndInst;

public class GetBookingsByUserIdPlainView extends PlainView {

    private GetBookingsByUserIdResult result;

    public GetBookingsByUserIdPlainView(CommandResult commandResult) {
        this.result = (GetBookingsByUserIdResult) commandResult;
    }

    @Override
    public String display() {
        StringBuilder builder = new StringBuilder();
        Iterator<Booking> iter = result.getBookings().iterator();
        builder.append("Bookings from User with ID [")
                .append(result.getUid())
                .append("]:")
                .append("\n\n");
        while (iter.hasNext()) {
            Booking booking = iter.next();
            appendBid(booking, builder);
            builder.append('\n');
            appendBeginInst(booking, builder);
            builder.append('\n');
            appendEndInst(booking, builder);
            if (iter.hasNext()) {
                builder.append("\n============================================\n");
            }
        }
        return builder.toString();
    }
}
