package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.StringArgument;
import xeraction.elevator.argument.TargetSelectorArgument;

public class BanCommand implements Command {
    private String target;
    private String reason;

    public String build() {
        return "ban " + target + (reason == null ? "" : " " + reason);
    }

    public static final ParseSequence<BanCommand> SEQUENCE = new ParseSequence<>(BanCommand::new)
            .node("target", new TargetSelectorArgument(), (arg, cmd) -> cmd.target = arg.build())
            .node("reason", new StringArgument(true), (arg, cmd) -> cmd.reason = arg.value())
            .rule("ban <target> [<reason>]");
}
