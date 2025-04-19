package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.StringArgument;

public class HelpCommand implements Command {
    private String cmd;

    public String build() {
        return "help" + (cmd != null ? " " + cmd : "");
    }

    public static final ParseSequence<HelpCommand> SEQUENCE = new ParseSequence<>(HelpCommand::new)
            .node("cmd", new StringArgument(), (arg, cmd) -> cmd.cmd = arg.value())
            .rule("help [<cmd>]");
}
