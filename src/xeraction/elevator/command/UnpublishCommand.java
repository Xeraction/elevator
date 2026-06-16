package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;

public class UnpublishCommand implements Command {

    public String build() {
        return "unpublish";
    }

    public static final ParseSequence<UnpublishCommand> SEQUENCE = new ParseSequence<>(UnpublishCommand::new)
            .rule("unpublish");
}
