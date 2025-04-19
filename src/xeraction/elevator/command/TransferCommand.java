package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.StringArgument;
import xeraction.elevator.argument.TargetSelectorArgument;

public class TransferCommand implements Command {
    private String host;
    private String port;
    private String players;

    public String build() {
        return "transfer " + host + (port != null ? " " + port : "") + (players != null ? " " + players : "");
    }

    public static final ParseSequence<TransferCommand> SEQUENCE = new ParseSequence<>(TransferCommand::new)
            .node("host", new StringArgument(), (arg, cmd) -> cmd.host = arg.value())
            .node("port", new StringArgument(), (arg, cmd) -> cmd.port = arg.value())
            .node("players", new TargetSelectorArgument(), (arg, cmd) -> cmd.players = arg.build())
            .rule("transfer <host> [<port>] [<players>]");
}
