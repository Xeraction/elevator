package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.StringArgument;
import xeraction.elevator.argument.TargetSelectorArgument;
import xeraction.elevator.util.LegacyData;

public class GamemodeCommand implements Command {
    private String gamemode;
    private String target;

    public String build() {
        return "gamemode " + gamemode + (target != null ? " " + target : "");
    }

    public static final ParseSequence<GamemodeCommand> SEQUENCE = new ParseSequence<>(GamemodeCommand::new)
            .node("gm", new StringArgument(), (arg, cmd) -> cmd.gamemode = LegacyData.renameGamemode(arg.value()))
            .node("target", new TargetSelectorArgument(), (arg, cmd) -> cmd.target = arg.build())
            .rule("gamemode <gm> [<target>]");
}
