package xeraction.elevator.command.legacy;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.StringArgument;
import xeraction.elevator.argument.Vec3Argument;
import xeraction.elevator.command.Command;

public class TestforblocksCommand implements Command {
    private String pos1;
    private String pos2;
    private String pos3;
    private String mode;

    public String build() {
        return "execute if blocks " + pos1 + " " + pos2 + " " + pos3 + " " + (mode != null ? mode : "all");
    }

    public static final ParseSequence<TestforblocksCommand> SEQUENCE = new ParseSequence<>(TestforblocksCommand::new)
            .node("pos1", new Vec3Argument(), (arg, cmd) -> cmd.pos1 = arg.vec())
            .node("pos2", new Vec3Argument(), (arg, cmd) -> cmd.pos2 = arg.vec())
            .node("pos3", new Vec3Argument(), (arg, cmd) -> cmd.pos3 = arg.vec())
            .node("mode", new StringArgument(), (arg, cmd) -> cmd.mode = arg.value())
            .rule("testforblocks <pos1> <pos2> <pos3> [<mode>]");
}
