package xeraction.elevator.command.execute;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.Vec3Argument;
import xeraction.elevator.argument.ItemArgument;
import xeraction.elevator.argument.SlotArgument;
import xeraction.elevator.argument.TargetSelectorArgument;

public class ItemsSub implements Subcommand {
    private boolean block;
    private String src;
    private String slot;
    private String item;

    public String build() {
        return "items " + (block ? "block " : "entity ") + src + " " + slot + " " + item;
    }

    public boolean requiresNext() {
        return false;
    }

    public static final ParseSequence<ItemsSub> SEQUENCE = new ParseSequence<>(ItemsSub::new)
            .node("srcPos", new Vec3Argument(), (arg, cmd) -> cmd.src = arg.vec())
            .node("src", new TargetSelectorArgument(), (arg, cmd) -> cmd.src = arg.build())
            .node("slot", new SlotArgument(), (arg, cmd) -> cmd.slot = arg.slot())
            .node("item", new ItemArgument(), (arg, cmd) -> cmd.item = arg.build())
            .lit("* block", cmd -> cmd.block = true).lit("* entity", cmd -> cmd.block = false)
            .rule("items (block <srcPos>|entity <src>) <slot> <item>");
}
