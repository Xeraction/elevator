package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.StringArgument;
import xeraction.elevator.argument.TargetSelectorArgument;
import xeraction.elevator.argument.TextComponentArgument;

public class ScoreboardCommand implements Command {
    private Mode mode;
    private String objective;
    private String criteria;
    private String displayName;
    private String slot;
    private DisplayMode displayMode;
    private String displayValue;
    private String target;
    private String score;
    private String operation;
    private String srcTarget;
    private String srcObj;
    private String legacyRest;

    public String build() {
        if (mode == Mode.Tag || mode == Mode.Teams)
            return mode.name + legacyRest;

        StringBuilder b = new StringBuilder("scoreboard ").append(mode.name);
        switch (mode) {
            case ObAdd -> {
                b.append(" ").append(objective).append(" ").append(criteria);
                if (displayName != null)
                    b.append(" ").append(displayName);
            }
            case ObRemove -> b.append(" ").append(objective);
            case ObSetdisplay -> {
                b.append(" ").append(slot);
                if (objective != null)
                    b.append(" ").append(objective);
            }
            case ObModify -> {
                b.append(" ").append(objective).append(" ").append(displayMode.name);
                switch (displayMode) {
                    case AutoUpdate, NFFixed, NFStyled -> b.append(" ").append(displayValue);
                    case Name -> b.append(" ").append(displayName);
                }
            }
            case PlList -> {
                if (target != null)
                    b.append(" ").append(target);
            }
            case PlGet, PlEnable -> b.append(" ").append(target).append(" ").append(objective);
            case PlSet, PlAdd, PlRemove -> b.append(" ").append(target).append(" ").append(objective).append(" ").append(score);
            case PlReset -> {
                b.append(" ").append(target);
                if (objective != null)
                    b.append(" ").append(objective);
            }
            case PlOperation -> b.append(" ").append(target).append(" ").append(objective).append(" ").append(operation).append(" ").append(srcTarget).append(" ").append(srcObj);
            case PlDisplay -> {
                if (displayMode == null) {
                    b.append(" name ").append(target).append(" ").append(objective);
                    if (displayName != null)
                        b.append(" ").append(displayName);
                } else {
                    b.append(" numberformat ").append(target).append(" ").append(objective);
                    switch (displayMode) {
                        case NFBlank -> b.append(" blank");
                        case NFFixed -> b.append(" fixed ").append(displayName);
                        case NFStyled -> b.append(" styled ").append(displayName);
                    }
                }
            }
        }
        return b.toString();
    }

