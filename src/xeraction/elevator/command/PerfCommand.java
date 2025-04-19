package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;

public class PerfCommand implements Command {
    private boolean start;

    public String build() {
        return "perf " + (start ? "start" : "stop");
    }

    public static final ParseSequence<PerfCommand> SEQUENCE = new ParseSequence<>(PerfCommand::new)
            .lit("* start", cmd -> cmd.start = true).lit("* stop", cmd -> cmd.start = false)
            .rule("perf (start|stop)");
}
