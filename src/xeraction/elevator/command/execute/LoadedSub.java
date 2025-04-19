package xeraction.elevator.command.execute;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.Vec3Argument;

public class LoadedSub implements Subcommand {
    private String pos;

    public String build() {
        return "loaded " + pos;
    }

    public boolean requiresNext() {
        return false;
    }

    public static final ParseSequence<LoadedSub> SEQUENCE = new ParseSequence<>(LoadedSub::new)
            .node("pos", new Vec3Argument(), (arg, cmd) -> cmd.pos = arg.vec())
            .rule("loaded <pos>");
}
