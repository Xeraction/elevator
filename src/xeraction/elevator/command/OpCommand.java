package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.TargetSelectorArgument;

public class OpCommand implements Command {
    private String target;

    public String build() {
        return "op " + target;
    }

    public static final ParseSequence<OpCommand> SEQUENCE = new ParseSequence<>(OpCommand::new)
            .node("target", new TargetSelectorArgument(), (arg, cmd) -> cmd.target = arg.build())
            .rule("op <target>");
}
