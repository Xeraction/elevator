package xeraction.elevator.command.execute;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.StringIterator;
import xeraction.elevator.argument.StringArgument;
import xeraction.elevator.argument.TargetSelectorArgument;
import xeraction.elevator.util.NumberRange;

public class ScoreSub implements Subcommand {
    private String trg;
    private String trgObj;
    private Mode mode;
    private String src;
    private String srcObj;
    private NumberRange range;

    public String build() {
        String s = "score " + trg + " " + trgObj + " " + mode.name + " ";
        if (mode == Mode.Matches)
            return s + range.build();
        return s + src + " " + srcObj;
    }

    public boolean requiresNext() {
        return false;
    }

    public static final ParseSequence<ScoreSub> SEQUENCE = new ParseSequence<>(ScoreSub::new)
            .node("trg", new TargetSelectorArgument(), (arg, cmd) -> cmd.trg = arg.build())
            .node("trgObj", new StringArgument(), (arg, cmd) -> cmd.trgObj = arg.value())
            .node("src", new TargetSelectorArgument(), (arg, cmd) -> cmd.src = arg.build())
            .node("srcObj", new StringArgument(), (arg, cmd) -> cmd.srcObj = arg.value())
            .node("range", new StringArgument(), (arg, cmd) -> cmd.range = NumberRange.parse(new StringIterator(arg.value())))
            .node("mode", new StringArgument(), (arg, cmd) -> cmd.mode = Mode.parse(arg.value()))
            .lit("* matches", cmd -> cmd.mode = Mode.Matches)
            .rule("score <trg> <trgObj> (matches <range>|<mode> <src> <srcObj>)");

    private enum Mode {
        Less("<"), LessEqual("<="), Equal("="), GreatEqual(">="), Great(">"), Matches("matches");

        public final String name;

        Mode(String name) {
            this.name = name;
        }

        public static Mode parse(String in) {
            for (Mode m : values())
                if (m.name.equals(in))
                    return m;
            return null;
        }
    }
}
