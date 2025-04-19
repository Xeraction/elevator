package xeraction.elevator.command.execute;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.StringArgument;

public class AlignSub implements Subcommand {
    private String axes;

    public String build() {
        return "align " + axes;
    }

    public boolean requiresNext() {
        return true;
    }

    public static final ParseSequence<AlignSub> SEQUENCE = new ParseSequence<>(AlignSub::new)
            .node("axes", new StringArgument(), (arg, cmd) -> cmd.axes = arg.value())
            .rule("align <axes>");
}
