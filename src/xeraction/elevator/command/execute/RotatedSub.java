package xeraction.elevator.command.execute;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.RotationArgument;
import xeraction.elevator.argument.TargetSelectorArgument;

public class RotatedSub implements Subcommand {
    private String rot;
    private String target;

    public String build() {
        return "rotated " + (rot == null ? "as " + target : rot);
    }

    public boolean requiresNext() {
        return true;
    }

    public static final ParseSequence<RotatedSub> SEQUENCE = new ParseSequence<>(RotatedSub::new)
            .node("target", new TargetSelectorArgument(), (arg, cmd) -> cmd.target = arg.build())
            .node("rot", new RotationArgument(), (arg, cmd) -> cmd.rot = arg.rot())
            .rule("rotated (as <target>|<rot>)");
}
