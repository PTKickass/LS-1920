package pt.isel.ls.model.commands;

import pt.isel.ls.model.commands.common.CommandHandler;
import pt.isel.ls.model.commands.common.CommandRequest;
import pt.isel.ls.model.commands.common.CommandResult;
import pt.isel.ls.model.commands.common.Parameters;
import pt.isel.ls.model.commands.common.exceptions.CommandException;
import pt.isel.ls.model.commands.common.exceptions.MissingArgumentsException;
import pt.isel.ls.model.commands.common.exceptions.ParseArgumentException;
import pt.isel.ls.model.commands.results.PostRoomResult;
import pt.isel.ls.model.commands.sql.TransactionManager;
import pt.isel.ls.model.entities.Room;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.LinkedList;

import static pt.isel.ls.model.commands.common.Validator.validateString;
import static pt.isel.ls.model.commands.helpers.DatabaseDataHelper.getLids;

public class PostRoomCommand implements CommandHandler {

    @Override
    public CommandResult execute(CommandRequest commandRequest) throws CommandException, SQLException {
        PostRoomResult result = new PostRoomResult();
        TransactionManager trans = commandRequest.getTransactionHandler();
        trans.executeTransaction(con -> {
            PreparedStatement ps = con.prepareStatement("INSERT INTO ROOM "
                            + "(name, description, location, capacity) Values(?,?,?,?)",
                            Statement.RETURN_GENERATED_KEYS
            );
            Parameters params = commandRequest.getParams();
            if (params == null) {
                throw new MissingArgumentsException("No parameters specified");
            }
            String name = params.getString("name");
            String description = params.getString("description");
            String location = params.getString("location");
            Integer capacity;
            try {
                capacity = params.getInt("capacity");
            } catch (NumberFormatException e) {
                throw new ParseArgumentException("Invalid capacity");
            }

            if (capacity != null && capacity <= 0) {
                throw new ParseArgumentException("Capacity must be higher than 0");
            }

            if (name != null && validateString(name, "name", 30)
                    && location != null && validateString(location, "location", 50)
                    && validateString(description, "description", 50)) {

                ps.setString(1, name);
                ps.setString(2, description);
                ps.setString(3, location);

                if (capacity == null) {
                    ps.setNull(4, Types.INTEGER);
                } else {
                    ps.setInt(4, capacity);
                }

                ps.executeUpdate();

                //Get rid
                ResultSet rs = ps.getGeneratedKeys();
                rs.next();
                int rid = rs.getInt("rid");
                result.setRoom(new Room(rid, name));

                Iterable<String> labels = commandRequest.getParams().getValues("label");
                if (labels != null) {
                    LinkedList<Integer> lids = getLids(labels, con);
                    fillRoomLabelTable(con, rid, lids);
                }
            } else {
                throw new MissingArgumentsException();
            }
            ps.close();

        });
        return result;
    }

    private void fillRoomLabelTable(Connection con, int rid, LinkedList<Integer> lids) throws SQLException {
        StringBuilder builder = new StringBuilder("insert into ROOMLABEL values(?,?");
        for (int i = 0; i < lids.size() - 1; i++) {
            builder.append("), (?,?");
        }
        builder.append(")");

        PreparedStatement ps = con.prepareStatement(builder.toString());
        int i = 1;
        for (Integer lid: lids) {
            ps.setInt(i++,lid);
            ps.setInt(i++,rid);
        }

        ps.executeUpdate();
    }

    @Override
    public String getDescription() {
        return "creates a new room, given the following parameters\n"
                + "- name - the rooms's name.\n"
                + "- description - the rooms's description.\n"
                + "- location - the room's location.\n"
                + "- capacity - the room's person capacity.\n"
                + "- label - the set of labels for the room.";
    }
}
