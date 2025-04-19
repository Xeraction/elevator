package xeraction.elevator.command.execute;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.Vec3Argument;
import xeraction.elevator.argument.StringArgument;
import xeraction.elevator.argument.TargetSelectorArgument;

public class PositionedSub implements Subcommand {
    private String pos;
    private String target;
    private String height;

    public PositionedSub() {}

    public PositionedSub(String pos) {
        this.pos = pos;
    }

    public String build() {
        StringBuilder b = new StringBuilder("positioned ");
        if (pos != null)
            b.append(pos);
        else if (target != null)
            b.append("as ").append(target);
        else
            b.append("over ").append(height);
        return b.toString();
    }

    public boolean requiresNext() {
        return true;
    }

    public static final ParseSequence<PositionedSub> SEQUENCE = new ParseSequence<>(PositionedSub::new)
            .node("target", new TargetSelectorArgument(), (arg, cmd) -> cmd.target = arg.build())
            .node("height", new StringArgument(), (arg, cmd) -> cmd.height = arg.value())
            .node("pos", new Vec3Argument(), (arg, cmd) -> cmd.pos = arg.vec())
            .rule("positioned (as <target>|over <height>|<pos>)");
}
