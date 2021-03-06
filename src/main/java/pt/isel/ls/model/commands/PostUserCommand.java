package pt.isel.ls.model.commands;

import pt.isel.ls.model.commands.common.CommandHandler;
import pt.isel.ls.model.commands.common.CommandRequest;
import pt.isel.ls.model.commands.common.CommandResult;
import pt.isel.ls.model.commands.common.Parameters;
import pt.isel.ls.model.commands.common.exceptions.CommandException;
import pt.isel.ls.model.commands.common.exceptions.MissingArgumentsException;
import pt.isel.ls.model.commands.results.PostUserResult;
import pt.isel.ls.model.commands.sql.TransactionManager;
import pt.isel.ls.model.entities.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static pt.isel.ls.model.commands.common.Validator.validateString;

public class PostUserCommand implements CommandHandler {
    @Override
    public CommandResult execute(CommandRequest commandRequest) throws CommandException, SQLException {
        PostUserResult result = new PostUserResult();
        TransactionManager trans = commandRequest.getTransactionHandler();
        trans.executeTransaction(con -> {
            PreparedStatement ps = con.prepareStatement("INSERT INTO USERS "
                            + "(name, email) Values(?,?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            Parameters params = commandRequest.getParams();
            if (params == null) {
                throw new MissingArgumentsException("No parameters specified");
            }
            String name = params.getString("name");
            String email = params.getString("email");
            if (name != null && validateString(name, "name", 50)
                    && email != null && validateString(email, "email", 50)) {

                ps.setString(1, name);
                ps.setString(2, email);
                ps.executeUpdate();

                //Get uid
                ResultSet rs = ps.getGeneratedKeys();
                rs.next();
                result.setUser(new User(rs.getInt("uid"), name));
            } else {
                throw new MissingArgumentsException();
            }
            ps.close();

        });
        return result;
    }

    @Override
    public String getDescription() {
        return "creates a new user, given the following parameters\n"
                + "- name - the user's name.\n"
                + "- email - the user's email.";
    }
}