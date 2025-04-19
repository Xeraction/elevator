package xeraction.elevator.command;

import xeraction.elevator.ParseException;
import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.ItemArgument;
import xeraction.elevator.argument.StringArgument;
import xeraction.elevator.argument.TargetSelectorArgument;

public class ClearCommand implements Command {
    private String target;
    private String item;
    private int count = -1;

    public String build() {
        String out = "clear";
        if (target != null)
            out += " " + target;
        if (item != null)
            out += " " + item;
        if (count != -1)
            out += " " + count;
        return out;
    }

    public static final ParseSequence<ClearCommand> SEQUENCE = new ParseSequence<>(ClearCommand::new)
            .node("target", new TargetSelectorArgument(), (arg, cmd) -> cmd.target = arg.build())
            .node("item", new ItemArgument(), (arg, cmd) -> cmd.item = arg.build())
            .node("count", new StringArgument(), (arg, cmd) -> cmd.count = Integer.parseInt(arg.value()))
            .node("legacy", new StringArgument(), (arg, cmd) -> {
                throw new ParseException("Pre-1.13 clear command detected. Due to ambiguity in its format, it's not possible to reliably elevate this command with its functionality unchanged.");
            })
            .rule("clear [<target>] [<item>] [<count>] [<legacy>]");
}
