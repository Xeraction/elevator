package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.Vec2Argument;
import xeraction.elevator.argument.StringArgument;
import xeraction.elevator.argument.TargetSelectorArgument;

public class SpreadplayersCommand implements Command {
    private String center;
    private String distance;
    private String range;
    private String height;
    private String teams;
    private String target;

    public String build() {
        String s = "spreadplayers " + center + " " + distance + " " + range + " ";
        if (height != null)
            s += "under " + height + " ";
        return s + teams + " " + target;
    }

    public static final ParseSequence<SpreadplayersCommand> SEQUENCE = new ParseSequence<>(SpreadplayersCommand::new)
            .node("center", new Vec2Argument(), (arg, cmd) -> cmd.center = arg.vec())
            .node("dist", new StringArgument(), (arg, cmd) -> cmd.distance = arg.value())
            .node("range", new StringArgument(), (arg, cmd) -> cmd.range = arg.value())
            .node("height", new StringArgument(), (arg, cmd) -> cmd.height = arg.value())
            .node("teams", new StringArgument(), (arg, cmd) -> cmd.teams = arg.value())
            .node("target", new TargetSelectorArgument(), (arg, cmd) -> cmd.target = arg.build())
            .rule("spreadplayers <center> <dist> <range> {under <height>} <teams> <target>");
}
