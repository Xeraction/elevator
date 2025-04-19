package xeraction.elevator.command;

import xeraction.elevator.Elevator;
import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.*;
import xeraction.elevator.util.SNBT;

public class ItemCommand implements Command {
    private boolean modify;
    private boolean with;
    private String target;
    private boolean dstBlock;
    private String slot;
    private SNBT.Tag mod;
    private String item;
    private String count;
    private String srcTarget;
    private boolean srcBlock;
    private String srcSlot;

    public String build() {
        String trg = (dstBlock ? "block " : "entity ") + target + " " + slot + " ";
        if (modify)
            return "item modify " + trg + mod.build();
        if (with)
            return "item replace " + trg + "with " + item + (count != null ? " " + count : "");
        return "item replace " + trg + "from " + (srcBlock ? "block " : "entity ") + srcTarget + " " + srcSlot + (mod != null ? " " + mod.build() : "");
    }

    public static final ParseSequence<ItemCommand> SEQUENCE = new ParseSequence<>(ItemCommand::new)
            .node("pos", new Vec3Argument(), (arg, cmd) -> {cmd.target = arg.vec(); cmd.dstBlock = true;})
            .node("target", new TargetSelectorArgument(), (arg, cmd) -> {cmd.target = arg.build(); cmd.dstBlock = false;})
            .node("slot", new SlotArgument(), (arg, cmd) -> cmd.slot = arg.slot())
            .node("mod", new NBTArgument(), (arg, cmd) -> {
                cmd.mod = arg.tag();
                Elevator.warn("Inline modifier definition will not be upgraded because I'm not crazy.");
            })
            .node("item", new ItemArgument(), (arg, cmd) -> cmd.item = arg.build())
            .node("count", new StringArgument(), (arg, cmd) -> cmd.count = arg.value())
            .node("srcPos", new Vec3Argument(), (arg, cmd) -> {cmd.srcTarget = arg.vec(); cmd.srcBlock = true;})
            .node("srcTrg", new TargetSelectorArgument(), (arg, cmd) -> {cmd.srcTarget = arg.build(); cmd.srcBlock = false;})
            .node("srcSlot", new SlotArgument(), (arg, cmd) -> cmd.srcSlot = arg.slot())
            .lit("* modify", cmd -> cmd.modify = true).lit("* replace", cmd -> cmd.modify = false)
            .lit("* with", cmd -> cmd.with = true).lit("* from", cmd -> cmd.with = false)
            .sub("trg", "(block <pos>|entity <target>) <slot>")
            .rule("item (modify /trg/ <mod>|replace /trg/ (with <item> [<count>]|from (block <srcPos>|entity <srcTrg>) <srcSlot> [<mod>]))");
}
