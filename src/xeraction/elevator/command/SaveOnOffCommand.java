package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;

public class SaveOnOffCommand implements Command {
    private boolean on;

    public String build() {
        return on ? "save-on" : "save-off";
    }

    public static final ParseSequence<SaveOnOffCommand> SEQUENCE = new ParseSequence<>(SaveOnOffCommand::new)
            .lit("save-on", cmd -> cmd.on = true).lit("save-off", cmd -> cmd.on = false)
            .rule("(save-on|save-off)");
}
