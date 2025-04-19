package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.StringArgument;
import xeraction.elevator.argument.TargetSelectorArgument;
import xeraction.elevator.util.LegacyData;

public class EnchantCommand implements Command {
    private String target;
    private String enchant;
    private String level;

    public String build() {
        return "enchant " + target + " " + enchant + (level == null ? "" : " " + level);
    }

    public static final ParseSequence<EnchantCommand> SEQUENCE = new ParseSequence<>(EnchantCommand::new)
            .node("target", new TargetSelectorArgument(), (arg, cmd) -> cmd.target = arg.build())
            .node("enchant", new StringArgument(), (arg, cmd) -> cmd.enchant = LegacyData.renameEnchantmentId(arg.value()))
            .node("level", new StringArgument(), (arg, cmd) -> cmd.level = arg.value())
            .rule("enchant <target> <enchant> [<level>]");
}
