package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.StringArgument;

public class TestCommand implements Command {
    private Mode mode;
    private String test;
    private String opt1, opt2, opt3, opt4;

    public String build() {
        StringBuilder b = new StringBuilder("test ").append(mode.name);
        if (test != null)
            b.append(" ").append(test);
        if (opt1 != null)
            b.append(" ").append(opt1);
        if (opt2 != null)
            b.append(" ").append(opt2);
        if (opt3 != null)
            b.append(" ").append(opt3);
        if (opt4 != null)
            b.append(" ").append(opt4);
        return b.toString();
    }

    public static final ParseSequence<TestCommand> SEQUENCE = new ParseSequence<>(TestCommand::new)
            .node("test", new StringArgument(), (arg, cmd) -> cmd.test = arg.value())
            .node("opt1", new StringArgument(), (arg, cmd) -> cmd.opt1 = arg.value())
            .node("opt2", new StringArgument(), (arg, cmd) -> cmd.opt2 = arg.value())
            .node("opt3", new StringArgument(), (arg, cmd) -> cmd.opt3 = arg.value())
            .node("opt4", new StringArgument(), (arg, cmd) -> cmd.opt4 = arg.value())
            .lit((cmd, lit) -> cmd.mode = Mode.parse(lit))
            .rule("test (clearall [<opt1>]|clearthat|clearthese|create <test> [<opt1>] [<opt2> <opt3>]|locate <test>|pos <opt1>|resetclosest|resetthat|resetthese|run <test> [<opt1>] [<opt2>] [<opt3>] [<opt4>]|(runclosest|runthat|runthese) [<opt1>] [<opt2>]|runmultiple <test> [<opt1>]|runfailed [<opt1>] [<opt2>] [<opt3>] [<opt4>]|stop|verify <test>|export <test>|exportclosest|exportthat|exportthese)");

    private enum Mode {
        ClearAll("clearall"),
        ClearThat("clearthat"),
        ClearThese("clearthese"), // i know what you're thinking
        Create("create"),
        Locate("locate"),
        Pos("pos"),
        ResetClosest("resetclosest"),
        ResetThat("resetthat"),
        ResetThese("resetthese"),
        Run("run"),
        RunClosest("runclosest"),
        RunThat("runthat"),
        RunThese("runthese"),
        RunMultiple("runmultiple"),
        RunFailed("runfailed"),
        Stop("stop"),
        Verify("verify"),
        Export("export"),
        ExportClosest("exportclosest"),
        ExportThat("exportthat"),
        ExportThese("exportthese");

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
