package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.TargetSelectorArgument;

public class WhitelistCommand implements Command {
    private Mode mode;
    private String target;

    public String build() {
        return "whitelist " + mode.name + (target != null ? " " + target : "");
    }

    public static final ParseSequence<WhitelistCommand> SEQUENCE = new ParseSequence<>(WhitelistCommand::new)
            .node("target", new TargetSelectorArgument(), (arg, cmd) -> cmd.target = arg.build())
            .lit((cmd, lit) -> cmd.mode = Mode.parse(lit))
            .rule("whitelist ((add|remove) <target>|list|off|on|reload)");

    private enum Mode {
        Add("add"), List("list"), Off("off"), On("on"), Reload("reload"), Remove("remove");

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
