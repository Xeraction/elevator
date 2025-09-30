package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.Vec3Argument;
import xeraction.elevator.argument.StringArgument;

public class SetworldspawnCommand implements Command {
    private String pos;
    private String yaw;
    private String pitch;

    public String build() {
        return "setworldspawn" + (pos != null ? " " + pos : "") + (yaw != null ? " " + yaw + " " + (pitch != null ? pitch : "0") : "");
    }

    public static final ParseSequence<SetworldspawnCommand> SEQUENCE = new ParseSequence<>(SetworldspawnCommand::new)
            .node("pos", new Vec3Argument(), (arg, cmd) -> cmd.pos = arg.vec())
            .node("yaw", new StringArgument(), (arg, cmd) -> cmd.yaw = arg.value())
            .node("pitch", new StringArgument(), (arg, cmd) -> cmd.pitch = arg.value())
            .rule("setworldspawn [<pos>] [<yaw>] [<pitch>]");
}
