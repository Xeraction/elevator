package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.NBTArgument;
import xeraction.elevator.argument.Vec3Argument;
import xeraction.elevator.argument.StringArgument;
import xeraction.elevator.util.EntityData;
import xeraction.elevator.util.LegacyData;
import xeraction.elevator.util.SNBT;

public class SummonCommand implements Command {
    private String entity;
    private String pos;
    private SNBT.Compound nbt;

    public String build() {
        return "summon " + entity + (pos != null ? " " + pos : "") + (nbt != null && !nbt.value.isEmpty() ? " " + nbt.build() : "");
    }

    public static final ParseSequence<SummonCommand> SEQUENCE = new ParseSequence<>(SummonCommand::new)
            .node("entity", new StringArgument(), (arg, cmd) -> cmd.entity = LegacyData.renameEntityId(arg.value()))
            .node("pos", new Vec3Argument(), (arg, cmd) -> cmd.pos = arg.vec())
            .node("nbt", new NBTArgument(), (arg, cmd) -> {
                cmd.nbt = (SNBT.Compound)arg.tag();
                cmd.entity = EntityData.elevateEntityData(cmd.nbt, cmd.entity);
            })
            .rule("summon <entity> [<pos>] [<nbt>]");
}
