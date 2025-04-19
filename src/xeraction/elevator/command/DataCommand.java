package xeraction.elevator.command;

import xeraction.elevator.Elevator;
import xeraction.elevator.ParseSequence;
import xeraction.elevator.StringIterator;
import xeraction.elevator.argument.*;
import xeraction.elevator.util.NBTPath;
import xeraction.elevator.util.SNBT;

public class DataCommand implements Command {
    private String trgPos;
    private String trgEntity;
    private String trgStorage;
    private NBTPath path;
    private String scale;
    private SNBT.Tag nbt;
    private String index;
    private String srcPos;
    private String srcEntity;
    private String srcStorage;
    private NBTPath srcPath;
    private String start;
    private String end;
    private Mode mode;
    private Setting setting;
    private ModSet modSet;

    public String build() {
        StringBuilder b = new StringBuilder("data ").append(mode.name).append(" ");
        if (trgPos != null)
            b.append("block ").append(trgPos);
        else if (trgEntity != null)
            b.append("entity ").append(trgEntity);
        else
            b.append("storage ").append(trgStorage);
        b.append(" ");
        switch (mode) {
            case Get -> {
                if (path != null)
                    b.append(path.build());
                if (scale != null)
                    b.append(scale);
            }
            case Merge -> b.append(nbt.build());
            case Remove -> b.append(path.build());
            case Modify -> {
                b.append(path.build()).append(" ");
                b.append(setting.name).append(" ");
                if (setting == Setting.Insert)
                    b.append(index).append(" ");
                b.append(modSet.name).append(" ");
                switch (modSet) {
                    case From, String -> {
                        if (srcPos != null)
                            b.append("block ").append(srcPos);
                        else if (srcEntity != null)
                            b.append("entity ").append(srcEntity);
                        else
                            b.append("storage ").append(srcStorage);
                        if (srcPath != null)
                            b.append(" ").append(srcPath.build());
                        if (start != null)
                            b.append(" ").append(start);
                        if (end != null)
                            b.append(" ").append(end);
                    }
                    case Value -> b.append(nbt.build());
                }
            }
        }
        return b.toString();
    }

    public static final ParseSequence<DataCommand> SEQUENCE = new ParseSequence<>(DataCommand::new)
            .node("trgPos", new Vec3Argument(), (arg, cmd) -> cmd.trgPos = arg.vec())
            .node("trgEntity", new TargetSelectorArgument(), (arg, cmd) -> cmd.trgEntity = arg.build())
            .node("trgStorage", new StringArgument(), (arg, cmd) -> cmd.trgStorage = arg.value())
            .node("srcPos", new Vec3Argument(), (arg, cmd) -> cmd.srcPos = arg.vec())
            .node("srcEntity", new TargetSelectorArgument(), (arg, cmd) -> cmd.srcEntity = arg.build())
            .node("srcStorage", new StringArgument(), (arg, cmd) -> cmd.srcStorage = arg.value())
            .node("path", new NBTPathArgument(), (arg, cmd) -> {cmd.path = arg.path(); warn(arg.path().build());})
            .node("scale", new StringArgument(), (arg, cmd) -> cmd.scale = arg.value())
            .node("nbt", new NBTArgument(), (arg, cmd) -> {cmd.nbt = arg.tag(); warn(arg.tag().build());})
            .node("index", new StringArgument(), (arg, cmd) -> cmd.index = arg.value())
            .node("srcPath", new NBTPathArgument(), (arg, cmd) -> {cmd.srcPath = arg.path(); warn(arg.path().build());})
            .node("start", new StringArgument(), (arg, cmd) -> cmd.start = arg.value())
            .node("end", new StringArgument(), (arg, cmd) -> cmd.end = arg.value())
            .lit("* get", cmd -> cmd.mode = Mode.Get).lit("data merge", cmd -> cmd.mode = Mode.Merge) //explicit literals are searched before wildcards, so no conflicts here
            .lit("* modify", cmd -> cmd.mode = Mode.Modify).lit("* remove", cmd -> cmd.mode = Mode.Remove)
            .lit("* append", cmd -> cmd.setting = Setting.Append).lit("* insert", cmd -> cmd.setting = Setting.Insert)
            .lit("* merge", cmd -> cmd.setting = Setting.Merge).lit("* prepend", cmd -> cmd.setting = Setting.Prepend)
            .lit("* set", cmd -> cmd.setting = Setting.Set).lit("* from", cmd -> cmd.modSet = ModSet.From)
            .lit("* string", cmd -> cmd.modSet = ModSet.String).lit("* value", cmd -> cmd.modSet = ModSet.Value)
            .sub("target", "(block <trgPos>|entity <trgEntity>|storage <trgStorage>)")
            .sub("source", "(block <srcPos>|entity <srcEntity>|storage <srcStorage>)")
            .rule("data (get /target/ [<path>] [<scale>]|merge /target/ <nbt>|remove /target/ <path>|modify /target/ <path> (append|insert <index>|merge|prepend|set) (from /source/ [<srcPath>]|string /source/ [<srcPath>] [<start>] [<end>]|value <nbt>))");

    private static void warn(String nbt) {
        Elevator.warn("NBT in the /data command cannot be checked for correctness due to its ambiguous nature. Please verify the NBT by hand.\n" + nbt);
    }

    private enum Mode {
        Get("get"), Merge("merge"), Modify("modify"), Remove("remove");

        public final String name;

        Mode(String name) {
            this.name = name;
        }
    }

    private enum Setting {
        Append("append"), Insert("insert"), Merge("merge"), Prepend("prepend"), Set("set");

        public final String name;

        Setting(String name) {
            this.name = name;
        }
    }

    private enum ModSet {
        From("from"), String("string"), Value("value");

        public final String name;

        ModSet(String name) {
            this.name = name;
        }
    }

    public static class NBTPathArgument implements Argument {
        private NBTPath path;

        public boolean parse(StringIterator iterator) {
            path = new NBTPath(iterator);
            return true;
        }

        public NBTPath path() {
            return path;
        }
    }
}
