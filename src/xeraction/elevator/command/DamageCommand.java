package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.StringArgument;
import xeraction.elevator.argument.TargetSelectorArgument;

public class DamageCommand implements Command {
    private String target;
    private String amount;
    private String type;
    private String loc;
    private String entity;
    private String cause;

    public String build() {
        StringBuilder b = new StringBuilder("damage ").append(target).append(" ").append(amount);
        if (type != null)
            b.append(" ").append(type);
        if (loc != null)
            b.append(" at ").append(loc);
        else if (entity != null) {
            b.append(" by ").append(entity);
            if (cause != null)
                b.append(" from ").append(cause);
        }
        return b.toString();
    }

    public static final ParseSequence<DamageCommand> SEQUENCE = new ParseSequence<>(DamageCommand::new)
            .node("target", new TargetSelectorArgument(), (arg, cmd) -> cmd.target = arg.build())
            .node("amount", new StringArgument(), (arg, cmd) -> cmd.amount = arg.value())
            .node("type", new StringArgument(), (arg, cmd) -> cmd.type = arg.value())
            .node("loc", new StringArgument(), (arg, cmd) -> cmd.loc = arg.value())
            .node("entity", new TargetSelectorArgument(), (arg, cmd) -> cmd.entity = arg.build())
            .node("cause", new TargetSelectorArgument(), (arg, cmd) -> cmd.cause = arg.build())
            .rule("damage <target> <amount> [<type>] [(at <loc>|by <entity> [from <cause>])]");
}
