package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.StringArgument;
import xeraction.elevator.argument.TargetSelectorArgument;

public class DialogCommand implements Command {
    private boolean show;
    private String target;
    private String dialog;

    public String build() {
        return "dialog " + (show ? "show " : "clear ") + target + (dialog != null ? " " + dialog : "");
    }

    public static final ParseSequence<DialogCommand> SEQUENCE = new ParseSequence<>(DialogCommand::new)
            .node("target", new TargetSelectorArgument(), (arg, cmd) -> cmd.target = arg.build())
            .node("dialog", new StringArgument(true), (arg, cmd) -> cmd.dialog = arg.value())
            .lit("* show", cmd -> cmd.show = true).lit("* clear", cmd -> cmd.show = false)
            .rule("dialog (show <target> <dialog>|clear <target>)");
}
