package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.Vec3Argument;
import xeraction.elevator.argument.StringArgument;

public class FillbiomeCommand implements Command {
    private String from;
    private String to;
    private String biome;
    private String filter;

    public String build() {
        return "fillbiome " + from + " " + to + " " + biome + (filter != null ? " replace " + filter : "");
    }

    public static final ParseSequence<FillbiomeCommand> SEQUENCE = new ParseSequence<>(FillbiomeCommand::new)
            .node("from", new Vec3Argument(), (arg, cmd) -> cmd.from = arg.vec())
            .node("to", new Vec3Argument(), (arg, cmd) -> cmd.to = arg.vec())
            .node("biome", new StringArgument(), (arg, cmd) -> cmd.biome = arg.value())
            .node("filter", new StringArgument(), (arg, cmd) -> cmd.filter = arg.value())
            .rule("fillbiome <from> <to> <biome> [replace <filter>]");
}
