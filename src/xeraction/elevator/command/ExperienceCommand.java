package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.StringArgument;
import xeraction.elevator.argument.TargetSelectorArgument;

public class ExperienceCommand implements Command {
    private Mode mode;
    private String target;
    private String amount;
    private String levels;

    public String build() {
        return "xp " + mode.name + " " + target + (amount != null ? " " + amount : "") + (levels != null ? " " + levels : "");
    }

    public static final ParseSequence<ExperienceCommand> SEQUENCE = new ParseSequence<>(ExperienceCommand::new)
            .node("target", new TargetSelectorArgument(), (arg, cmd) -> cmd.target = arg.build())
            .node("amount", new StringArgument(), (arg, cmd) -> cmd.amount = arg.value())
            .node("old", new StringArgument(), (arg, cmd) -> {
                String val = arg.value();
                if (val.endsWith("L")) {
                    cmd.levels = "levels";
                    val = val.substring(0, val.length() - 1);
                } else
                    cmd.levels = "points";
                cmd.amount = val;
                cmd.mode = Mode.Add;
                cmd.target = "@s"; //if optional target arg is not present, default to the executor
            })
            .lit("* query", cmd -> cmd.mode = Mode.Query).lit("* add", cmd -> cmd.mode = Mode.Add)
            .lit("* set", cmd -> cmd.mode = Mode.Set).lit("* levels", cmd -> cmd.levels = "levels")
            .lit("* points", cmd -> cmd.levels = "points")
            .rule("(experience|xp) (query <target> (levels|points)|(add|set) <target> <amount> [(levels|points)]|<old> [<target>])");

    private enum Mode {
        Add("add"), Set("set"), Query("query");

        public final String name;

        Mode(String name) {
            this.name = name;
        }
    }
}
