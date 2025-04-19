package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.StringArgument;
import xeraction.elevator.argument.TargetSelectorArgument;
import xeraction.elevator.argument.TextComponentArgument;

public class TeamCommand implements Command {
    private Mode mode;
    private String team;
    private String target;
    private String name;
    private Option option;
    private String value;

    public String build() {
        String s = "team " + mode.name;
        switch (mode) {
            case List -> {
                if (team != null)
                    s += " " + team;
            }
            case Add -> {
                s += " " + team;
                if (name != null)
                    s += " " + name;
            }
            case Remove, Empty -> s += " " + team;
            case Join -> {
                s += " " + team;
                if (target != null)
                    s += " " + target;
            }
            case Leave -> s += " " + target;
            case Modify -> s += " " + team + " " + option.name + " " + value;
        }
        return s;
    }

    public static final ParseSequence<TeamCommand> SEQUENCE = new ParseSequence<>(TeamCommand::new)
            .node("team", new StringArgument(), (arg, cmd) -> cmd.team = arg.value())
            .node("name", new TextComponentArgument(), (arg, cmd) -> cmd.name = arg.tag().build())
            .node("target", new TargetSelectorArgument(), (arg, cmd) -> cmd.target = arg.build())
            .node("text", new TextComponentArgument(), (arg, cmd) -> cmd.value = arg.tag().build())
            .node("value", new StringArgument(), (arg, cmd) -> cmd.value = arg.value())
            .lit("* list", cmd -> cmd.mode = Mode.List).lit("* add", cmd -> cmd.mode = Mode.Add)
            .lit("* remove", cmd -> cmd.mode = Mode.Remove).lit("* empty", cmd -> cmd.mode = Mode.Empty)
            .lit("* join", cmd -> cmd.mode = Mode.Join).lit("* leave", cmd -> cmd.mode = Mode.Leave)
            .lit("* modify", cmd -> cmd.mode = Mode.Modify).lit("* displayName", cmd -> cmd.option = Option.DisplayName)
            .lit("* color", cmd -> cmd.option = Option.Color).lit("* friendlyFire", cmd -> cmd.option = Option.FriendlyFire)
            .lit("* seeFriendlyInvisibles", cmd -> cmd.option = Option.Invisible).lit("* nametagVisibility", cmd -> cmd.option = Option.Nametag)
            .lit("* deathMessageVisibility", cmd -> cmd.option = Option.DeathMessage).lit("* collisionRule", cmd -> cmd.option = Option.Collision)
            .lit("* prefix", cmd -> cmd.option = Option.Prefix).lit("* suffix", cmd -> cmd.option = Option.Suffix)
            .rule("team (list [<team>]|add <team> [<name>]|remove <team>|empty <team>|join <team> [<target>]|leave <target>|modify <team> ((displayName|prefix|suffix) <text>|(color|friendlyFire|seeFriendlyInvisibles|nametagVisibility|deathMessageVisibility|collisionRule) <value>))");

    private enum Mode {
        List("list"), Add("add"), Remove("remove"), Empty("empty"), Join("join"), Leave("leave"), Modify("modify");

        public final String name;

        Mode(String name) {
            this.name = name;
        }
    }

    private enum Option {
        DisplayName("displayName"),
        Color("color"),
        FriendlyFire("friendlyFire"),
        Invisible("seeFriendlyInvisibles"),
        Nametag("nametagVisibility"),
        DeathMessage("deathMessageVisibility"),
        Collision("collisionRule"),
        Prefix("prefix"),
        Suffix("suffix");

        public final String name;

        Option(String name) {
            this.name = name;
        }
    }
}
