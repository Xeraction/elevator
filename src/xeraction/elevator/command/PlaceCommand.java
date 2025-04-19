package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.Vec3Argument;
import xeraction.elevator.argument.StringArgument;

public class PlaceCommand implements Command {
    private Mode mode;
    private String name;
    private String pos;
    private String target;
    private String maxDepth;
    private String rot;
    private String mirror;
    private String integrity;
    private String seed;
    private boolean strict = false;

    public String build() {
        String s = "place " + mode.name + " " + name;
        switch (mode) {
            case Feature, Structure, Template -> {
                if (pos != null)
                    s += " " + pos;
                if (mode == Mode.Template) {
                    if (rot != null)
                        s += " " + rot;
                    if (mirror != null)
                        s += " " + mirror;
                    if (integrity != null)
                        s += " " + integrity;
                    if (seed != null)
                        s += " " + seed;
                    if (strict)
                        s += " strict";
                }
            }
            case Jigsaw -> {
                s += " " + target + " " + maxDepth;
                if (pos != null)
                    s += " " + pos;
            }
        }
        return s;
    }

    public static final ParseSequence<PlaceCommand> SEQUENCE = new ParseSequence<>(PlaceCommand::new)
            .node("name", new StringArgument(), (arg, cmd) -> cmd.name = arg.value())
            .node("pos", new Vec3Argument(), (arg, cmd) -> cmd.pos = arg.vec())
            .node("target", new StringArgument(), (arg, cmd) -> cmd.target = arg.value())
            .node("maxDepth", new StringArgument(), (arg, cmd) -> cmd.maxDepth = arg.value())
            .node("rot", new StringArgument(), (arg, cmd) -> cmd.rot = arg.value())
            .node("mirror", new StringArgument(), (arg, cmd) -> cmd.mirror = arg.value())
            .node("int", new StringArgument(), (arg, cmd) -> cmd.integrity = arg.value())
            .node("seed", new StringArgument(), (arg, cmd) -> cmd.seed = arg.value())
            .lit("* strict", cmd -> cmd.strict = true)
            .lit((cmd, lit) -> cmd.mode = Mode.parse(lit))
            .rule("place (feature <name> [<pos>]|jigsaw <name> <target> <maxDepth> [<pos>]|structure <name> [<pos>]|template <name> [<pos>] [<rot>] [<mirror>] [<int>] [<seed>] [strict])");

    private enum Mode {
        Feature("feature"), Jigsaw("jigsaw"), Structure("structure"), Template("template");

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
