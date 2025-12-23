package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.StringArgument;
import xeraction.elevator.util.LegacyData;

public class GameruleCommand implements Command {
    private String rule;
    private String value;
    private boolean upgraded = false;

    public String build() {
        if (!upgraded) {
            rule = LegacyData.renameGameRule(rule, value);
            upgraded = true;
        }
        return "gamerule " + rule;
    }

    public static final ParseSequence<GameruleCommand> SEQUENCE = new ParseSequence<>(GameruleCommand::new)
            .node("rule", new StringArgument(), (arg, cmd) -> cmd.rule = arg.value())
            .node("value", new StringArgument(), (arg, cmd) -> cmd.value = arg.value())
            .rule("gamerule <rule> [<value>]");
}
