package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.AnchorArgument;
import xeraction.elevator.argument.Vec3Argument;
import xeraction.elevator.argument.StringArgument;
import xeraction.elevator.argument.TargetSelectorArgument;

public class RotateCommand implements Command {
    private String target;
    private Mode mode = Mode.None;
    private String facing;
    private String anchor;

    public String build() {
        String s = "rotate " + target + " " + mode.name;
        return switch (mode) {
            case None -> s + facing;
            case Facing -> s + " " + facing;
            case Entity -> s + " " + facing + (anchor != null ? " " + anchor : "");
        };
    }

    public static final ParseSequence<RotateCommand> SEQUENCE = new ParseSequence<>(RotateCommand::new)
            .node("target", new TargetSelectorArgument(), (arg, cmd) -> cmd.target = arg.build())
            .node("rot", new StringArgument(), (arg, cmd) -> cmd.facing = arg.value())
            .node("loc", new Vec3Argument(), (arg, cmd) -> cmd.facing = arg.vec())
            .node("ent", new TargetSelectorArgument(), (arg, cmd) -> cmd.facing = arg.build())
            .node("anchor", new AnchorArgument(), (arg, cmd) -> cmd.anchor = arg.anchor())
            .lit("* facing", cmd -> cmd.mode = Mode.Facing).lit("* entity", cmd -> cmd.mode = Mode.Entity)
            .rule("rotate <target> (facing entity <rot> [<anchor>]|facing <loc>|<ent>)");

    private enum Mode {
        None(""), Facing("facing"), Entity("facing entity");

        public final String name;

        Mode(String name) {
            this.name = name;
        }
    }
}
