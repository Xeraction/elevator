package xeraction.elevator.command.execute;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.TargetSelectorArgument;

public class EntitySub implements Subcommand {
    private String target;

    public String build() {
        return "entity " + target;
    }

    public boolean requiresNext() {
        return false;
    }

    public static final ParseSequence<EntitySub> SEQUENCE = new ParseSequence<>(EntitySub::new)
            .node("target", new TargetSelectorArgument(), (arg, cmd) -> cmd.target = arg.build())
            .rule("entity <target>");
}
