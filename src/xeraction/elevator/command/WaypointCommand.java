package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.StringArgument;
import xeraction.elevator.argument.TargetSelectorArgument;

public class WaypointCommand implements Command {
    private String point;
    private Setting setting;
    private String content;

    public String build() {
        if (point == null)
            return "waypoint list";

        return "waypoint modify " + point + " " + setting.name + (content != null ? " " + content : "");
    }

    public static final ParseSequence<WaypointCommand> SEQUENCE = new ParseSequence<>(WaypointCommand::new)
            .node("point", new TargetSelectorArgument(), (arg, cmd) -> cmd.point = arg.build())
            .node("con", new StringArgument(), (arg, cmd) -> cmd.content = arg.value())
            .lit("waypoint modify color", cmd -> cmd.setting = Setting.Color)
            .lit("waypoint modify color hex", cmd -> cmd.setting = Setting.ColorHex)
            .lit("waypoint modify color reset", cmd -> cmd.setting = Setting.ColorReset)
            .lit("waypoint modify style set", cmd -> cmd.setting = Setting.StyleSet)
            .lit("waypoint modify style reset", cmd -> cmd.setting = Setting.StyleReset)
            .rule("waypoint (list|modify <point> (color (hex <con>|reset|<con>)|style (set <con>|reset)))");

    private enum Setting {
        Color("color"), ColorHex("color hex"), ColorReset("color reset"), StyleSet("style set"), StyleReset("style reset");

        private String name;

        Setting(String name) {
            this.name = name;
        }
    }
}
