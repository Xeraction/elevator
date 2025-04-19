package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.StringArgument;
import xeraction.elevator.argument.TargetSelectorArgument;
import xeraction.elevator.util.LegacyData;

public class StopsoundCommand implements Command {
    private String target;
    private String source;
    private String sound;

    public String build() {
        return "stopsound " + target + (source != null ? " " + source : "") + (sound != null ? " " + sound : "");
    }

    public static final ParseSequence<StopsoundCommand> SEQUENCE = new ParseSequence<>(StopsoundCommand::new)
            .node("target", new TargetSelectorArgument(), (arg, cmd) -> cmd.target = arg.build())
            .node("source", new StringArgument(), (arg, cmd) -> cmd.source = arg.value())
            .node("sound", new StringArgument(), (arg, cmd) -> cmd.sound = LegacyData.renameSound(arg.value()))
            .rule("stopsound <target> [<source>] [<sound>]");
}
