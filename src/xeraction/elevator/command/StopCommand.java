package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;

public class StopCommand implements Command {
    public String build() {
        return "stop";
    }

    public static final ParseSequence<StopCommand> SEQUENCE = new ParseSequence<>(StopCommand::new)
            .rule("stop");
}
