package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.StringArgument;
import xeraction.elevator.argument.TargetSelectorArgument;
import xeraction.elevator.argument.TextComponentArgument;
import xeraction.elevator.util.SNBT;

public class BossbarCommand implements Command {
    private Mode mode;
    private String id;
    private SNBT.Tag name;
    private Setting setting;
    private String settingValue;
    private String target;

    public String build() {
        String cmd = "bossbar " + mode.name;
        switch (mode) {
            case Add -> cmd += " " + id + " " + name.build();
            case Get -> cmd += " " + id + " " + setting.name;
            case Remove -> cmd += " " + id;
            case Set -> {
                cmd += " " + id + " " + setting.name + " ";
                if (setting == Setting.Name)
                    cmd += name.build();
                else if (setting == Setting.Players)
                    cmd += target;
                else
                    cmd += settingValue;
            }
        }
        return cmd;
    }

    public static final ParseSequence<BossbarCommand> SEQUENCE = new ParseSequence<>(BossbarCommand::new)
            .node("id", new StringArgument(), (arg, cmd) -> cmd.id = arg.value())
            .node("name", new TextComponentArgument(), (arg, cmd) -> cmd.name = arg.tag())
            .node("stng", new StringArgument(), (arg, cmd) -> cmd.setting = Setting.parse(arg.value()))
            .node("vl", new StringArgument(), (arg, cmd) -> cmd.settingValue = arg.value())
            .node("target", new TargetSelectorArgument(), (arg, cmd) -> cmd.target = arg.build())
            .lit("* add", cmd -> cmd.mode = Mode.Add).lit("* get", cmd -> cmd.mode = Mode.Get)
            .lit("* list", cmd -> cmd.mode = Mode.List).lit("* remove", cmd -> cmd.mode = Mode.Remove)
            .lit("* set", cmd -> cmd.mode = Mode.Set).lit("* color", cmd -> cmd.setting = Setting.Color)
            .lit("* max", cmd -> cmd.setting = Setting.Max).lit("* name", cmd -> cmd.setting = Setting.Name)
            .lit("* players", cmd -> cmd.setting = Setting.Players).lit("* style", cmd -> cmd.setting = Setting.Style)
            .lit("* value", cmd -> cmd.setting = Setting.Value).lit("* visible", cmd -> cmd.setting = Setting.Visible)
            .rule("bossbar (add <id> <name>|get <id> <stng>|list|remove <id>|set <id> (color <vl>|max <vl>|name <name>|players [<target>]|style <vl>|value <vl>|visible <vl>))");

    private enum Mode {
        Add("add"), Get("get"), List("list"), Remove("remove"), Set("set");

        public final String name;

        Mode(String name) {
            this.name = name;
        }
    }

    private enum Setting {
        Color("color"), Max("max"), Name("name"), Players("players"), Style("style"), Value("value"), Visible("visible");

        public final String name;

        Setting(String name) {
            this.name = name;
        }

        public static Setting parse(String s) {
            for (Setting set : values())
                if (set.name.equals(s))
                    return set;
            return null;
        }
    }
}
