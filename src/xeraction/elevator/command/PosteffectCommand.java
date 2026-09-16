package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.StringArgument;
import xeraction.elevator.argument.TargetSelectorArgument;

public class PosteffectCommand implements Command {
    private String target;
    private String effect;
    private Mode mode;

    public String build() {
        return "posteffect " + mode.name + " " + target + (effect != null ? " " + effect : "");
    }

    public static final ParseSequence<PosteffectCommand> SEQUENCE = new ParseSequence<>(PosteffectCommand::new)
            .node("target", new TargetSelectorArgument(), (arg, cmd) -> cmd.target = arg.build())
            .node("effect", new StringArgument(), (arg, cmd) -> cmd.effect = arg.value())
            .lit((cmd, lit) -> cmd.mode = Mode.parse(lit))
            .rule("posteffect (add <target> <effect>|clear <target>|list <target>|remove <target> <effect>)");

    private enum Mode {
        Add("add"), Clear("clear"), List("list"), Remove("remove");

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
