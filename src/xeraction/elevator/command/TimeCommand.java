package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.StringArgument;

public class TimeCommand implements Command {
    private Mode mode;
    private String time;
    private String clock;

    public String build() {
        return "time " + (clock != null ? "of " + clock + " " : "") + mode.name + (time != null ? " " + time : "");
    }

    public static final ParseSequence<TimeCommand> SEQUENCE = new ParseSequence<>(TimeCommand::new)
            .node("time", new StringArgument(true), (arg, cmd) -> cmd.time = arg.value()) //sometimes there's an extra "repetition" argument in certain commands, so we're lazy and make the argument greedy
            .node("clock", new StringArgument(), (arg, cmd) -> cmd.clock = arg.value())
            .lit((cmd, lit) -> cmd.mode = Mode.parse(lit))
            .rule("time {of <clock>} ((add|query|set|rate) <time>|(pause|resume))");

    private enum Mode {
        Add("add"), Query("query"), Set("set"), Rate("rate"), Pause("pause"), Resume("resume");

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
