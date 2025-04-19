package xeraction.elevator.command.legacy;

import xeraction.elevator.Elevator;
import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.StringArgument;
import xeraction.elevator.command.Command;

public class StatsCommand implements Command {
    private String command;

    public String build() {
        return command;
    }

    public static final ParseSequence<StatsCommand> SEQUENCE = new ParseSequence<>(StatsCommand::new)
            .node("cmd", new StringArgument(true), (arg, cmd) -> {
                cmd.command = arg.value();
                Elevator.warn("/stats command doesn't have a direct replacement. Please update by hand.");
            })
            .rule("<cmd>");
}
