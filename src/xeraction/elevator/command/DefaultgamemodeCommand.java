package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.StringArgument;

public class DefaultgamemodeCommand implements Command {
    private String gamemode;

    public String build() {
        return "defaultgamemode " + gamemode;
    }

    public static final ParseSequence<DefaultgamemodeCommand> SEQUENCE = new ParseSequence<>(DefaultgamemodeCommand::new)
            .node("gamemode", new StringArgument(), (arg, cmd) -> cmd.gamemode = arg.value())
            .rule("defaultgamemode <gamemode>");
}
