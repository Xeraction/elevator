package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.TargetSelectorArgument;
import xeraction.elevator.argument.TextComponentArgument;

public class TellrawCommand implements Command {
    private String target;
    private String msg;

    public String build() {
        return "tellraw " + target + " " + msg;
    }

    public static final ParseSequence<TellrawCommand> SEQUENCE = new ParseSequence<>(TellrawCommand::new)
            .node("target", new TargetSelectorArgument(), (arg, cmd) -> cmd.target = arg.build())
            .node("msg", new TextComponentArgument(), (arg, cmd) -> cmd.msg = arg.tag().build())
            .rule("tellraw <target> <msg>");
}
