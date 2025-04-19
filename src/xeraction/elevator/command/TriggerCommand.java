package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.StringArgument;

public class TriggerCommand implements Command {
    private String objective;
    private String value;
    private boolean add;

    public String build() {
        return "trigger " + objective + (value != null ? " " + (add ? "add " : "set ") + value : "");
    }

    public static final ParseSequence<TriggerCommand> SEQUENCE = new ParseSequence<>(TriggerCommand::new)
            .node("obj", new StringArgument(), (arg, cmd) -> cmd.objective = arg.value())
            .node("value", new StringArgument(), (arg, cmd) -> cmd.value = arg.value())
            .lit("* add", cmd -> cmd.add = true).lit("* set", cmd -> cmd.add = false)
            .rule("trigger <obj> [(add|set) <value>]");
}
