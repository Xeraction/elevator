package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.StringArgument;
import xeraction.elevator.util.LegacyData;

public class PublishCommand implements Command {
    private String allow;
    private String gamemode;
    private String port;

    public String build() {
        StringBuilder b = new StringBuilder("publish");
        if (allow != null)
            b.append(" ").append(allow);
        if (gamemode != null)
            b.append(" ").append(gamemode);
        if (port != null)
            b.append(" ").append(port);
        return b.toString();
    }

    public static final ParseSequence<PublishCommand> SEQUENCE = new ParseSequence<>(PublishCommand::new)
            .node("allow", new StringArgument(), (arg, cmd) -> cmd.allow = arg.value())
            .node("gamemode", new StringArgument(), (arg, cmd) -> cmd.gamemode = LegacyData.renameGamemode(arg.value()))
            .node("port", new StringArgument(), (arg, cmd) -> cmd.port = arg.value())
            .rule("publish [<allow>] [<gamemode>] [<port>]");
}
