package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.NBTArgument;
import xeraction.elevator.argument.StringArgument;
import xeraction.elevator.argument.Vec3Argument;
import xeraction.elevator.argument.BlockPredicateArgument;
import xeraction.elevator.util.BlockPredicate;
import xeraction.elevator.util.LegacyData;
import xeraction.elevator.util.SNBT;

public class SetblockCommand implements Command {
    private String pos;
    private String block;
    private Mode mode;
    private String legacyData;
    private String legacyMode;
    private SNBT.Tag legacyNBT;

    public String build() {
        if (legacyData != null) {
            //pre-1.13 block data
            block = LegacyData.flattenBlock(block, legacyData, legacyNBT != null ? (SNBT.Compound)legacyNBT : null);
            legacyData = null;
            return "setblock " + pos + " " + block + (legacyMode != null ? " " + legacyMode : "");
        }
        return "setblock " + pos + " " + block + (mode != null ? " " + mode.name : "") + (legacyMode != null ? " " + legacyMode : "");
    }

    public static final ParseSequence<SetblockCommand> SEQUENCE = new ParseSequence<>(SetblockCommand::new)
            .node("pos", new Vec3Argument(), (arg, cmd) -> cmd.pos = arg.vec())
            .node("block", new BlockPredicateArgument(), (arg, cmd) -> cmd.block = arg.predicate().build())
            .node("data", new StringArgument(), (arg, cmd) -> cmd.legacyData = arg.value())
            .node("mode", new StringArgument(), (arg, cmd) -> cmd.legacyMode = arg.value())
            .node("nbt", new NBTArgument(), (arg, cmd) -> {
                if (arg.tag() instanceof SNBT.Compound)
                    cmd.legacyNBT = arg.tag();
            })
            .lit((cmd, lit) -> cmd.mode = Mode.parse(lit))
            .rule("setblock <pos> <block> [(destroy|keep|replace|strict|<data> [<mode>] [<nbt>])]");

    private enum Mode {
        Destroy("destroy"), Keep("keep"), Replace("replace"), Strict("strict");

        public final String name;

        Mode(String name) {
            this.name = name;
        }

        public static Mode parse(String mode) {
            for (Mode m : values())
                if (m.name.equals(mode))
                    return m;
            return null;
        }
    }
}
