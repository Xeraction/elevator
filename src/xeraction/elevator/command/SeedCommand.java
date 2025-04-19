package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;

public class SeedCommand implements Command {
    public String build() {
        return "seed";
    }

    public static final ParseSequence<SeedCommand> SEQUENCE = new ParseSequence<>(SeedCommand::new)
            .rule("seed");
}
