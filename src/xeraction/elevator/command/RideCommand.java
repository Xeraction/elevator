package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.TargetSelectorArgument;

public class RideCommand implements Command {
    private String target;
    private String vehicle;

    public String build() {
        return "ride " + target + (vehicle != null ? " mount " + vehicle : " dismount");
    }

    public static final ParseSequence<RideCommand> SEQUENCE = new ParseSequence<>(RideCommand::new)
            .node("target", new TargetSelectorArgument(), (arg, cmd) -> cmd.target = arg.build())
            .node("vehicle", new TargetSelectorArgument(), (arg, cmd) -> cmd.vehicle = arg.build())
            .rule("ride <target> (mount <vehicle>|dismount)");
}
