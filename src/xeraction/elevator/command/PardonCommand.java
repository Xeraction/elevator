package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.TargetSelectorArgument;

public class PardonCommand implements Command {
    private String target;

    public String build() {
        return "pardon " + target;
    }

    public static final ParseSequence<PardonCommand> SEQUENCE = new ParseSequence<>(PardonCommand::new)
            .node("target", new TargetSelectorArgument(), (arg, cmd) -> cmd.target = arg.build())
            .rule("pardon <target>");
}
