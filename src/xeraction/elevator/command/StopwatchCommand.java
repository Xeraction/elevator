package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.StringArgument;

public class StopwatchCommand implements Command {

    private Mode mode;
    private String id;
    private String scale;

    public String build() {
        return "stopwatch " + mode.name + " " + id + (scale != null ? " " + scale : "");
    }

    public static final ParseSequence<StopwatchCommand> SEQUENCE = new ParseSequence<>(StopwatchCommand::new)
            .node("id", new StringArgument(), (arg, cmd) -> cmd.id = arg.value())
            .node("scale", new StringArgument(), (arg, cmd) -> cmd.scale = arg.value())
            .lit((cmd, lit) -> cmd.mode = Mode.parse(lit))
            .rule("stopwatch (create <id>|query <id> [<scale>]|restart <id>|remove <id>)");

    private enum Mode {
        Create("create"),
        Query("query"),
        Restart("restart"),
        Remove("remove");

        public final String name;

        Mode(String name) {
            this.name = name;
        }

        public static Mode parse(String mode) {
            for (Mode m : values())
                if (m.name.equals(mode))
                    return m;
            return null;
        }
    }
}
