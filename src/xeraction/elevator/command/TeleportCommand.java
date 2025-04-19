package xeraction.elevator.command;

import xeraction.elevator.Elevator;
import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.*;

public class TeleportCommand implements Command {
    private String target;
    private String loc;
    private FaceMode faceMode;
    private String facing;
    private String anchor;
    public boolean inExecute = false;

    public String build() {
        StringBuilder b = new StringBuilder();
        if (Elevator.executeTP && !inExecute && loc != null && loc.contains("~")) {
            b.append("execute as ").append(target).append(" at @s run tp @s ").append(loc);
        } else {
            b.append("tp");
            if (loc == null && facing != null) { //legacy tp command support
                loc = target;
                target = "@s";
            }
            if (target != null)
                b.append(" ").append(target);
            if (loc != null)
                b.append(" ").append(loc);
        }
        if (faceMode != null) {
            b.append(" ").append(faceMode.name);
            switch (faceMode) {
                case None -> b.append(facing);
                case Facing -> b.append(" ").append(facing);
                case Entity -> {
                    b.append(" ").append(facing);
                    if (anchor != null)
                        b.append(" ").append(anchor);
                }
            }
        }
        return b.toString();
    }

    public static final ParseSequence<TeleportCommand> SEQUENCE = new ParseSequence<>(TeleportCommand::new)
            .node("aloc", new Vec3Argument(), (arg, cmd) -> cmd.target = arg.vec())
            .node("bloc", new Vec3Argument(), (arg, cmd) -> cmd.loc = arg.vec())
            .node("trg", new TargetSelectorArgument(), (arg, cmd) -> cmd.target = arg.build())
            .node("dst", new TargetSelectorArgument(), (arg, cmd) -> cmd.loc = arg.build())
            .node("fent", new TargetSelectorArgument(), (arg, cmd) -> cmd.facing = arg.build())
            .node("anchor", new AnchorArgument(), (arg, cmd) -> cmd.anchor = arg.anchor())
            .node("floc", new Vec3Argument(), (arg, cmd) -> cmd.facing = arg.vec())
            .node("rot", new Vec2Argument(), (arg, cmd) -> {cmd.facing = arg.vec(); cmd.faceMode = FaceMode.None;})
            .lit("* facing", cmd -> cmd.faceMode = FaceMode.Facing).lit("* entity", cmd -> cmd.faceMode = FaceMode.Entity)
            .rule("(tp|teleport) (<aloc>|<trg> [(<bloc>|<dst>)]) [(facing entity <fent> [<anchor>]|facing <floc>|<rot>)]");

    private enum FaceMode {
        None(""), Facing("facing"), Entity("facing entity");

        public final String name;

        FaceMode(String name) {
            this.name = name;
        }
    }
}
