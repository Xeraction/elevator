package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.StringArgument;
import xeraction.elevator.argument.TargetSelectorArgument;
import xeraction.elevator.util.LegacyData;

public class EffectCommand implements Command {
    private boolean give = true; //legacy command doesn't include give literal
    private String target;
    private String effect;
    private String duration;
    private String amplifier;
    private String hide;

    public String build() {
        StringBuilder b = new StringBuilder("effect ");
        b.append(give ? "give" : "clear");
        if (target != null)
            b.append(" ").append(target);
        if (effect != null)
            b.append(" ").append(effect);
        if (duration != null)
            b.append(" ").append(duration);
        if (amplifier != null)
            b.append(" ").append(amplifier);
        if (hide != null)
            b.append(" ").append(hide);
        return b.toString();
    }

    public static final ParseSequence<EffectCommand> SEQUENCE = new ParseSequence<>(EffectCommand::new)
            .node("target", new TargetSelectorArgument(), (arg, cmd) -> cmd.target = arg.build())
            .node("effect", new StringArgument(), (arg, cmd) -> cmd.effect = LegacyData.renameEffectId(arg.value()))
            .node("duration", new StringArgument(), (arg, cmd) -> cmd.duration = arg.value())
            .node("amplifier", new StringArgument(), (arg, cmd) -> cmd.amplifier = arg.value())
            .node("hide", new StringArgument(), (arg, cmd) -> cmd.hide = arg.value())
            .lit("* clear", cmd -> cmd.give = false).lit("* give", cmd -> cmd.give = true)
            .sub("eff", "<effect> [<duration>] [<amplifier>] [<hide>]")
            .rule("effect (clear [<target>] [<effect>]|give <target> /eff/|<target> (clear|/eff/))");
}
