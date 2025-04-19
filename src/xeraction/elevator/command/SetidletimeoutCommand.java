package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.StringArgument;

public class SetidletimeoutCommand implements Command {
    private String minutes;

    public String build() {
        return "setidletimeout " + minutes;
    }

    public static final ParseSequence<SetidletimeoutCommand> SEQUENCE = new ParseSequence<>(SetidletimeoutCommand::new)
            .node("min", new StringArgument(), (arg, cmd) -> cmd.minutes = arg.value())
            .rule("setidletimeout <min>");
}
