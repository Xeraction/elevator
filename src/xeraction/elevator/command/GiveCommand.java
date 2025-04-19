package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.ItemArgument;
import xeraction.elevator.argument.NBTArgument;
import xeraction.elevator.argument.StringArgument;
import xeraction.elevator.argument.TargetSelectorArgument;
import xeraction.elevator.util.LegacyData;
import xeraction.elevator.util.SNBT;

public class GiveCommand implements Command {
    private String target;
    private String item;
    private String count;
    private String legacyData;

    public String build() {
        if (legacyData != null) {
            item = LegacyData.flattenItem(item, legacyData, null);
            legacyData = null;
        }
        return "give " + target + " " + item + (count != null ? " " + count : "");
    }

    public static final ParseSequence<GiveCommand> SEQUENCE = new ParseSequence<>(GiveCommand::new)
            .node("target", new TargetSelectorArgument(), (arg, cmd) -> cmd.target = arg.build())
            .node("item", new ItemArgument(), (arg, cmd) -> cmd.item = arg.build())
            .node("count", new StringArgument(), (arg, cmd) -> cmd.count = arg.value())
            .node("data", new StringArgument(), (arg, cmd) -> cmd.legacyData = arg.value())
            .node("nbt", new NBTArgument(), (arg, cmd) -> {
                cmd.item = LegacyData.flattenItem(cmd.item, cmd.legacyData, (SNBT.Compound)arg.tag());
                cmd.legacyData = null;
            })
            .rule("give <target> <item> [<count>] [<data>] [<nbt>]");
}
