package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;

public class JfrCommand implements Command { //no, i didn't know this existed
    private boolean start;

    public String build() {
        return "jfr " + (start ? "start" : "stop");
    }

    public static final ParseSequence<JfrCommand> SEQUENCE = new ParseSequence<>(JfrCommand::new)
            .lit("* start", cmd -> cmd.start = true)
            .lit("* stop", cmd -> cmd.start = false)
            .rule("jfr (start|stop)");
}
