package pt.isel.ls.model.commands.results;

import pt.isel.ls.model.commands.common.CommandResult;
import pt.isel.ls.model.entities.Booking;
import pt.isel.ls.model.entities.Room;

import java.util.LinkedList;

public class GetBookingsByRoomIdResult implements CommandResult {

    private boolean hasResult = false;
    private LinkedList<Booking> bookings;
    private Room room;

    public void addBooking(Booking booking) {
        if (bookings == null) {
            bookings = new LinkedList<>();
            hasResult = true;
        }
        bookings.add(booking);
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Iterable<Booking> getBookings() {
        return bookings;
    }

    @Override
    public boolean hasResults() {
        return hasResult;
    }

    @Override
    public CommandResult.ResultType getResultType() {
        return ResultType.GetBookingsByRoomId;
    }

    public Room getRoom() {
        return room;
    }
}
