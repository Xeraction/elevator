package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.TargetSelectorArgument;

public class SwingCommand implements Command {

    private String target;
    private Hand hand = Hand.None;

    public String build() {
        return "swing" + (target != null ? " " + target : "") + (hand != Hand.None ? " " + (hand == Hand.Mainhand ? "mainhand" : "offhand") : "");
    }

    public static final ParseSequence<SwingCommand> SEQUENCE = new ParseSequence<>(SwingCommand::new)
            .node("target", new TargetSelectorArgument(), (arg, cmd) -> cmd.target = arg.build())
            .lit("* mainhand", cmd -> cmd.hand = Hand.Mainhand)
            .lit("* offhand", cmd -> cmd.hand = Hand.Offhand)
            .rule("swing [<target>] [(mainhand|offhand)]");

    private enum Hand {
        None,
        Mainhand,
        Offhand
    }
}
