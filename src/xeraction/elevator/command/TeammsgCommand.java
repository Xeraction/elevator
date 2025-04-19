package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.StringArgument;

public class TeammsgCommand implements Command {
    private String msg;

    public String build() {
        return "teammsg " + msg;
    }

    public static final ParseSequence<TeammsgCommand> SEQUENCE = new ParseSequence<>(TeammsgCommand::new)
            .node("msg", new StringArgument(true), (arg, cmd) -> cmd.msg = arg.value())
            .rule("(teammsg|tm) <msg>");
}
