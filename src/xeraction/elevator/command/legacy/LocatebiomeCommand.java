package xeraction.elevator.command.legacy;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.StringArgument;
import xeraction.elevator.command.Command;

public class LocatebiomeCommand implements Command {
    private String biome;

    public String build() {
        return "locate biome " + biome;
    }

    public static final ParseSequence<LocatebiomeCommand> SEQUENCE = new ParseSequence<>(LocatebiomeCommand::new)
            .node("biome", new StringArgument(), (arg, cmd) -> cmd.biome = arg.value())
            .rule("locatebiome <biome>");
}
