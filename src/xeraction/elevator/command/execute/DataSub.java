package xeraction.elevator.command.execute;

import xeraction.elevator.Elevator;
import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.Vec3Argument;
import xeraction.elevator.argument.StringArgument;
import xeraction.elevator.argument.TargetSelectorArgument;
import xeraction.elevator.command.DataCommand;
import xeraction.elevator.util.NBTPath;

public class DataSub implements Subcommand {
    private Mode mode;
    private String target;
    private NBTPath path;

    public String build() {
        return "data " + mode.name + " " + target + " " + path.build();
    }

    public boolean requiresNext() {
        return false;
    }

    public static final ParseSequence<DataSub> SEQUENCE = new ParseSequence<>(DataSub::new)
            .node("path", new DataCommand.NBTPathArgument(), (arg, cmd) -> {cmd.path = arg.path(); warn(arg.path().build());})
            .node("pos", new Vec3Argument(), (arg, cmd) -> cmd.target = arg.vec())
            .node("target", new TargetSelectorArgument(), (arg, cmd) -> cmd.target = arg.build())
            .node("src", new StringArgument(), (arg, cmd) -> cmd.target = arg.value())
            .lit("* block", cmd -> cmd.mode = Mode.Block)
            .lit("* entity", cmd -> cmd.mode = Mode.Entity)
            .lit("* storage", cmd -> cmd.mode = Mode.Storage)
            .rule("data (block <pos> <path>|entity <target> <path>|storage <src> <path>)");

    private static void warn(String nbt) {
        Elevator.warn("NBT in the /execute if data command cannot be checked for correctness due to its ambiguous nature. Please verify the NBT by hand.\n" + nbt);
    }

    private enum Mode {
        Block("block"), Entity("entity"), Storage("storage");

        public final String name;

        Mode(String name) {
            this.name = name;
        }
    }
}
