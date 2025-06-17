package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;

public class VersionCommand implements Command {

    public String build() {
        return "version";
    }

    public static final ParseSequence<VersionCommand> SEQUENCE = new ParseSequence<>(VersionCommand::new)
            .rule("version");
}
