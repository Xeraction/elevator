package xeraction.elevator.command;

import xeraction.elevator.Elevator;
import xeraction.elevator.ParseSequence;
import xeraction.elevator.StringIterator;
import xeraction.elevator.argument.Argument;
import xeraction.elevator.argument.Vec3Argument;
import xeraction.elevator.argument.TargetSelectorArgument;
import xeraction.elevator.command.execute.*;
import xeraction.elevator.util.LegacyData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExecuteCommand implements Command {
    private List<Subcommand> commands = new ArrayList<>();

    public String build() {
        StringBuilder b = new StringBuilder("execute");
        for (Subcommand cmd : commands)
            b.append(" ").append(cmd.build());
        return b.toString();
    }

    //the execute command is too complicated for our simple-minded system, so we cheat a little by nesting command parsers
    public static final ParseSequence<ExecuteCommand> SEQUENCE = new ParseSequence<>(ExecuteCommand::new)
            .node("cmd", new SubCommandsArgument(), (arg, cmd) -> cmd.commands = arg.commands)
            .rule("execute <cmd>");

    private static class SubCommandsArgument implements Argument {
        private List<Subcommand> commands;

        public boolean parse(StringIterator iterator) {
            commands = new ArrayList<>();
            Subcommand current;
            while (iterator.hasMore()) {
                String cmd = iterator.peekWord();
                if (!subCommands.containsKey(cmd) && commands.isEmpty()) {
                    //legacy execute command
                    TargetSelectorArgument targArg = new TargetSelectorArgument();
                    targArg.parse(iterator);
                    commands.add(new AsAtSub(true, targArg.build()));
                    Vec3Argument posArg = new Vec3Argument();
                    posArg.parse(iterator);
                    commands.add(new PositionedSub(posArg.vec()));
                    if (iterator.peekWord().equals("detect")) {
                        iterator.readWord();
                        Vec3Argument detectPos = new Vec3Argument();
                        detectPos.parse(iterator);
                        String block = LegacyData.flattenBlock(iterator.readWord(), iterator.readWord(), null);
                        commands.add((Subcommand)IfUnlessSub.SEQUENCE.parse(new StringIterator("if block " + detectPos.vec() + " " + block)));
                    }
                    iterator.skipSpaces();
                    commands.add(new RunSub(iterator.getRemaining()));
                    break;
                }
                current = (Subcommand)subCommands.get(cmd).parse(iterator);
                if (current == null)
                    return false;
                commands.add(current);
                if (current.isFinal())
                    break;
                if (current.requiresNext() && !iterator.hasMore())
                    return false;
            }
            return true;
        }
    }

    private static final Map<String, ParseSequence<? extends Subcommand>> subCommands = new HashMap<>();

    static {
        subCommands.put("align", AlignSub.SEQUENCE);
        subCommands.put("anchored", AnchoredSub.SEQUENCE);
        subCommands.put("as", AsAtSub.SEQUENCE);
        subCommands.put("at", AsAtSub.SEQUENCE);
        subCommands.put("facing", FacingSub.SEQUENCE);
        subCommands.put("in", InOnSub.SEQUENCE);
        subCommands.put("on", InOnSub.SEQUENCE);
        subCommands.put("positioned", PositionedSub.SEQUENCE);
        subCommands.put("rotated", RotatedSub.SEQUENCE);
        subCommands.put("store", StoreSub.SEQUENCE);
        subCommands.put("summon", SummonSub.SEQUENCE);
        subCommands.put("if", IfUnlessSub.SEQUENCE);
        subCommands.put("unless", IfUnlessSub.SEQUENCE);
        subCommands.put("run", RunSub.SEQUENCE);
    }
}
