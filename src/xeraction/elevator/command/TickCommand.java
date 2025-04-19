package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.StringArgument;

public class TickCommand implements Command {
    private Mode mode;
    private String time;

    public String build() {
        String s = "tick " + mode.name;
        if (mode == Mode.Rate || mode == Mode.Step || mode == Mode.Sprint)
            return s + " " + time;
        return s;
    }

    public static final ParseSequence<TickCommand> SEQUENCE = new ParseSequence<>(TickCommand::new)
            .node("time", new StringArgument(), (arg, cmd) -> cmd.time = arg.value())
            .lit("tick step stop", cmd -> cmd.mode = Mode.StepStop)
            .lit("tick sprint stop", cmd -> cmd.mode = Mode.SprintStop)
            .lit((cmd, lit) -> cmd.mode = Mode.parse(lit))
            .rule("tick (query|rate <time>|freeze|unfreeze|step [(stop|<time>)]|sprint [(stop|<time>)])");

    private enum Mode {
        Query("query"),
        Rate("rate"),
        Freeze("freeze"),
        Unfreeze("unfreeze"),
        Step("step"),
        StepStop("step stop"),
        Sprint("sprint"),
        SprintStop("sprint stop");

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
