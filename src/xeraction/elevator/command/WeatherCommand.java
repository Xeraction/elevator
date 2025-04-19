package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.StringArgument;

public class WeatherCommand implements Command {
    private Mode mode;
    private String duration;

    public String build() {
        return "weather " + mode.name + (duration != null ? " " + duration : "");
    }

    public static final ParseSequence<WeatherCommand> SEQUENCE = new ParseSequence<>(WeatherCommand::new)
            .node("duration", new StringArgument(), (arg, cmd) -> cmd.duration = arg.value())
            .lit((cmd, lit) -> cmd.mode = Mode.parse(lit))
            .rule("weather (clear|rain|thunder) [<duration>]");

    private enum Mode {
        Clear("clear"), Rain("rain"), Thunder("thunder");

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
