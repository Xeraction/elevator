package xeraction.elevator.command.execute;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.Vec3Argument;
import xeraction.elevator.argument.StringArgument;

public class BiomeSub implements Subcommand {
    private String pos;
    private String biome;

    public String build() {
        return "biome " + pos + " " + biome;
    }

    public boolean requiresNext() {
        return false;
    }

    public static final ParseSequence<BiomeSub> SEQUENCE = new ParseSequence<>(BiomeSub::new)
            .node("pos", new Vec3Argument(), (arg, cmd) -> cmd.pos = arg.vec())
            .node("biome", new StringArgument(), (arg, cmd) -> cmd.biome = arg.value())
            .rule("biome <pos> <biome>");
}
