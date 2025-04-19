package xeraction.elevator.command;

import xeraction.elevator.*;
import xeraction.elevator.argument.*;
import xeraction.elevator.util.LegacyData;

import java.util.UUID;

public class AttributeCommand implements Command {
    private String target;
    private String attribute;
    private Mode mode;
    private String scale;
    private String value;
    private String id;
    private Modifier modifier;

    public String build() {
        String cmd = "attribute " + target + " " + attribute + " " + mode.value;
        switch (mode) {
            case Get, BaseGet -> {
                if (scale != null)
                    cmd += " "+  scale;
            }
            case BaseSet -> cmd += " " + value;
            case ModifierAdd -> cmd += " " + id + " " + value + " " + modifier.value;
            case ModifierRemove -> cmd += " " + id;
            case ModifierGet -> {
                cmd += " " + id;
                if (scale != null)
                    cmd += " " + scale;
            }
        }
        return cmd;
    }

    public static final ParseSequence<AttributeCommand> SEQUENCE = new ParseSequence<>(AttributeCommand::new)
            .node("target", new TargetSelectorArgument(), (arg, cmd) -> cmd.target = arg.build())
            .node("attr", new StringArgument(), (arg, cmd) -> cmd.attribute = LegacyData.renameAttribute(arg.value()))
            .node("scale", new StringArgument(), (arg, cmd) -> cmd.scale = arg.value())
            .node("value", new StringArgument(), (arg, cmd) -> cmd.value = arg.value())
            .node("id", new IdFix(true), (arg, cmd) -> cmd.id = arg.id())
            .node("ido", new IdFix(false), (arg, cmd) -> cmd.id = arg.id())
            .node("mod", new StringArgument(), (arg, cmd) -> cmd.modifier = Modifier.parse(arg.value()))
            .lit("attribute get", cmd -> cmd.mode = Mode.Get)
            .lit("attribute base get", cmd -> cmd.mode = Mode.BaseGet)
            .lit("attribute base set", cmd -> cmd.mode = Mode.BaseSet)
            .lit("attribute base reset", cmd -> cmd.mode = Mode.BaseReset)
            .lit("attribute modifier add", cmd -> cmd.mode = Mode.ModifierAdd)
            .lit("attribute modifier remove", cmd -> cmd.mode = Mode.ModifierRemove)
            .lit("attribute modifier value get", cmd -> cmd.mode = Mode.ModifierGet)
            .rule("attribute <target> <attr> (get [<scale>]|base get [<scale>]|base set <value>|base reset|modifier add <id> <value> <mod>|modifier remove <ido>|modifier value get <ido> [<scale>])");

    private static class IdFix implements Argument {
        private final boolean withName;
        private String id;

        public IdFix(boolean withName) {
            this.withName = withName;
        }

        public boolean parse(StringIterator iterator) {
            String next = iterator.readWord();
            try {
                UUID uuid = UUID.fromString(next);
                if (withName)
                    iterator.readWord();
                //existing modifiers get migrated by using the uuid under the minecraft namespace
                id = "minecraft:" + next;
            } catch (IllegalArgumentException ignored) {
                id = next;
                if (id.startsWith("minecraft:"))
                    Elevator.warn("Beware of problems when overriding vanilla attribute modifiers.");
            }
            return true;
        }

        public String id() {
            return id;
        }
    }

    private enum Mode {
        Get("get"), BaseGet("base get"), BaseSet("base set"), BaseReset("base reset"),
        ModifierAdd("modifier add"), ModifierRemove("modifier remove"), ModifierGet("modifier value get");

        public final String value;

        Mode(String value) {
            this.value = value;
        }
    }

    private enum Modifier {
        AddValue("add_value"), AddMultipliedBase("add_multiplied_base"), AddMultipliedTotal("add_multiplied_total");

        public final String value;

        Modifier(String value) {
            this.value = value;
        }

        public static Modifier parse(String s) {
            return switch (s) {
                case "add", "add_value" -> AddValue;
                case "multiply_base", "add_multiplied_base" -> AddMultipliedBase;
                case "multiply", "add_multiplied_total" -> AddMultipliedTotal;
                default -> null;
            };
        }
    }
}
