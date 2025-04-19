package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.StringArgument;

public class ScheduleCommand implements Command {
    private String func;
    private String time;
    private Mode mode;

    public String build() {
        if (time == null)
            return "schedule clear " + func;
        return "schedule function " + func + " " + time + (mode != null ? " " + mode.name : "");
    }

    public static final ParseSequence<ScheduleCommand> SEQUENCE = new ParseSequence<>(ScheduleCommand::new)
            .node("func", new StringArgument(), (arg, cmd) -> cmd.func = arg.value())
            .node("time", new StringArgument(), (arg, cmd) -> cmd.time = arg.value())
            .lit("* append", cmd -> cmd.mode = Mode.Append).lit("* replace", cmd -> cmd.mode = Mode.Replace)
            .rule("schedule (clear <func>|function <func> <time> [(append|replace)])");

    private enum Mode {
        Append("append"), Replace("replace");

        public final String name;

        Mode(String name) {
            this.name = name;
        }
    }
}
