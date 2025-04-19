package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.TargetSelectorArgument;

public class KillCommand implements Command {
    private String target;

    public String build() {
        return "kill" + (target != null ? " " + target : "");
    }

    public static final ParseSequence<KillCommand> SEQUENCE = new ParseSequence<>(KillCommand::new)
            .node("target", new TargetSelectorArgument(), (arg, cmd) -> cmd.target = arg.build())
            .rule("kill [<target>]");
}
