package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.StringArgument;
import xeraction.elevator.argument.StringOrInlineArgument;
import xeraction.elevator.argument.TargetSelectorArgument;
import xeraction.elevator.argument.Vec3Argument;

public class ComputeCommand implements Command {
    private Context context;
    private String contextVal;
    private boolean integer;
    private String provider;
    private String scale;

    public String build() {
        return "compute " + context.name + (contextVal != null ? " " + contextVal : "")
                + (integer ? " integer " : " float ") + provider + (scale != null ? " " + scale : "");
    }

    public static final ParseSequence<ComputeCommand> SEQUENCE = new ParseSequence<>(ComputeCommand::new)
            .node("pos", new Vec3Argument(), (arg, cmd) -> cmd.contextVal = arg.vec())
            .node("target", new TargetSelectorArgument(), (arg, cmd) -> cmd.contextVal = arg.build())
            .node("prov", new StringOrInlineArgument(), (arg, cmd) -> cmd.provider = arg.build())
            .node("scale", new StringArgument(), (arg, cmd) -> cmd.scale = arg.value())
            .lit("integer", cmd -> cmd.integer = true).lit("float", cmd -> cmd.integer = false)
            .lit((cmd, lit) -> cmd.context = Context.parse(lit))
            .rule("compute (default|block <pos>|entity <target>) (integer <prov>|float <prov> [<scale>])");

    public enum Context {
        Default("default"), Block("block"), Entity("entity");

        public final String name;

        Context(String name) {
            this.name = name;
        }

        public static Context parse(String context) {
            for (Context c : values())
                if (c.name.equals(context))
                    return c;
            return null;
        }
    }
}
