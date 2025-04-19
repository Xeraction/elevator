package xeraction.elevator.command.execute;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.AnchorArgument;
import xeraction.elevator.argument.Vec3Argument;
import xeraction.elevator.argument.TargetSelectorArgument;

public class FacingSub implements Subcommand {
    private String pos;
    private String target;
    private String anchor;

    public String build() {
        StringBuilder b = new StringBuilder("facing ");
        if (pos != null)
            b.append(pos);
        else
            b.append(target).append(" ").append(anchor);
        return b.toString();
    }

    public boolean requiresNext() {
        return true;
    }

    public static final ParseSequence<FacingSub> SEQUENCE = new ParseSequence<>(FacingSub::new)
            .node("target", new TargetSelectorArgument(), (arg, cmd) -> cmd.target = arg.build())
            .node("anchor", new AnchorArgument(), (arg, cmd) -> cmd.anchor = arg.anchor())
            .node("pos", new Vec3Argument(), (arg, cmd) -> cmd.pos = arg.vec())
            .rule("facing (entity <target> <anchor>|<pos>)");
}
