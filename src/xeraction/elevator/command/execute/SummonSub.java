package xeraction.elevator.command.execute;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.StringArgument;

public class SummonSub implements Subcommand {
    private String entity;

    public String build() {
        return "summon " + entity;
    }

    public boolean requiresNext() {
        return true;
    }

    public static final ParseSequence<SummonSub> SEQUENCE = new ParseSequence<>(SummonSub::new)
            .node("entity", new StringArgument(), (arg, cmd) -> cmd.entity = arg.value())
            .rule("summon <entity>");
}
