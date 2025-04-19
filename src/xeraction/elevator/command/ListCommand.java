package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;

public class ListCommand implements Command {
    private boolean uuids = false;

    public String build() {
        return "list" + (uuids ? " uuids" : "");
    }

    public static final ParseSequence<ListCommand> SEQUENCE = new ParseSequence<>(ListCommand::new)
            .lit("* uuids", cmd -> cmd.uuids = true)
            .rule("list [uuids]");
}
