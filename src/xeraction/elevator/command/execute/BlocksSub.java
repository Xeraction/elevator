package xeraction.elevator.command.execute;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.Vec3Argument;

public class BlocksSub implements Subcommand {
    private String start;
    private String end;
    private String dest;
    private boolean all;

    public String build() {
        return "blocks " + start + " " + end + " " + dest + (all ? " all" : " masked");
    }

    public boolean requiresNext() {
        return false;
    }

    public static final ParseSequence<BlocksSub> SEQUENCE = new ParseSequence<>(BlocksSub::new)
            .node("start", new Vec3Argument(), (arg, cmd) -> cmd.start = arg.vec())
            .node("end", new Vec3Argument(), (arg, cmd) -> cmd.end = arg.vec())
            .node("dest", new Vec3Argument(), (arg, cmd) -> cmd.dest = arg.vec())
            .lit("* all", cmd -> cmd.all = true).lit("* masked", cmd -> cmd.all = false)
            .rule("blocks <start> <end> <dest> (all|masked)");
}