    public static final ParseSequence<ScoreboardCommand> SEQUENCE = new ParseSequence<>(ScoreboardCommand::new)
            .node("obj", new StringArgument(), (arg, cmd) -> cmd.objective = arg.value())
            .node("crit", new StringArgument(), (arg, cmd) -> cmd.criteria = arg.value())
            .node("name", new TextComponentArgument(), (arg, cmd) -> cmd.displayName = arg.tag().build())
            .node("slot", new StringArgument(), (arg, cmd) -> {
                cmd.slot = arg.value();
                if (cmd.slot.equals("belowName"))
                    cmd.slot = "below_name";
            })
            .node("val", new StringArgument(), (arg, cmd) -> cmd.displayValue = arg.value())
            .node("comp", new TextComponentArgument(), (arg, cmd) -> cmd.displayValue = arg.tag().build())
            .node("target", new TargetSelectorArgument(), (arg, cmd) -> cmd.target = arg.build())
            .node("score", new StringArgument(), (arg, cmd) -> cmd.score = arg.value())
            .node("op", new StringArgument(), (arg, cmd) -> cmd.operation = arg.value())
            .node("srcTrg", new TargetSelectorArgument(), (arg, cmd) -> cmd.srcTarget = arg.build())
            .node("srcObj", new StringArgument(), (arg, cmd) -> cmd.srcObj = arg.value())
            .node("rest", new StringArgument(true), (arg, cmd) -> cmd.legacyRest = arg.value())
            .lit("scoreboard objectives list", cmd -> cmd.mode = Mode.ObList).lit("scoreboard players list", cmd -> cmd.mode = Mode.PlList)
            .lit("scoreboard objectives add", cmd -> cmd.mode = Mode.ObAdd).lit("scoreboard players add", cmd -> cmd.mode = Mode.PlAdd)
            .lit("scoreboard objectives remove", cmd -> cmd.mode = Mode.ObRemove).lit("scoreboard players remove", cmd -> cmd.mode = Mode.PlRemove)
            .lit("* setdisplay", cmd -> cmd.mode = Mode.ObSetdisplay).lit("* modify", cmd -> cmd.mode = Mode.ObModify)
            .lit("* get", cmd -> cmd.mode = Mode.PlGet).lit("* set", cmd -> cmd.mode = Mode.PlSet).lit("* reset", cmd -> cmd.mode = Mode.PlReset)
            .lit("* enable", cmd -> cmd.mode = Mode.PlEnable).lit("* operation", cmd -> cmd.mode = Mode.PlOperation).lit("* display", cmd -> cmd.mode = Mode.PlDisplay)
            .lit("* displayautoupdate", cmd -> cmd.displayMode = DisplayMode.AutoUpdate).lit("* displayname", cmd -> cmd.displayMode = DisplayMode.Name)
            .lit("* numberformat", cmd -> cmd.displayMode = DisplayMode.NFReset).lit("* blank", cmd -> cmd.displayMode = DisplayMode.NFBlank)
            .lit("* fixed", cmd -> cmd.displayMode = DisplayMode.NFFixed).lit("* styled", cmd -> cmd.displayMode = DisplayMode.NFStyled)
            .lit("* hearts", cmd -> cmd.displayMode = DisplayMode.RTHearts).lit("* integer", cmd -> cmd.displayMode = DisplayMode.RTInteger)
            .lit("* tag", cmd -> cmd.mode = Mode.Tag).lit("* teams", cmd -> cmd.mode = Mode.Teams)
            .sub("obj", "(list|add <obj> <crit> [<name>]|remove <obj>|setdisplay <slot> [<obj>]|modify <obj> (displayautoupdate <val>|displayname <name>|numberformat blank|numberformat fixed <comp>|numberformat styled <comp>|numberformat|rendertype (hearts|integer)))")
            .sub("pla", "(list [<target>]|(get|enable) <target> <obj>|(set|add|remove) <target> <obj> <score>|reset <target> [<obj>]|operation <target> <obj> <op> <srcTrg> <srcObj>|display (name <target> <obj> [<name>]|numberformat <target> <obj> [(blank|fixed <name>|styled <name>)])|tag <rest>)")
            .rule("scoreboard (objectives /obj/|players /pla/|teams <rest>)");

    private enum Mode {
        ObList("objectives list"),
        ObAdd("objectives add"),
        ObRemove("objectives remove"),
        ObSetdisplay("objectives setdisplay"),
        ObModify("objectives modify"),
        PlList("players list"),
        PlGet("players get"),
        PlSet("players set"),
        PlAdd("players add"),
        PlRemove("players remove"),
        PlReset("players reset"),
        PlEnable("players enable"),
        PlOperation("players operation"),
        PlDisplay("players display"),
        Tag("tag "),
        Teams("team ");

        public final String name;

        Mode(String name) {
            this.name = name;
        }
    }

    private enum DisplayMode {
        AutoUpdate("displayautoupdate"),
        Name("displayname"),
        NFReset("numberformat"),
        NFBlank("numberformat blank"),
        NFFixed("numberformat fixed"),
        NFStyled("numberformat styled"),
        RTHearts("rendertype hearts"),
        RTInteger("rendertype integer");

        public final String name;

        DisplayMode(String name) {
            this.name = name;
        }
    }
}
