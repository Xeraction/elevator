package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.TargetSelectorArgument;

public class DeopCommand implements Command {
    private String target;

    public String build() {
        return "deop " + target;
    }

    public static final ParseSequence<DeopCommand> SEQUENCE = new ParseSequence<>(DeopCommand::new)
            .node("target", new TargetSelectorArgument(), (arg, cmd) -> cmd.target = arg.build())
            .rule("deop <target>");
}
