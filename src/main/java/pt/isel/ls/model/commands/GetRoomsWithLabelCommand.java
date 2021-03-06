package pt.isel.ls.model.commands;

import pt.isel.ls.model.commands.common.CommandHandler;
import pt.isel.ls.model.commands.common.CommandRequest;
import pt.isel.ls.model.commands.common.CommandResult;
import pt.isel.ls.model.commands.common.exceptions.CommandException;
import pt.isel.ls.model.commands.common.exceptions.InvalidIdException;
import pt.isel.ls.model.commands.helpers.DatabaseDataHelper;
import pt.isel.ls.model.commands.results.GetRoomsWithLabelResult;
import pt.isel.ls.model.commands.sql.TransactionManager;
import pt.isel.ls.model.entities.Label;
import pt.isel.ls.model.entities.Room;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GetRoomsWithLabelCommand implements CommandHandler {
    @Override
    public CommandResult execute(CommandRequest commandRequest) throws CommandException, SQLException {
        GetRoomsWithLabelResult result = new GetRoomsWithLabelResult();
        TransactionManager trans = commandRequest.getTransactionHandler();
        trans.executeTransaction(con -> {
            PreparedStatement ps = con.prepareStatement("SELECT ROOM.rid, name, location, capacity "
                    + "FROM ROOMLABEL INNER JOIN ROOM ON ROOMLABEL.rid = ROOM.rid WHERE lid = ? ORDER BY ROOM.rid");
            int labelId;
            try {
                labelId = commandRequest.getPath().getInt("lid");
            }  catch (NumberFormatException e) {
                throw new InvalidIdException("Invalid Label ID");
            }
            ps.setInt(1, labelId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Integer capacity = rs.getInt("capacity");
                if (rs.wasNull()) {
                    capacity = null;
                }
                result.addRoom(new Room(rs.getInt("rid"),
                        rs.getString("name"),
                        rs.getString("location"),
                        capacity));
                result.setLabel(new Label(labelId, DatabaseDataHelper.getLabelName(labelId, con)));
            }

            rs.close();
            ps.close();
        });
        return result;
    }

    @Override
    public String getDescription() {
        return "returns a list with all the rooms having label lid";
    }
}
