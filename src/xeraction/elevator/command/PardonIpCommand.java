package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.StringArgument;

public class PardonIpCommand implements Command {
    private String ip;

    public String build() {
        return "pardon-ip " + ip;
    }

    public static final ParseSequence<PardonIpCommand> SEQUENCE = new ParseSequence<>(PardonIpCommand::new)
            .node("ip", new StringArgument(), (arg, cmd) -> cmd.ip = arg.value())
            .rule("pardon-ip <ip>");
}
