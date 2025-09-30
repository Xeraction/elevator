package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.StringArgument;

public class FetchprofileCommand implements Command {
    private boolean name;
    private String id;

    public String build() {
        return "fetchprofile " + (name ? "name " : "id ") + id;
    }

    public static final ParseSequence<FetchprofileCommand> SEQUENCE = new ParseSequence<>(FetchprofileCommand::new)
            .node("id", new StringArgument(), (arg, cmd) -> cmd.id = arg.value())
            .lit("name", cmd -> cmd.name = true).lit("id", cmd -> cmd.name = false)
            .rule("fetchprofile (name|id) <id>");
}
