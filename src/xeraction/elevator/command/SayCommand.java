package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.StringArgument;

public class SayCommand implements Command {
    private String msg;

    public String build() {
        return "say " + msg;
    }

    public static final ParseSequence<SayCommand> SEQUENCE = new ParseSequence<>(SayCommand::new)
            .node("msg", new StringArgument(true), (arg, cmd) -> cmd.msg = arg.value())
            .rule("say <msg>");
}
