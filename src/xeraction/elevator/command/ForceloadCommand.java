package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.Vec2Argument;

public class ForceloadCommand implements Command {
    private Mode mode;
    private String from;
    private String to;

    public String build() {
        return "forceload " + mode.name + (from != null ? " " + from : "") + (to != null ? " " + to : "");
    }

    public static final ParseSequence<ForceloadCommand> SEQUENCE = new ParseSequence<>(ForceloadCommand::new)
            .node("from", new Vec2Argument(), (arg, cmd) -> cmd.from = arg.vec())
            .node("to", new Vec2Argument(), (arg, cmd) -> cmd.to = arg.vec())
            .lit("* all", cmd -> cmd.mode = Mode.RemoveAll)
            .lit((cmd, lit) -> cmd.mode = Mode.parse(lit))
            .rule("forceload (add <from> [<to>]|remove (all|<from> [<to>])|query [<from>])");

    private enum Mode {
        Add("add"), Remove("remove"), RemoveAll("remove all"), Query("query");

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
