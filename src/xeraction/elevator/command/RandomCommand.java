package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.StringIterator;
import xeraction.elevator.argument.StringArgument;
import xeraction.elevator.util.NumberRange;

public class RandomCommand implements Command {
    private Mode mode;
    private String range;
    private String sequence;
    private String seed;
    private String worldSeed;
    private String seqId;

    public String build() {
        StringBuilder b = new StringBuilder("reset ").append(mode.name).append(" ");
        if (mode == Mode.Reset) {
            b.append(sequence);
            if (seed != null)
                b.append(" ").append(seed);
            if (worldSeed != null)
                b.append(" ").append(worldSeed);
            if (seqId != null)
                b.append(" ").append(seqId);
        } else {
            b.append(range);
            if (sequence != null)
                b.append(" ").append(sequence);
        }
        return b.toString();
    }

    public static final ParseSequence<RandomCommand> SEQUENCE = new ParseSequence<>(RandomCommand::new)
            .node("seq", new StringArgument(), (arg, cmd) -> cmd.sequence = arg.value())
            .node("seed", new StringArgument(), (arg, cmd) -> cmd.seed = arg.value())
            .node("worldSeed", new StringArgument(), (arg, cmd) -> cmd.worldSeed = arg.value())
            .node("seqId", new StringArgument(), (arg, cmd) -> cmd.seqId = arg.value())
            .node("range", new StringArgument(), (arg, cmd) -> cmd.range = NumberRange.parse(new StringIterator(arg.value())).build())
            .lit((cmd, lit) -> cmd.mode = Mode.parse(lit))
            .rule("random (reset <seq> [<seed>] [<worldSeed>] [<seqId>]|(value|roll) <range> [<seq>])");

    private enum Mode {
        Value("value"), Roll("roll"), Reset("reset");

        public final String name;

        Mode(String name) {
            this.name = name;
        }

        public static Mode parse(String mode) {
            for (Mode m : values())
                if (m.name.equals(mode))
                    return m;
            return null;
        }
    }
}
