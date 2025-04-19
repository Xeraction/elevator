package xeraction.elevator.command.execute;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.StringIterator;
import xeraction.elevator.argument.Argument;

import java.util.HashMap;
import java.util.Map;

public class IfUnlessSub implements Subcommand {
    private boolean unless;
    private Subcommand sub;

    public String build() {
        return (unless ? "unless " : "if ") + sub.build();
    }

    public boolean requiresNext() {
        return sub.requiresNext();
    }

    public static final ParseSequence<IfUnlessSub> SEQUENCE = new ParseSequence<>(IfUnlessSub::new)
            .node("cmd", new SubCommandArgument(), (arg, cmd) -> cmd.sub = arg.cmd())
            .lit("if", cmd -> cmd.unless = false)
            .lit("unless", cmd -> cmd.unless = true)
            .rule("(if|unless) <cmd>");

    private static class SubCommandArgument implements Argument {
        private Subcommand cmd;

        public boolean parse(StringIterator iterator) {
            String command = iterator.peekWord();
            cmd = (Subcommand)subCommands.get(command).parse(iterator);
            return cmd != null;
        }

        public Subcommand cmd() {
            return cmd;
        }
    }

    private static final Map<String, ParseSequence<? extends Subcommand>> subCommands = new HashMap<>();

    static {
        subCommands.put("biome", BiomeSub.SEQUENCE);
        subCommands.put("block", BlockSub.SEQUENCE);
        subCommands.put("blocks", BlocksSub.SEQUENCE);
        subCommands.put("data", DataSub.SEQUENCE);
        subCommands.put("dimension", ValueSub.DimensionSub.SEQUENCE);
        subCommands.put("entity", EntitySub.SEQUENCE);
        subCommands.put("function", ValueSub.FunctionSub.SEQUENCE);
        subCommands.put("items", ItemsSub.SEQUENCE);
        subCommands.put("loaded", LoadedSub.SEQUENCE);
        subCommands.put("predicate", ValueSub.PredicateSub.SEQUENCE);
        subCommands.put("score", ScoreSub.SEQUENCE);
    }
}
