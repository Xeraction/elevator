package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;

public class SaveAllCommand implements Command {
    private boolean flush = false;

    public String build() {
        return "save-all" + (flush ? " " + "flush" : "");
    }

    public static final ParseSequence<SaveAllCommand> SEQUENCE = new ParseSequence<>(SaveAllCommand::new)
            .lit("* flush", cmd -> cmd.flush = true)
            .rule("save-all [flush]");
}
