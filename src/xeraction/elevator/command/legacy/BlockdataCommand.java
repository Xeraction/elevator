package xeraction.elevator.command.legacy;

import xeraction.elevator.Elevator;
import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.NBTArgument;
import xeraction.elevator.argument.Vec3Argument;
import xeraction.elevator.command.Command;
import xeraction.elevator.util.BlockEntityData;
import xeraction.elevator.util.SNBT;

public class BlockdataCommand implements Command {
    private String pos;
    private SNBT.Tag tag;
    //the wiki specifies a <UserCreator> argument but then doesn't elaborate. looked into the game's code, turns out it doesn't exist. good job wiki

    public String build() {
        return "data merge block " + pos + " " + tag.build();
    }

    public static final ParseSequence<BlockdataCommand> SEQUENCE = new ParseSequence<>(BlockdataCommand::new)
            .node("pos", new Vec3Argument(), (arg, cmd) -> cmd.pos = arg.vec())
            .node("tag", new NBTArgument(), (arg, cmd) -> {
                cmd.tag = arg.tag();
                BlockEntityData.elevateBlockEntity((SNBT.Compound)cmd.tag, null);
                Elevator.warn("Block-specific NBT in the /data command cannot be checked for correctness due to its ambiguous nature. Please verify the NBT by hand.");
            })
            .rule("blockdata <pos> <tag>");
}
