package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.StringArgument;

public class DebugCommand implements Command {
    private boolean start;
    private String name;

    public String build() {
        return "debug " + (name == null ? (start ? "start" : "stop") : "function " + name);
    }

    public static final ParseSequence<DebugCommand> SEQUENCE = new ParseSequence<>(DebugCommand::new)
            .node("name", new StringArgument(), (arg, cmd) -> cmd.name = arg.value())
            .lit("* start", cmd -> cmd.start = true).lit("* stop", cmd -> cmd.start = false)
            .rule("debug (start|stop|function <name>)");
}
