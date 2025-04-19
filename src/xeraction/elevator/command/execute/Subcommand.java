package xeraction.elevator.command.execute;

import xeraction.elevator.command.Command;

public interface Subcommand extends Command {
    boolean requiresNext();

    default boolean isFinal() {
        return false;
    }
}
