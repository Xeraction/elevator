package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.StringArgument;
import xeraction.elevator.argument.TargetSelectorArgument;

public class RecipeCommand implements Command {
    private boolean give;
    private String target;
    private String recipe;

    public String build() {
        return "recipe " + (give ? "give " : "take ") + target + " " + recipe;
    }

    public static final ParseSequence<RecipeCommand> SEQUENCE = new ParseSequence<>(RecipeCommand::new)
            .node("target", new TargetSelectorArgument(), (arg, cmd) -> cmd.target = arg.build())
            .node("recipe", new StringArgument(), (arg, cmd) -> cmd.recipe = arg.value())
            .lit("* give", cmd -> cmd.give = true).lit("* take", cmd -> cmd.give = false)
            .rule("recipe (give|take) <target> <recipe>");
}
