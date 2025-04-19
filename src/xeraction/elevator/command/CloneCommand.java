package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.StringIterator;
import xeraction.elevator.argument.*;
import xeraction.elevator.util.BlockPredicate;
import xeraction.elevator.util.LegacyData;

public class CloneCommand implements Command {
    private String srcDim;
    private String trgDim;
    private String begin;
    private String end;
    private String dst;
    private boolean strict = false;
    private Mode mode;
    private String filter;
    private Setting setting;

    public String build() {
        StringBuilder builder = new StringBuilder("clone ");
        if (srcDim != null)
            builder.append("from ").append(srcDim).append(" ");
        builder.append(begin).append(" ");
        builder.append(end).append(" ");
        if (trgDim != null)
            builder.append("to ").append(trgDim).append(" ");
        builder.append(dst);
        if (strict)
            builder.append(" strict");
        if (mode != null)
            builder.append(" ").append(mode.name);
        if (filter != null)
            builder.append(" ").append(filter);
        if (setting != null)
            builder.append(" ").append(setting.name);
        return builder.toString();
    }

    public static final ParseSequence<CloneCommand> SEQUENCE = new ParseSequence<>(CloneCommand::new)
            .node("srcDim", new StringArgument(), (arg, cmd) -> cmd.srcDim = arg.value())
            .node("begin", new Vec3Argument(), (arg, cmd) -> cmd.begin = arg.vec())
            .node("end", new Vec3Argument(), (arg, cmd) -> cmd.end = arg.vec())
            .node("trgDim", new StringArgument(), (arg, cmd) -> cmd.trgDim = arg.value())
            .node("dst", new Vec3Argument(), (arg, cmd) -> cmd.dst = arg.vec())
            .node("filter", new FilterArgument(), (arg, cmd) -> {
                if (arg.setting != null)
                    cmd.setting = arg.setting;
                cmd.filter = arg.filter;
            })
            .lit("* strict", cmd -> cmd.strict = true).lit("* replace", cmd -> cmd.mode = Mode.Replace)
            .lit("* masked", cmd -> cmd.mode = Mode.Masked).lit("* filtered", cmd -> cmd.mode = Mode.Filtered)
            .lit("* force", cmd -> cmd.setting = Setting.Force).lit("* move", cmd -> cmd.setting = Setting.Move)
            .lit("* normal", cmd -> cmd.setting = Setting.Normal)
            .rule("clone {from <srcDim>} <begin> <end> {to <trgDim>} <dst> {strict} [(replace|masked|filtered <filter>)] [(force|move|normal)]");

    private enum Mode {
        Replace("replace"), Masked("masked"), Filtered("filtered");

        public final String name;

        Mode(String name) {
            this.name = name;
        }
    }

    private enum Setting {
        Force("force"), Move("move"), Normal("normal");

        public final String name;

        Setting(String name) {
            this.name = name;
        }

        public static Setting parse(String name) {
            for (Setting s : values())
                if (s.name.equals(name))
                    return s;
            return null;
        }
    }

    private static class FilterArgument implements Argument {
        private Setting setting;
        private String filter;

        public boolean parse(StringIterator iterator) {
            String word = iterator.peekWord();
            if (word.equals("force") || word.equals("move") || word.equals("normal")) {
                //pre-1.13 block data
                setting = Setting.parse(word);
                iterator.readWord();
                if (iterator.hasMore()) {
                    String block = iterator.readWord();
                    String data = iterator.hasMore() ? iterator.readWord() : "0";
                    filter = LegacyData.flattenBlock(block, data, null);
                } else
                    filter = "minecraft:air"; //block not specified
            } else {
                setting = null;
                filter = new BlockPredicate(iterator).build();
            }
            return true;
        }
    }
}
