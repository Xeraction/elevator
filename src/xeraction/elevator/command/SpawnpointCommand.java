package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.Vec3Argument;
import xeraction.elevator.argument.StringArgument;
import xeraction.elevator.argument.TargetSelectorArgument;

public class SpawnpointCommand implements Command {
    private String target;
    private String pos;
    private String yaw;
    private String pitch;

    public String build() {
        return "spawnpoint" + (target != null ? " " + target : "") + (pos != null ? " " + pos : "") + (yaw != null ? " " + yaw + " " + (pitch != null ? pitch : "0") : "");
    }

    public static final ParseSequence<SpawnpointCommand> SEQUENCE = new ParseSequence<>(SpawnpointCommand::new)
            .node("target", new TargetSelectorArgument(), (arg, cmd) -> cmd.target = arg.build())
            .node("pos", new Vec3Argument(), (arg, cmd) -> cmd.pos = arg.vec())
            .node("yaw", new StringArgument(), (arg, cmd) -> cmd.yaw = arg.value())
            .node("pitch", new StringArgument(), (arg, cmd) -> cmd.pitch = arg.value())
            .rule("spawnpoint [<target>] [<pos>] [<yaw>] [<pitch>]");
}
