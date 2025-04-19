package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.StringArgument;
import xeraction.elevator.argument.Vec2Argument;

public class WorldborderCommand implements Command {
    private Mode mode;
    private String opt1;
    private String opt2;

    public String build() {
        return "worldborder " + mode.name + (opt1 != null ? " " + opt1 : "") + (opt2 != null ? " " + opt2 : "");
    }

    public static final ParseSequence<WorldborderCommand> SEQUENCE = new ParseSequence<>(WorldborderCommand::new)
            .node("opt1", new StringArgument(), (arg, cmd) -> cmd.opt1 = arg.value())
            .node("opt2", new StringArgument(), (arg, cmd) -> cmd.opt2 = arg.value())
            .node("pos", new Vec2Argument(), (arg, cmd) -> cmd.opt1 = arg.vec())
            .lit((cmd, lit) -> cmd.mode = Mode.parse(lit))
            .rule("worldborder ((add|set) <opt1> [<opt2>]|center <pos>|damage (amount|buffer) <opt1>|get|warning (distance|time) <opt1>)");

    private enum Mode {
        Add("add"),
        Center("center"),
        DamageAmount("damage amount", "amount"),
        DamageBuffer("damage buffer", "buffer"),
        Get("get"),
        Set("set"),
        WarningDistance("warning distance", "distance"),
        WarningTime("warning time", "time");

        public final String name;
        public final String literal;

        Mode(String name) {
            this(name, name);
        }

        Mode(String name, String literal) {
            this.name = name;
            this.literal = literal;
        }

        public static Mode parse(String mode) {
            for (Mode m : values())
                if (m.literal.equals(mode))
                    return m;
            return null;
        }
    }
}
