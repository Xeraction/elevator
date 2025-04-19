package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.Vec3Argument;
import xeraction.elevator.argument.StringArgument;

public class SetworldspawnCommand implements Command {
    private String pos;
    private String angle;

    public String build() {
        return "setworldspawn" + (pos != null ? " " + pos : "") + (angle != null ? " " + angle : "");
    }

    public static final ParseSequence<SetworldspawnCommand> SEQUENCE = new ParseSequence<>(SetworldspawnCommand::new)
            .node("pos", new Vec3Argument(), (arg, cmd) -> cmd.pos = arg.vec())
            .node("angle", new StringArgument(), (arg, cmd) -> cmd.angle = arg.value())
            .rule("setworldspawn [<pos>] [<angle>]");
}
