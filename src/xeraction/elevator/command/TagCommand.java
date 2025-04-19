package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.StringArgument;
import xeraction.elevator.argument.TargetSelectorArgument;

public class TagCommand implements Command {
    private String target;
    private Mode mode;
    private String name;

    public String build() {
        return "tag " + target + " " + mode.name + (mode != Mode.List ? " " + name : "");
    }

    public static final ParseSequence<TagCommand> SEQUENCE = new ParseSequence<>(TagCommand::new)
            .node("target", new TargetSelectorArgument(), (arg, cmd) -> cmd.target = arg.build())
            .node("name", new StringArgument(), (arg, cmd) -> cmd.name = arg.value())
            .lit((cmd, lit) -> cmd.mode = Mode.parse(lit))
            .rule("tag <target> (add <name>|remove <name>|list)");

    private enum Mode {
        Add("add"), Remove("remove"), List("list");

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
