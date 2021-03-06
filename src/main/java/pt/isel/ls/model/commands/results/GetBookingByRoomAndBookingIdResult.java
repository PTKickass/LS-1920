package pt.isel.ls.model.commands.results;

import pt.isel.ls.model.commands.common.CommandResult;
import pt.isel.ls.model.entities.Booking;

public class GetBookingByRoomAndBookingIdResult implements CommandResult {

    private boolean hasResult = false;
    private Booking booking;

    public void setBooking(Booking booking) {
        this.booking = booking;
        hasResult = true;
    }

    public Booking getBooking() {
        return booking;
    }

    @Override
    public boolean hasResults() {
        return hasResult;
    }

    @Override
    public CommandResult.ResultType getResultType() {
        return ResultType.GetBookingByRoomAndBookingId;
    }
}
