package xeraction.elevator.command.execute;

import xeraction.elevator.Elevator;
import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.StringArgument;
import xeraction.elevator.command.Command;
import xeraction.elevator.command.TeleportCommand;

public class RunSub implements Subcommand {
    private Command command;

    public RunSub() {}

    public RunSub(String command) {
        this.command = Elevator.parseCommand(command);
    }

    public String build() {
        return "run " + command.build();
    }

    public boolean requiresNext() {
        return false;
    }

    public boolean isFinal() {
        return true;
    }

    public static final ParseSequence<RunSub> SEQUENCE = new ParseSequence<>(RunSub::new)
            .node("cmd", new StringArgument(true), (arg, cmd) -> {
                cmd.command = Elevator.parseCommand(arg.value());
                if (cmd.command instanceof TeleportCommand tp)
                    tp.inExecute = true;
            })
            .rule("run <cmd>");
}
