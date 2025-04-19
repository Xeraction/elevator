package xeraction.elevator.command.legacy;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.command.Command;

public class ToggledownfallCommand implements Command {
    public String build() {
        return "weather clear"; //went with clear because clear weather is nice :)
    }

    public static final ParseSequence<ToggledownfallCommand> SEQUENCE = new ParseSequence<>(ToggledownfallCommand::new)
            .rule("toggledownfall");
}
