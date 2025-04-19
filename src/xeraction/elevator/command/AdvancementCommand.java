package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.*;

public class AdvancementCommand implements Command {
    private boolean grant;
    private String target;
    private Mode mode;
    private String advancement;
    private String criterion;
    private boolean legacyTest = false;

    public String build() {
        if (legacyTest)
            return "execute if entity " + (!target.startsWith("@") ? "@a[name=" + target + "," : target.endsWith("]") ? target.substring(0, target.length() - 2) + "," : target + "[") + "advancements={" + advancement + "=" + (criterion != null ? "{" + criterion + "=true}" : "true") + "}]";
        String out = "advancement " + (grant ? "grant " : "revoke ") + target;
        if (mode == Mode.Everything) {
            out += " everything";
            return out;
        }
        out += " " + mode.name + " " + advancement;
        if (criterion != null)
            out += " " + criterion;
        return out;
    }

    public static final ParseSequence<AdvancementCommand> SEQUENCE = new ParseSequence<>(AdvancementCommand::new)
            .node("target", new TargetSelectorArgument(), (arg, cmd) -> cmd.target = arg.build())
            .node("ad", new StringArgument(), (arg, cmd) -> cmd.advancement = arg.value())
            .node("cr", new StringArgument(), (arg, cmd) -> cmd.criterion = arg.value())
            .lit("* grant", cmd -> cmd.grant = true).lit("* revoke", cmd -> cmd.grant = false)
            .lit("* test", cmd -> cmd.legacyTest = true)
            .lit((cmd, lit) -> cmd.mode = Mode.parse(lit))
            .rule("advancement ((grant|revoke) <target> (everything|only <ad> [<cr>]|from <ad>|through <ad>|until <ad>)|test <target> <ad> [<cr>])");

    private enum Mode {
        Everything("everything"), Only("only"), From("from"), Through("through"), Until("until");

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
