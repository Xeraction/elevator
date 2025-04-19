package xeraction.elevator.command.execute;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.StringArgument;

public class InOnSub implements Subcommand {
    private boolean in;
    private String value; //technically different, bit idrc about validating the relation entity of an execute command

    public String build() {
        return (in ? "in " : "on ") + value;
    }

    public boolean requiresNext() {
        return true;
    }

    public static final ParseSequence<InOnSub> SEQUENCE = new ParseSequence<>(InOnSub::new)
            .node("val", new StringArgument(), (arg, cmd) -> cmd.value = arg.value())
            .lit("in", cmd -> cmd.in = true)
            .lit("on", cmd -> cmd.in = false)
            .rule("(in|on) <val>");
}
