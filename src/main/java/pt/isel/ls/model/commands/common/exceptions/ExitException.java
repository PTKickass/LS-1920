package pt.isel.ls.model.commands.common.exceptions;

public class ExitException extends CommandException {

    public ExitException(String message) {
        super(message);
    }

    @Override
    public ExceptionType getExceptionType() {
        return ExceptionType.ExitException;
    }
}
