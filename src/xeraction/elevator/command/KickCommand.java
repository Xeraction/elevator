package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.StringArgument;
import xeraction.elevator.argument.TargetSelectorArgument;

public class KickCommand implements Command {
    private String target;
    private String reason;

    public String build() {
        return "kick " + target + (reason != null ? " " + reason : "");
    }

    public static final ParseSequence<KickCommand> SEQUENCE = new ParseSequence<>(KickCommand::new)
            .node("target", new TargetSelectorArgument(), (arg, cmd) -> cmd.target = arg.build())
            .node("reason", new StringArgument(true), (arg, cmd) -> cmd.reason = arg.value())
            .rule("kick <target> [<reason>]");
}
