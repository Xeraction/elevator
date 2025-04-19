package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.StringArgument;
import xeraction.elevator.util.LegacyData;

public class DifficultyCommand implements Command {
    private String difficulty;

    public String build() {
        return "difficulty" + (difficulty == null ? "" : " " + difficulty);
    }

    public static final ParseSequence<DifficultyCommand> SEQUENCE = new ParseSequence<>(DifficultyCommand::new)
            .node("diff", new StringArgument(), (arg, cmd) -> cmd.difficulty = LegacyData.renameDifficulty(arg.value()))
            .rule("difficulty [<diff>]");
}
