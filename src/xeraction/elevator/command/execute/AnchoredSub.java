package xeraction.elevator.command.execute;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.AnchorArgument;

public class AnchoredSub implements Subcommand {
    private String anchor;

    public String build() {
        return "anchored " + anchor;
    }

    public boolean requiresNext() {
        return true;
    }

    public static final ParseSequence<AnchoredSub> SEQUENCE = new ParseSequence<>(AnchoredSub::new)
            .node("anchor", new AnchorArgument(), (arg, cmd) -> cmd.anchor = arg.anchor())
            .rule("anchored <anchor>");
}
