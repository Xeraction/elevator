package xeraction.elevator.command.execute;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.TargetSelectorArgument;

public class AsAtSub implements Subcommand {
    private boolean as;
    private String target;

    public AsAtSub() {}

    public AsAtSub(boolean as, String target) {
        this.as = as;
        this.target = target;
    }

    public String build() {
        return (as ? "as " : "at ") + target;
    }

    public boolean requiresNext() {
        return true;
    }

    public static final ParseSequence<AsAtSub> SEQUENCE = new ParseSequence<>(AsAtSub::new)
            .node("target", new TargetSelectorArgument(), (arg, cmd) -> cmd.target = arg.build())
            .lit("as", cmd -> cmd.as = true)
            .lit("at", cmd -> cmd.as = false)
            .rule("(as|at) <target>");
}
