package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.TargetSelectorArgument;

public class SpectateCommand implements Command {
    private String target;
    private String player;

    public String build() {
        return "spectate" + (target != null ? " " + target : "") + (player != null ? " " + player : "");
    }

    public static final ParseSequence<SpectateCommand> SEQUENCE = new ParseSequence<>(SpectateCommand::new)
            .node("target", new TargetSelectorArgument(), (arg, cmd) -> cmd.target = arg.build())
            .node("player", new TargetSelectorArgument(), (arg, cmd) -> cmd.player = arg.build())
            .rule("spectate [<target>] [<player>]");
}
