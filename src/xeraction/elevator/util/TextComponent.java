package xeraction.elevator.util;

import xeraction.elevator.Elevator;
import xeraction.elevator.StringIterator;
import xeraction.elevator.argument.TargetSelectorArgument;
import xeraction.elevator.command.Command;

import java.util.UUID;

public class TextComponent {
    public static void elevateCMD(SNBT.Tag old) {
        if (old instanceof SNBT.Compound c)
            elevateSingle(c);
        else if (old instanceof SNBT.List l)
            for (SNBT.Tag t : l.value)
                elevateCMD(t);
    }

    public static SNBT.Tag elevateNBT(SNBT.Tag old, boolean keepName) {
        SNBT.Tag tag;
        if (old instanceof SNBT.Strings s && shouldUnstring(s.value)) {
            tag = SNBT.parseTag(new StringIterator(s.value), false);
            tag.name = s.name;
        } else
            tag = old;
        if (tag instanceof SNBT.Compound c)
            elevateSingle(c);
        else if (tag instanceof SNBT.List l) {
            for (SNBT.Tag element : l.value)
                elevateNBT(element, false);
        }
        if (!keepName)
            tag.name = null;
        return tag;
    }

    public static void jsonToSNBT(SNBT.Compound tag, String name) {
        if (!tag.contains(name))
            return;
        if (tag.get(name) instanceof SNBT.Strings s && shouldUnstring(s.value)) {
            tag.remove(name);
            tag.add(elevateNBT(SNBT.parseTag(new StringIterator(s.value), false), false).name(name));
        }
    }

    private static boolean shouldUnstring(String s) {
        return s.startsWith("{") || s.startsWith("[") || s.startsWith("\"") || s.startsWith("'");
    }

    private static void elevateSingle(SNBT.Compound tag) {
        if (tag.contains("extra"))
            elevateNBT(tag.get("extra"), true);
        if (tag.contains("with"))
            elevateNBT(tag.get("with"), true);
        if (tag.contains("selector"))
            tag.set("selector", TargetSelectorArgument.elevateTargetSelector(tag.getString("selector")));
        if (tag.contains("separator"))
            elevateNBT(tag.get("separator"), true);
        if (tag.contains("entity"))
            tag.set("entity", TargetSelectorArgument.elevateTargetSelector(tag.getString("entity")));
        if (tag.contains("click_event") || tag.contains("clickEvent")) {
            tag.rename("clickEvent", "click_event");
            SNBT.Compound click = tag.getCompound("click_event");
            switch (click.getString("action")) {
                case "open_url" -> click.rename("value", "url");
                case "open_file" -> click.rename("value", "path");
                case "run_command", "suggest_command" -> {
                    click.rename("value", "command");
                    Command command = Elevator.parseCommand(click.getString("command"));
                    if (command != null)
                        click.set("command", command.build());
                }
                case "change_page" -> {
                    if (click.contains("value")) {
                        int page = Integer.parseInt(click.getString("value"));
                        click.remove("value");
                        click.add(new SNBT.Int("page", page));
                    }
                }
            }
        }
        if (tag.contains("hover_event") || tag.contains("hoverEvent")) {
            tag.rename("hoverEvent", "hover_event");
            SNBT.Compound hover = tag.getCompound("hover_event");
            String action = hover.getString("action");
            if (hover.contains("value") && !action.equals("show_text"))
                hover.rename("value", "contents");
            if (hover.contains("contents") && hover.get("contents") instanceof SNBT.Compound c) {
                switch (action) {
                    case "show_text" -> c.name = "value";
                    case "show_item" -> {
                        hover.value.addAll(c.value);
                        hover.remove("contents");
                        if (hover.contains("components"))
                            ItemComponents.elevateComponents(hover.getCompound("components").value);
                    }
                    case "show_entity" -> {
                        hover.value.addAll(c.value);
                        hover.remove("contents");
                        if (hover.contains("name")) {
                            SNBT.Tag name = elevateNBT(hover.get("name"), true);
                            hover.remove("name");
                            hover.add(name);
                        }
                        if (hover.contains("id")) { //could be either the old uuid or the new entity id. thanks mojang
                            SNBT.Strings id = (SNBT.Strings)hover.get("id");
                            try {
                                UUID.fromString(id.value);
                                id.name = "uuid";
                            } catch (IllegalArgumentException ignored) {}
                        }
                        if (hover.contains("type"))
                            hover.rename("type", "id");
                    }
                }
            }
        }
    }
}
