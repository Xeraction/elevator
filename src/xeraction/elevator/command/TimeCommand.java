package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.StringArgument;

public class TimeCommand implements Command {
    private Mode mode;
    private String time;

    public String build() {
        return "time " + mode.name + " " + time;
    }

    public static final ParseSequence<TimeCommand> SEQUENCE = new ParseSequence<>(TimeCommand::new)
            .node("time", new StringArgument(), (arg, cmd) -> cmd.time = arg.value())
            .lit((cmd, lit) -> cmd.mode = Mode.parse(lit))
            .rule("time (add|query|set) <time>");

    private enum Mode {
        Add("add"), Query("query"), Set("set");

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
