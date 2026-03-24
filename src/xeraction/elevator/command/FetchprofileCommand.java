package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.StringArgument;
import xeraction.elevator.argument.TargetSelectorArgument;

public class FetchprofileCommand implements Command {
    private Mode mode;
    private String id;

    public String build() {
        return "fetchprofile " + mode.name + " " + id;
    }

    public static final ParseSequence<FetchprofileCommand> SEQUENCE = new ParseSequence<>(FetchprofileCommand::new)
            .node("id", new StringArgument(), (arg, cmd) -> cmd.id = arg.value())
            .node("trg", new TargetSelectorArgument(), (arg, cmd) -> cmd.id = arg.build())
            .lit((cmd, lit) -> cmd.mode = Mode.parse(lit))
            .rule("fetchprofile (name <id>|id <id>|entity <trg>)");

    private enum Mode {
        Name("name"), Id("id"), Entity("entity");

        private final String name;

        Mode(String name) {
            this.name = name;
        }

        public static Mode parse(String s) {
            for (Mode m : values())
                if (m.name.equals(s))
                    return m;
            return null;
        }
    }
}
