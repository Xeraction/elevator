package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.StringIterator;
import xeraction.elevator.argument.*;
import xeraction.elevator.util.BlockPredicate;
import xeraction.elevator.util.LegacyData;
import xeraction.elevator.util.SNBT;

public class FillCommand implements Command {
    private String from;
    private String to;
    private String block;
    private KR kr;
    private String filter;
    private Mode mode;
    private String legacyData;

    public String build() {
        if (legacyData != null) {
            block = LegacyData.flattenBlock(block, legacyData, null);
            legacyData = null;
        }
        return "fill " + from + " " + to + " " + block + ((kr != null ? " " + kr.name + (filter != null ? " " + filter : "") : "") + (mode != null ? " " + mode.name : ""));
    }

    public static final ParseSequence<FillCommand> SEQUENCE = new ParseSequence<>(FillCommand::new)
            .node("from", new Vec3Argument(), (arg, cmd) -> cmd.from = arg.vec())
            .node("to", new Vec3Argument(), (arg, cmd) -> cmd.to = arg.vec())
            .node("block", new BlockArgument(), (arg, cmd) -> {
                cmd.block = arg.block;
                if (arg.data != null)
                    cmd.legacyData = arg.data;
            })
            .node("filter", new FilterArgument(), (arg, cmd) -> {
                if (arg.predicate != null)
                    cmd.filter = arg.predicate.build();
                else {
                    cmd.block = LegacyData.flattenBlock(cmd.block, cmd.legacyData, arg.tag);
                    cmd.legacyData = null;
                }
            })
            .node("leg", new StringArgument(), (arg, cmd) -> cmd.filter = LegacyData.flattenBlock(cmd.filter, arg.value(), null))
            .node("nbt", new NBTArgument(), (arg, cmd) -> {
                cmd.block = LegacyData.flattenBlock(cmd.block, cmd.legacyData, (SNBT.Compound)arg.tag());
                cmd.legacyData = null;
            })
            .lit("* keep", cmd -> cmd.kr = KR.Keep).lit("* replace", cmd -> cmd.kr = KR.Replace)
            .lit((cmd, lit) -> cmd.mode = Mode.parse(lit))
            .rule("fill <from> <to> <block> [(keep|{replace [<filter>] [<leg>]} [(destroy|hollow|outline|strict)] [<nbt>])]");

    private enum KR {
        Keep("keep"), Replace("replace");

        public final String name;

        KR(String name) {
            this.name = name;
        }
    }

    private enum Mode {
        Destroy("destroy"), Hollow("hollow"), Outline("outline"), Strict("strict");

        public final String name;

        Mode(String name) {
            this.name = name;
        }

        public static Mode parse(String mode) {
            for (Mode m : values())
                if (m.name.equals(mode))
                    return m;
            return null;
        }
    }

    private static class BlockArgument implements Argument {
        private String block;
        private String data;

        public boolean parse(StringIterator iterator) {
            block = new BlockPredicate(iterator).build();
            data = null;
            if (iterator.hasMore()) {
                String next = iterator.peekWord();
                if (next.equals("keep") || next.equals("replace") || next.equals("destroy") || next.equals("hollow") || next.equals("outline") || next.equals("strict"))
                    return true;
                //pre-1.13 block data
                data = iterator.readWord();
            }
            return true;
        }
    }

    private static class FilterArgument implements Argument {
        private BlockPredicate predicate;
        private SNBT.Compound tag;

        public boolean parse(StringIterator iterator) {
            if (iterator.peekSkip() == '{') {
                tag = SNBT.parse(iterator);
                predicate = null;
                return true;
            }
            predicate = new BlockPredicate(iterator);
            tag = null;
            return true;
        }
    }
}
