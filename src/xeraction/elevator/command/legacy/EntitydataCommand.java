package xeraction.elevator.command.legacy;

import xeraction.elevator.Elevator;
import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.NBTArgument;
import xeraction.elevator.argument.TargetSelectorArgument;
import xeraction.elevator.command.Command;
import xeraction.elevator.util.EntityData;
import xeraction.elevator.util.SNBT;

public class EntitydataCommand implements Command {
    private String target;
    private SNBT.Tag tag;

    public String build() {
        return "data merge entity " + target + " " + tag.build();
    }

    public static final ParseSequence<EntitydataCommand> SEQUENCE = new ParseSequence<>(EntitydataCommand::new)
            .node("target", new TargetSelectorArgument(), (arg, cmd) -> cmd.target = arg.build())
            .node("tag", new NBTArgument(), (arg, cmd) -> {
                cmd.tag = arg.tag();
                EntityData.elevateEntityData((SNBT.Compound)cmd.tag, null);
                Elevator.warn("Entity-specific NBT in the /data command cannot be checked for correctness due to its ambiguous nature. Please verify the NBT by hand.");
            })
            .rule("entitydata <target> <tag>");
}
