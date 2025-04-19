package xeraction.elevator.command.legacy;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.*;
import xeraction.elevator.command.Command;
import xeraction.elevator.util.LegacyData;
import xeraction.elevator.util.SNBT;

public class ReplaceitemCommand implements Command {
    private boolean blockMode;
    private String target;
    private String slot;
    private String item;
    private String count;
    private String legacyData;

    public String build() {
        if (legacyData != null) {
            item = LegacyData.flattenItem(item, legacyData, null);
            legacyData = null;
        }
        return "item replace " + (blockMode ? "block " : "entity ") + target + " " + slot + " with " + item + (count != null ? " " + count : "");
    }

    public static final ParseSequence<ReplaceitemCommand> SEQUENCE = new ParseSequence<>(ReplaceitemCommand::new)
            .node("pos", new Vec3Argument(), (arg, cmd) -> cmd.target = arg.vec())
            .node("target", new TargetSelectorArgument(), (arg, cmd) -> cmd.target = arg.build())
            .node("slot", new SlotArgument(), (arg, cmd) -> cmd.slot = arg.slot())
            .node("item", new ItemArgument(), (arg, cmd) -> cmd.item = arg.build())
            .node("count", new StringArgument(), (arg, cmd) -> cmd.count = arg.value())
            .node("data", new StringArgument(), (arg, cmd) -> cmd.legacyData = arg.value())
            .node("nbt", new NBTArgument(), (arg, cmd) -> {
                cmd.item = LegacyData.flattenItem(cmd.item, cmd.legacyData, (SNBT.Compound)arg.tag());
                cmd.legacyData = null;
            })
            .lit("* block", cmd -> cmd.blockMode = true).lit("* entity", cmd -> cmd.blockMode = false)
            .rule("replaceitem (block <pos>|entity <target>) <slot> <item> [<count>] [<data>] [<nbt>]");
}
