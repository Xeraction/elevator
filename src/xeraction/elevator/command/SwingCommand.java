package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.StringArgument;
import xeraction.elevator.argument.TargetSelectorArgument;

public class SwingCommand implements Command {

    private String target;
    private Hand hand = Hand.None;
    private String animation;
    private String duration;

    public String build() {
        return "swing" + (target != null ? " " + target : "") + (hand != Hand.None ? " " + (hand == Hand.Mainhand ? "mainhand" : "offhand") : "")
                + (animation != null ? " " + animation : "") + (duration != null ? " " + duration : "");
    }

    public static final ParseSequence<SwingCommand> SEQUENCE = new ParseSequence<>(SwingCommand::new)
            .node("target", new TargetSelectorArgument(), (arg, cmd) -> cmd.target = arg.build())
            .node("anim", new StringArgument(), (arg, cmd) -> cmd.animation = arg.value())
            .node("dur", new StringArgument(), (arg, cmd) -> cmd.duration = arg.value())
            .lit("* mainhand", cmd -> cmd.hand = Hand.Mainhand)
            .lit("* offhand", cmd -> cmd.hand = Hand.Offhand)
            .rule("swing [<target>] [(mainhand|offhand)] [<anim>] [<dur>]");

    private enum Hand {
        None,
        Mainhand,
        Offhand
    }
}
