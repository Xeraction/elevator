package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.StringArgument;
import xeraction.elevator.argument.TargetSelectorArgument;

public class MsgCommand implements Command {
    private String target;
    private String msg;

    public String build() {
        return "msg " + target + " " + msg;
    }

    public static final ParseSequence<MsgCommand> SEQUENCE = new ParseSequence<>(MsgCommand::new)
            .node("target", new TargetSelectorArgument(), (arg, cmd) -> cmd.target = arg.build())
            .node("msg", new StringArgument(true), (arg, cmd) -> cmd.msg = arg.value())
            .rule("(msg|tell|w) <target> <msg>");
}
