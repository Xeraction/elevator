package xeraction.elevator.command.execute;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.StringIterator;
import xeraction.elevator.argument.StringArgument;
import xeraction.elevator.util.NumberRange;

public class StopwatchSub implements Subcommand {

    private String id;
    private NumberRange range;

    public String build() {
        return "stopwatch " + id + " " + range;
    }

    public boolean requiresNext() {
        return false;
    }

    public static final ParseSequence<StopwatchSub> SEQUENCE = new ParseSequence<>(StopwatchSub::new)
            .node("id", new StringArgument(), (arg, cmd) -> cmd.id = arg.value())
            .node("range", new StringArgument(), (arg, cmd) -> cmd.range = NumberRange.parse(new StringIterator(arg.value())))
            .rule("stopwatch <id> <range>");
}
