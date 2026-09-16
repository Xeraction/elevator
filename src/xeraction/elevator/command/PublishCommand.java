package xeraction.elevator.command;

import xeraction.elevator.ParseException;
import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.StringArgument;

public class PublishCommand implements Command {
    private String allow;
    private String port;

    public String build() {
        StringBuilder b = new StringBuilder("publish");
        if (allow != null)
            b.append(" ").append(allow);
        if (port != null)
            b.append(" ").append(port);
        return b.toString();
    }

    public static final ParseSequence<PublishCommand> SEQUENCE = new ParseSequence<>(PublishCommand::new)
            .node("allow", new StringArgument(), (arg, cmd) -> cmd.allow = arg.value())
            .node("port", new StringArgument(), (arg, cmd) -> {
                if (cmd.port != null) //first port argument already fired, cannot have a second one
                    throw new ParseException("A server cannot have two ports.");
                if (!Character.isDigit(arg.value().charAt(0)))
                    return; //port is actually the old gamemode argument, in this case fall back to second port argument
                cmd.port = arg.value();
            })
            .rule("publish [<allow>] [<port>] [<port>]");
}
