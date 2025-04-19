package xeraction.elevator.command;

import xeraction.elevator.Elevator;
import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.*;
import xeraction.elevator.util.SNBT;

public class LootCommand implements Command {
    private TargetMode targetMode;
    private String target;
    private String slot;
    private String count;
    private SourceMode sourceMode;
    private SNBT.Tag lootTable;
    private String sourceTarget;
    private String tool;

    public String build() {
        String trg = targetMode.name + " " + target + (slot != null ? " " + slot : "") + (count != null ? " " + count : "");
        String src = sourceMode.name + (lootTable != null ? " " + lootTable : "") + (sourceTarget != null ? " " + sourceTarget : "") + (tool != null ? " " + tool : "");
        return "loot " + trg + " " + src;
    }

    public static final ParseSequence<LootCommand> SEQUENCE = new ParseSequence<>(LootCommand::new)
            .node("trg", new TargetSelectorArgument(), (arg, cmd) -> cmd.target = arg.build())
            .node("pos", new Vec3Argument(), (arg, cmd) -> cmd.target = arg.vec())
            .node("slot", new SlotArgument(), (arg, cmd) -> cmd.slot = arg.slot())
            .node("count", new StringArgument(), (arg, cmd) -> cmd.count = arg.value())
            .node("loot", new NBTArgument(), (arg, cmd) -> {
                cmd.lootTable = arg.tag();
                Elevator.warn("Inline loot table definition will not be upgraded because I'm not crazy.");
            })
            .node("srcPos", new Vec3Argument(), (arg, cmd) -> cmd.sourceTarget = arg.vec())
            .node("srcTrg", new TargetSelectorArgument(), (arg, cmd) -> cmd.sourceTarget = arg.build())
            .node("tool", new ItemArgument(), (arg, cmd) -> cmd.tool = arg.build())
            .lit("* give", cmd -> cmd.targetMode = TargetMode.Give).lit("* insert", cmd -> cmd.targetMode = TargetMode.Insert)
            .lit("* spawn", cmd -> cmd.targetMode = TargetMode.Spawn).lit("* block", cmd -> cmd.targetMode = TargetMode.ReplaceBlock)
            .lit("* entity", cmd -> cmd.targetMode = TargetMode.ReplaceEntity).lit("* fish", cmd -> cmd.sourceMode = SourceMode.Fish)
            .lit("* loot", cmd -> cmd.sourceMode = SourceMode.Loot).lit("* kill", cmd -> cmd.sourceMode = SourceMode.Kill)
            .lit("* mine", cmd -> cmd.sourceMode = SourceMode.Mine)
            .lit("* mainhand", cmd -> cmd.tool = "mainhand").lit("* offhand", cmd -> cmd.tool = "offhand")
            .sub("trg", "(give <trg>|insert <pos>|spawn <pos>|replace (block <pos>|entity <trg>) <slot> [<count>])")
            .sub("src", "(fish <loot> <srcPos> /tool/|loot <loot>|kill <srcTrg>|mine <srcPos> /tool/)")
            .sub("tool", "[(mainhand|offhand|<tool>)]")
            .rule("loot /trg/ /src/");

    private enum TargetMode {
        Give("give"), Insert("insert"), Spawn("spawn"), ReplaceBlock("replace block"), ReplaceEntity("replace entity");

        public final String name;

        TargetMode(String name) {
            this.name = name;
        }
    }

    private enum SourceMode {
        Fish("fish"), Loot("loot"), Kill("kill"), Mine("mine");

        public final String name;

        SourceMode(String name) {
            this.name = name;
        }
    }
}
