package pt.isel.ls.model.commands;

import pt.isel.ls.model.commands.common.CommandHandler;
import pt.isel.ls.model.commands.common.CommandRequest;
import pt.isel.ls.model.commands.common.CommandResult;
import pt.isel.ls.model.commands.common.PsqlConnectionHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GetBookingsByRoomAndBookingId implements CommandHandler {
    @Override
    public CommandResult execute(CommandRequest commandRequest) {
        CommandResult result = new CommandResult();
        try (Connection con = PsqlConnectionHandler.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * "
                    + "FROM BOOKING WHERE rid = ? AND bid = ?");

            int roomId = Integer.parseInt(commandRequest.getPath().getVariable(0));
            int bookingId = Integer.parseInt(commandRequest.getPath().getVariable(1));
            ps.setInt(1, roomId);
            ps.setInt(2, bookingId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.addResult("booking id (bid): " + rs.getInt("bid"));
                result.addResult("reservation by user id (uid): " + rs.getInt("uid"));
                result.addResult("room id (rid): " + rs.getInt("rid"));
                result.addResult("begin instant: " + rs.getString("begin_inst"));
                result.addResult("end instant: " + rs.getString("end_inst"));
            }
            result.setTitle("Information about booking " + bookingId + " in room " + roomId);
            result.setSuccess(true);

            rs.close();
            ps.close();
        } catch (SQLException e) {
            result.setSuccess(false);
            result.clearResults();
            result.setTitle(e.getMessage());
        }
        return result;
    }
}