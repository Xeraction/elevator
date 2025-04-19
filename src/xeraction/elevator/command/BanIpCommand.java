package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.StringArgument;

public class BanIpCommand implements Command {
    private String target;
    private String reason;

    public String build() {
        return "ban-ip " + target + (reason == null ? "" : " " + reason);
    }

    public static ParseSequence<BanIpCommand> SEQUENCE = new ParseSequence<>(BanIpCommand::new)
            .node("target", new StringArgument(), (arg, cmd) -> cmd.target = arg.value())
            .node("reason", new StringArgument(true), (arg, cmd) -> cmd.reason = arg.value())
            .rule("ban-ip <target> [<reason>]");
}
