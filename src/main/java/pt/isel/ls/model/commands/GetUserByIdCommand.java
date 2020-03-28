package pt.isel.ls.model.commands;

import pt.isel.ls.model.commands.common.CommandHandler;
import pt.isel.ls.model.commands.common.CommandRequest;
import pt.isel.ls.model.commands.common.CommandResult;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GetUserByIdCommand implements CommandHandler {
    @Override
    public CommandResult execute(CommandRequest commandRequest) {
        CommandResult result = new CommandResult();
        try (Connection con = commandRequest.getConnectionHandler().getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * "
                    + "FROM USERS WHERE uid = ?");

            int userId = Integer.parseInt(commandRequest.getPath().getVariable("uid"));
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                result.addResult("No results found");
            } else {
                result.addResult("user id (uid): " + rs.getInt("uid"));
                result.addResult("name: " + rs.getString("name"));
                result.addResult("email: " + rs.getString("email"));
            }
            result.setTitle("Information about user " + userId);
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