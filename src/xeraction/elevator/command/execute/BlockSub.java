package xeraction.elevator.command.execute;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.Vec3Argument;
import xeraction.elevator.argument.BlockPredicateArgument;
import xeraction.elevator.util.BlockPredicate;

public class BlockSub implements Subcommand {
    private String pos;
    private BlockPredicate block;

    public String build() {
        return "block " + pos + " " + block.build();
    }

    public boolean requiresNext() {
        return false;
    }

    public static final ParseSequence<BlockSub> SEQUENCE = new ParseSequence<>(BlockSub::new)
            .node("pos", new Vec3Argument(), (arg, cmd) -> cmd.pos = arg.vec())
            .node("block", new BlockPredicateArgument(), (arg, cmd) -> cmd.block = arg.predicate())
            .rule("block <pos> <block>");
}
