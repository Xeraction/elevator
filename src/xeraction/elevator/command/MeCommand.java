package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.StringArgument;

public class MeCommand implements Command {
    private String action;

    public String build() {
        return "me " + action;
    }

    public static final ParseSequence<MeCommand> SEQUENCE = new ParseSequence<>(MeCommand::new)
            .node("action", new StringArgument(true), (arg, cmd) -> cmd.action = arg.value())
            .rule("me <action>");
}
