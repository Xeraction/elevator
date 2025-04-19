package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.StringArgument;

public class GameruleCommand implements Command {
    private String rule;
    private String value;

    public String build() {
        return "gamerule " + rule + (value != null ? " " + value : "");
    }

    public static final ParseSequence<GameruleCommand> SEQUENCE = new ParseSequence<>(GameruleCommand::new)
            .node("rule", new StringArgument(), (arg, cmd) -> cmd.rule = arg.value())
            .node("value", new StringArgument(), (arg, cmd) -> cmd.value = arg.value())
            .rule("gamerule <rule> [<value>]");
}
