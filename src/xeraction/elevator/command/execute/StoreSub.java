package xeraction.elevator.command.execute;

import xeraction.elevator.Elevator;
import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.Vec3Argument;
import xeraction.elevator.argument.StringArgument;
import xeraction.elevator.argument.TargetSelectorArgument;
import xeraction.elevator.command.DataCommand;
import xeraction.elevator.util.NBTPath;

public class StoreSub implements Subcommand {
    private boolean result;
    private Mode mode;
    private NBTPath path;
    private String type;
    private String scale;
    private String general;
    private boolean max;
    private String target;

    public String build() {
        StringBuilder b = new StringBuilder("store ").append(result ? "result " : "success ");
        b.append(mode.name).append(" ");
        switch (mode) {
            case Block, Entity, Storage -> b.append(target).append(" ").append(path.build()).append(" ").append(type).append(" ").append(scale);
            case Bossbar -> b.append(general).append(max ? " max" : " value");
            case Score -> b.append(target).append(" ").append(general);
        }
        return b.toString();
    }

    public boolean requiresNext() {
        return true;
    }

    public static final ParseSequence<StoreSub> SEQUENCE = new ParseSequence<>(StoreSub::new)
            .node("path", new DataCommand.NBTPathArgument(), (arg, cmd) -> {cmd.path = arg.path(); warn(cmd.path.build());})
            .node("type", new StringArgument(), (arg, cmd) -> cmd.type = arg.value())
            .node("scale", new StringArgument(), (arg, cmd) -> cmd.scale = arg.value())
            .node("trgPos", new Vec3Argument(), (arg, cmd) -> cmd.target = arg.vec())
            .node("trg", new TargetSelectorArgument(), (arg, cmd) -> cmd.target = arg.build())
            .node("gen", new StringArgument(), (arg, cmd) -> cmd.general = arg.value())
            .lit("* result", cmd -> cmd.result = true).lit("* success", cmd -> cmd.result = false)
            .lit("* max", cmd -> cmd.max = true).lit("* value", cmd -> cmd.max = false)
            .lit("* block", cmd -> cmd.mode = Mode.Block).lit("* bossbar", cmd -> cmd.mode = Mode.Bossbar)
            .lit("* entity", cmd -> cmd.mode = Mode.Entity).lit("* score", cmd -> cmd.mode = Mode.Score)
            .lit("* storage", cmd -> cmd.mode = Mode.Storage)
            .sub("pts", "<path> <type> <scale>")
            .rule("store (result|success) (block <trgPos> /pts/|bossbar <gen> (max|value)|entity <trg> /pts/|score <trg> <gen>|storage <trg> /pts/)");

    private static void warn(String nbt) {
        Elevator.warn("NBT in the /execute store command cannot be checked for correctness due to its ambiguous nature. Please verify the NBT by hand.\n" + nbt);
    }

    private enum Mode {
        Block("block"), Bossbar("bossbar"), Entity("entity"), Score("score"), Storage("storage");

        public final String name;

        Mode(String name) {
            this.name = name;
        }
    }
}
