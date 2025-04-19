package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.StringArgument;
import xeraction.elevator.argument.TargetSelectorArgument;
import xeraction.elevator.argument.TextComponentArgument;

public class TitleCommand implements Command {
    private String target;
    private Mode mode;
    private String title;
    private String fadeIn;
    private String stay;
    private String fadeOut;

    public String build() {
        String s = "title " + target + " " + mode.name;
        if (mode == Mode.Times)
            return s + " " + fadeIn + " " + stay + " " + fadeOut;
        if (title != null)
            return s + " " + title;
        return s;
    }

    public static final ParseSequence<TitleCommand> SEQUENCE = new ParseSequence<>(TitleCommand::new)
            .node("target", new TargetSelectorArgument(), (arg, cmd) -> cmd.target = arg.build())
            .node("title", new TextComponentArgument(), (arg, cmd) -> cmd.title = arg.tag().build())
            .node("fadeIn", new StringArgument(), (arg, cmd) -> cmd.fadeIn = arg.value())
            .node("stay", new StringArgument(), (arg, cmd) -> cmd.stay = arg.value())
            .node("fadeOut", new StringArgument(), (arg, cmd) -> cmd.fadeOut = arg.value())
            .lit((cmd, lit) -> cmd.mode = Mode.parse(lit))
            .rule("title <target> (clear|reset|(title|subtitle|actionbar) <title>|times <fadeIn> <stay> <fadeOut>)");

    private enum Mode {
        Clear("clear"),
        Reset("reset"),
        Title("title"),
        Subtitle("subtitle"),
        Actionbar("actionbar"),
        Times("times");

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
