package xeraction.elevator.command.execute;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.SlotArgument;
import xeraction.elevator.argument.TargetSelectorArgument;
import xeraction.elevator.argument.Vec3Argument;

public class SlotSub implements Subcommand {
    private boolean block;
    private String target;
    private String slot;

    public String build() {
        return "slots " + (block ? "block " : "entity ") + target + " " + slot;
    }

    public boolean requiresNext() {
        return false;
    }

    public static final ParseSequence<SlotSub> SEQUENCE = new ParseSequence<>(SlotSub::new)
            .node("pos", new Vec3Argument(), (arg, cmd) -> {cmd.target = arg.vec(); cmd.block = true;})
            .node("target", new TargetSelectorArgument(), (arg, cmd) -> {cmd.target = arg.build(); cmd.block = false;})
            .node("slot", new SlotArgument(), (arg, cmd) -> cmd.slot = arg.build())
            .rule("slots (block <pos>|entity <target>) <slot>");
}
