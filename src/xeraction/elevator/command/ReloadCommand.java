package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;

public class ReloadCommand implements Command {
    private boolean all = false;

    public String build() {
        return "reload" + (all ? " all" : "");
    }

    public static final ParseSequence<ReloadCommand> SEQUENCE = new ParseSequence<>(ReloadCommand::new)
            .lit("* all", cmd -> cmd.all = true)
            .rule("reload [all]");
}
