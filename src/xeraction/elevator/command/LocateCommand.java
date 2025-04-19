package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.StringArgument;
import xeraction.elevator.util.LegacyData;

public class LocateCommand implements Command {
    private Mode mode;
    private String locate;

    public String build() {
        return "locate " + mode.name + " " + locate;
    }

    public static final ParseSequence<LocateCommand> SEQUENCE = new ParseSequence<>(LocateCommand::new)
            .node("loc", new StringArgument(), (arg, cmd) -> cmd.locate = cmd.mode == Mode.Structure ? LegacyData.renameStructure(arg.value()) : arg.value())
            .node("old", new StringArgument(), (arg, cmd) -> {
                cmd.locate = LegacyData.renameStructure(arg.value());
                cmd.mode = Mode.Structure;
            })
            .lit((cmd, lit) -> cmd.mode = Mode.parse(lit))
            .rule("locate (structure|biome|poi|<old>) [<loc>]");

    private enum Mode {
        Structure("structure"), Biome("biome"), Poi("poi");

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
