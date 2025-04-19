package xeraction.elevator.command.legacy;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.StringArgument;
import xeraction.elevator.argument.Vec3Argument;
import xeraction.elevator.command.Command;

public class PlacefeatureCommand implements Command {
    private String feature;
    private String pos;

    public String build() {
        return "place feature " + feature + (pos != null ? " " + pos : "");
    }

    public static final ParseSequence<PlacefeatureCommand> SEQUENCE = new ParseSequence<>(PlacefeatureCommand::new)
            .node("feature", new StringArgument(), (arg, cmd) -> cmd.feature = arg.value())
            .node("pos", new Vec3Argument(), (arg, cmd) -> cmd.pos = arg.vec())
            .rule("placefeature <feature> [<pos>]");
}
