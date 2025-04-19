package xeraction.elevator.command.legacy;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.StringIterator;
import xeraction.elevator.argument.Argument;
import xeraction.elevator.argument.Vec3Argument;
import xeraction.elevator.command.Command;
import xeraction.elevator.util.LegacyData;
import xeraction.elevator.util.SNBT;

public class TestforblockCommand implements Command {
    private String pos;
    private String block;

    public String build() {
        return "execute if block " + pos + " " + block;
    }

    public static final ParseSequence<TestforblockCommand> SEQUENCE = new ParseSequence<>(TestforblockCommand::new)
            .node("pos", new Vec3Argument(), (arg, cmd) -> cmd.pos = arg.vec())
            .node("block", new BlockArgument(), (arg, cmd) -> cmd.block = arg.block)
            .rule("testforblock <pos> <block>");

    private static class BlockArgument implements Argument {
        private String block;

        public boolean parse(StringIterator iterator) {
            block = LegacyData.flattenBlock(iterator.readWord(), iterator.hasMore() ? iterator.readWord() : null, iterator.hasMore() ? SNBT.parse(iterator) : null);
            return true;
        }
    }
}
