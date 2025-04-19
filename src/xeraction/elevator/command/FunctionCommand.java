package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.NBTArgument;
import xeraction.elevator.argument.Vec3Argument;
import xeraction.elevator.argument.StringArgument;
import xeraction.elevator.argument.TargetSelectorArgument;
import xeraction.elevator.util.NBTPath;
import xeraction.elevator.util.SNBT;

public class FunctionCommand implements Command {
    private String name;
    private SNBT.Compound args;
    private Mode mode;
    private String src;
    private NBTPath path;
    private boolean legacyExecute = false;
    private boolean legacyIf;

    public String build() {
        if (legacyExecute)
            return "execute " + (legacyIf ? "if" : "unless") + " entity " + src + " run function " + name;
        return "function " + name + (args != null ? " " + args : "") + (mode != null ? " with " + mode.name + " " + src + (path != null ? " " + path.build() : "") : "");
    }

    public static final ParseSequence<FunctionCommand> SEQUENCE = new ParseSequence<>(FunctionCommand::new)
            .node("name", new StringArgument(), (arg, cmd) -> cmd.name = arg.value())
            .node("args", new NBTArgument(), (arg, cmd) -> cmd.args = (SNBT.Compound)arg.tag())
            .node("srcPos", new Vec3Argument(), (arg, cmd) -> cmd.src = arg.vec())
            .node("srcTrg", new TargetSelectorArgument(), (arg, cmd) -> cmd.src = arg.build())
            .node("srcStg", new StringArgument(), (arg, cmd) -> cmd.src = arg.value())
            .node("path", new DataCommand.NBTPathArgument(), (arg, cmd) -> cmd.path = arg.path())
            .node("oldTrg", new TargetSelectorArgument(), (arg, cmd) -> {
                cmd.src = arg.build();
                cmd.legacyExecute = true;
            })
            .lit("* block", cmd -> cmd.mode = Mode.Block).lit("* entity", cmd -> cmd.mode = Mode.Entity)
            .lit("* storage", cmd -> cmd.mode = Mode.Storage)
            .lit("* if", cmd -> cmd.legacyIf = true).lit("* unless", cmd -> cmd.legacyIf = false)
            .rule("function <name> [(with (block <srcPos>|entity <srcTrg>|storage <srcStg>) [<path>]|(if|unless) <oldTrg>|<args>)]");

    private enum Mode {
        Block("block"), Entity("entity"), Storage("storage");

        public final String name;

        Mode(String name) {
            this.name = name;
        }
    }
}
