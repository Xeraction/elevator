package xeraction.elevator.util;

import xeraction.elevator.Elevator;
import xeraction.elevator.ParseException;
import xeraction.elevator.StringIterator;
import xeraction.elevator.command.AttributeCommand;

import java.util.*;
import java.util.stream.Collectors;

public class ItemComponents {

    //elevate existing item nbt
    public static void elevateItem(SNBT.Compound tag) {
        String data = null;
        if (tag.contains("Damage")) {
            data = "" + tag.getInt("Damage");
            if (!tag.contains("tag"))
                tag.add(new SNBT.Compound("tag", new ArrayList<>()));
            tag.getCompound("tag").add(tag.get("Damage"));
            tag.remove("Damage");
        }
        if (tag.get("id") instanceof SNBT.Int i) {
            tag.remove("id");
            tag.add(new SNBT.Strings("id", LegacyData.flattenItemNS("" + i.value, data)));
        }
        String id = tag.contains("id") ? LegacyData.renameItemId(tag.getString("id")) : null;
        if (id != null)
            tag.set("id", id);
        if (tag.contains("Count")) {
            if (tag.get("Count") instanceof SNBT.Int)
                tag.rename("Count", "count");
            else {
                int count = tag.getByte("Count");
                tag.remove("Count");
                tag.add(new SNBT.Int("count", count));
            }
        }
        if (tag.contains("tag")) {
            SNBT.Compound tt = tag.getCompound("tag");
            if (id != null && tt.contains("Damage") && !LegacyData.retainDamage.contains(id))
                tt.remove("Damage");
            List<SNBT.Tag> components = fromNBT(tt, id);
            tag.remove("tag");
            tag.add(new SNBT.Compound("components", components));
        }
        if (tag.contains("components"))
            elevateComponents(tag.getCompound("components").value);
    }

    //elevate existing components
    public static void elevateComponents(List<SNBT.Tag> components) {
        Map<Integer, SNBT.Tag> changedTags = new HashMap<>();
        List<String> addedHideFlags = new ArrayList<>();
        boolean hideCompleteTooltip = false;
        boolean hideAdditionalTooltip = false;

        for (int i = 0; i < components.size(); i++) {
            SNBT.Tag t = components.get(i);
            String name = t.name.startsWith("minecraft:") ? t.name.substring(10) : t.name;
            switch (name) {
                case "attribute_modifiers" -> {
                    //turn compound into list and move hide flag into its own component
                    SNBT.List modifiers = null;
                    if (t instanceof SNBT.Compound c) {
                        if (c.contains("show_in_tooltip") && !c.getBoolean("show_in_tooltip"))
                            addedHideFlags.add("attribute_modifiers");
                        if (c.contains("modifiers")) {
                            modifiers = new SNBT.List(t.name, c.getList("modifiers"));
                            changedTags.put(i, modifiers);
                        }
                    } else
                        modifiers = (SNBT.List)t;

                    //merge uuid and name into id
                    if (modifiers != null) {
                        for (SNBT.Tag ta : modifiers.value) {
                            SNBT.Compound c = (SNBT.Compound)ta;
                            if (c.contains("uuid")) {
                                c.add(new SNBT.Strings("id", "minecraft:" + SNBT.buildUUID(c.getArray("uuid"))));
                                c.remove("uuid");
                                c.remove("name");
                            }
                            c.set("type", LegacyData.renameAttribute(c.getString("type")));
                        }
                    }
                }
                case "block_entity_data" -> BlockEntityData.elevateBlockEntity((SNBT.Compound)t, null);
                case "bundle_contents", "charged_projectiles", "container" -> {
                    for (SNBT.Tag item : ((SNBT.List)t).value)
                        elevateItem(((SNBT.Compound)item).getCompound("item"));
                }
                case "can_break", "can_place_on" -> {
                    //turn compound into list and move hide flag into its own component
                    if (t instanceof SNBT.Compound c) {
                        elevateBlockPredicate(c);
                        if (c.contains("show_in_tooltip")) {
                            if (!c.getBoolean("show_in_tooltip"))
                                addedHideFlags.add(name);
                            c.remove("show_in_tooltip");
                        }
                        if (c.contains("predicates"))
                            changedTags.put(i, new SNBT.List(t.name, c.getList("predicates")));
                        else if (c.value.isEmpty())
                            changedTags.put(i, null);
                    } else if (t instanceof SNBT.List l) {
                        for (SNBT.Tag tag : l.value)
                            elevateBlockPredicate((SNBT.Compound)tag);
                    }
                }
                case "custom_name", "item_name" -> {
                    //change json string to snbt
                    if (t instanceof SNBT.Strings s)
                        changedTags.put(i, TextComponent.elevateNBT(s, true));
                }
                case "custom_model_data" -> {
                    if (t instanceof SNBT.Int) {
                        Elevator.warn("Old custom_model_data components are no longer supported. Please update by hand according to the resource pack.");
                        changedTags.put(i, null);
                    }
                }
                case "dyed_color" -> {
                    //turn compound into int and move hide flag into its own component
                    if (t instanceof SNBT.Compound c) {
                        if (c.contains("show_in_tooltip") && !c.getBoolean("show_in_tooltip"))
                            addedHideFlags.add("dyed_color");
                        changedTags.put(i, new SNBT.Int(t.name, c.getInt("rgb")));
                    }
                }
                case "enchantments", "stored_enchantments" -> {
                    SNBT.Compound c = (SNBT.Compound)t;
                    if (c.contains("show_in_tooltip")) {
                        if (!c.getBoolean("show_in_tooltip"))
                            addedHideFlags.add(name);
                        c.remove("show_in_tooltip");
                    }
                    if (c.contains("levels"))
                        changedTags.put(i, new SNBT.Compound(c.name, c.getCompound("levels").value));
                    else if (c.value.isEmpty())
                        changedTags.put(i, null);
                }
                case "equippable" -> ((SNBT.Compound)t).rename("model", "asset_id");
                case "food" -> {
                    SNBT.Compound c = (SNBT.Compound)t;
                    SNBT.Compound consumable = new SNBT.Compound("consumable", new ArrayList<>());
                    if (c.contains("eat_seconds")) {
                        consumable.add(c.get("eat_seconds"));
                        c.remove("eat_seconds");
                    }
                    if (c.contains("effects")) {
                        SNBT.Compound effect = new SNBT.Compound(null, new ArrayList<>());
                        effect.add(new SNBT.Strings("type", "apply_effects"));
                        effect.add(c.get("effects"));
                        consumable.add(new SNBT.List("on_consume_effects", List.of(effect)));
                        c.remove("effects");
                    }
                    if (!consumable.value.isEmpty())
                        components.add(consumable);
                    if (c.contains("using_converts_to")) {
                        SNBT.Compound item = c.getCompound("using_converts_to");
                        c.remove("using_converts_to");
                        elevateItem(item);
                        item.name = "use_remainder";
                        components.add(item);
                    }
                }
                case "fire_resistant" -> {
                    changedTags.put(i, new SNBT.Compound("damage_resistant", List.of(new SNBT.Strings("types", "#minecraft:is_fire"))));
                }
                case "hide_additional_tooltip" -> {
                    hideAdditionalTooltip = true;
                    changedTags.put(i, null);
                }
                case "hide_tooltip" -> {
                    hideCompleteTooltip = true;
                    changedTags.put(i, null);
                }
                case "instrument" -> {
                    if (t instanceof SNBT.Compound c) {
                        if (c.contains("description") && c.get("description") instanceof SNBT.Strings s) {
                            c.remove("description");
                            c.add(TextComponent.elevateNBT(s, true));
                        }
                    }
                }
                case "jukebox_playable" -> {
                    if (t instanceof SNBT.Compound c) {
                        if (c.contains("show_in_tooltip") && !c.getBoolean("show_in_tooltip"))
                            addedHideFlags.add("jukebox_playable");
                        changedTags.put(i, new SNBT.Strings(c.name, c.getString("song")));
                    }
                }
                case "lock" -> {
                    if (t instanceof SNBT.Strings s) {
                        changedTags.put(i, new SNBT.Compound(s.name, List.of(new SNBT.Compound("components", List.of(new SNBT.Strings("minecraft:custom_name", s.value))))));
                    } else {
                        SNBT.Compound c = (SNBT.Compound) t;
                        if (c.contains("components") && c.get("components") instanceof SNBT.Compound comp)
                            elevateComponents(comp.value);
                        if (c.contains("predicates") && c.get("predicates") instanceof SNBT.Compound comp)
                            for (SNBT.Tag pred : comp.value)
                                elevateComponentPredicate(pred);
                    }
                }
                case "lore" -> {
                    SNBT.List l = (SNBT.List)t;
                    for (int j = 0; j < l.value.size(); j++) {
                        if (l.value.get(j) instanceof SNBT.Strings s)
                            l.value.set(j, TextComponent.elevateNBT(s, false));
                    }
                }
                case "trim", "unbreakable" -> {
                    SNBT.Compound c = (SNBT.Compound)t;
                    if (c.contains("show_in_tooltip")) {
                        if (!c.getBoolean("show_in_tooltip"))
                            addedHideFlags.add(name);
                        c.remove("show_in_tooltip");
                    }
                }
                case "use_remainder" -> elevateItem((SNBT.Compound)t);
                case "weapon" -> {
                    SNBT.Compound c = (SNBT.Compound)t;
                    c.rename("damage_per_attack", "item_damage_per_attack");
                    if (c.contains("can_disable_blocking")) {
                        c.add(new SNBT.Float("disable_blocking_for_seconds", c.getBoolean("can_disable_blocking") ? 5 : 0));
                        c.remove("can_disable_blocking");
                    }
                }
                case "written_book_content" -> {
                    SNBT.Compound c = (SNBT.Compound)t;
                    if (c.contains("pages")) {
                        List<SNBT.Tag> pages = c.getList("pages");
                        for (int j = 0; j < pages.size(); j++) {
                            SNBT.Tag current = pages.get(j);
                            if (current instanceof SNBT.Strings s)
                                pages.set(j, TextComponent.elevateNBT(s, false));
                            else if (current instanceof SNBT.Compound comp && (comp.contains("raw") || comp.contains("filtered"))) {
                                if (comp.contains("raw") && comp.get("raw") instanceof SNBT.Strings s) {
                                    comp.remove("raw");
                                    comp.add(TextComponent.elevateNBT(s, true));
                                } //too lazy to add a new method get off my back
                                if (comp.contains("filtered") && comp.get("filtered") instanceof SNBT.Strings s) {
                                    comp.remove("filtered");
                                    comp.add(TextComponent.elevateNBT(s, true));
                                }
                            }
                        }
                    }
                }
            }
        }

        if (!changedTags.isEmpty())
            changedTags.forEach(components::set);
        components.removeIf(Objects::isNull);

        if (hideAdditionalTooltip) {
            for (SNBT.Tag t : components) {
                String name = t.name.startsWith("minecraft:") ? t.name.substring(10) : t.name;
                if (additionalTooltips.contains(name))
                    addedHideFlags.add(name);
            }
        }

        if (!addedHideFlags.isEmpty() || hideCompleteTooltip) {
            SNBT.Compound display = null;
            for (SNBT.Tag t : components) {
                if (t.name.equals("tooltip_display")) {
                    display = (SNBT.Compound)t;
                    break;
                }
            }
            if (display == null) {
                display = new SNBT.Compound("minecraft:tooltip_display", new ArrayList<>());
                components.add(display);
            }
            boolean contains = display.contains("hide_tooltip");
            if (contains && !hideCompleteTooltip)
                display.remove("hide_tooltip");
            else if (!contains && hideCompleteTooltip)
                display.add(new SNBT.Boolean("hide_tooltip", true));
            else if (contains && hideCompleteTooltip)
                display.set("hide_tooltip", true);
            if (!addedHideFlags.isEmpty()) {
                SNBT.List comps;
                if (display.contains("hidden_components"))
                    comps = (SNBT.List)display.get("hidden_components");
                else {
                    comps = new SNBT.List("hidden_components", new ArrayList<>());
                    display.add(comps);
                }
                for (String s : addedHideFlags)
                    comps.value.add(new SNBT.Strings(null, "minecraft:" + s));
            }
        }

        for (SNBT.Tag t : components)
            if (!t.name.startsWith("minecraft:"))
                t.name = "minecraft:" + t.name;
    }

    public static void elevateComponentPredicate(SNBT.Tag tag) {
        if (tag.name.equals("minecraft:attribute_modifiers")) {
            //TODO merge name and uuid into id, missing documentation from before the merge
        }
    }

    private static final List<String> additionalTooltips = List.of("banner_patterns", "bees", "block_entity_data", "block_state", "bundle_contents", "charged_projectiles", "container", "container_loot", "firework_explosion", "fireworks", "instrument", "map_id", "pot_decorations", "potion_contents", "written_book_content");

    //turn legacy item nbt into a list of item components (thanks mojang)
    public static List<SNBT.Tag> fromNBT(SNBT.Compound tag, String itemId) {
        List<SNBT.Tag> components = new ArrayList<>();
        List<String> hiddenComponents = new ArrayList<>();
        boolean glint = false;

        if (tag.contains("display")) {
            SNBT.Compound display = tag.getCompound("display");
            if (display.contains("Name"))
                components.add(TextComponent.elevateNBT(display.get("Name"), false).name("custom_name"));
            else if (display.contains("LocName")) //legacy LocName before 1.13
                components.add(new SNBT.Compound("custom_name", List.of(new SNBT.Strings("translate", display.getString("LocName")))));
            if (display.contains("Lore"))
                components.add(new SNBT.List("lore", display.getList("Lore").stream()
                        .map(t -> TextComponent.elevateNBT(t, false)).collect(Collectors.toCollection(ArrayList::new))));
            if (display.contains("color"))
                components.add(new SNBT.Int("dyed_color", display.getInt("color")));
            if (display.contains("MapColor"))
                components.add(new SNBT.Int("map_color", display.getInt("MapColor")));
            tag.remove("display");
        }

        if (tag.contains("HideFlags")) {
            int flags = tag.getInt("HideFlags");
            if ((flags & 0x01) != 0)
                hiddenComponents.add("minecraft:enchantments");
            if ((flags & 0x02) != 0)
                hiddenComponents.add("minecraft:attribute_modifiers");
            if ((flags & 0x04) != 0)
                hiddenComponents.add("minecraft:unbreakable");
            if ((flags & 0x08) != 0)
                hiddenComponents.add("minecraft:can_break");
            if ((flags & 0x10) != 0)
                hiddenComponents.add("minecraft:can_place_on");
            if ((flags & 0x20) != 0) {
                hiddenComponents.add("minecraft:potion_contents");
                hiddenComponents.add("minecraft:stored_enchantments");
                hiddenComponents.add("minecraft:written_book_content");
                hiddenComponents.add("minecraft:firework_explosion");
                hiddenComponents.add("minecraft:fireworks");
                hiddenComponents.add("minecraft:bundle_contents");
                hiddenComponents.add("minecraft:map_id");
            }
            if ((flags & 0x40) != 0)
                hiddenComponents.add("minecraft:dyed_color");
            if ((flags & 0x80) != 0)
                hiddenComponents.add("minecraft:trim");
            tag.remove("HideFlags");
        }

        if (tag.contains("CustomModelData")) {
            Elevator.warn("Item with legacy custom model data found. This is incompatible with the new model format. Please update by hand according to the specific resource pack.");
            throw new ParseException("See command warning");
        }

        if (tag.contains("AttributeModifiers")) {
            SNBT.List modifiers = (SNBT.List)tag.get("AttributeModifiers");
            for (SNBT.Tag t : modifiers.value) {
                SNBT.Compound modifier = (SNBT.Compound)t;
                if (modifier.contains("AttributeName")) {
                    modifier.rename("AttributeName", "type");
                    modifier.set("type", LegacyData.renameAttribute(modifier.getString("type")));
                }
                modifier.rename("Slot", "slot");
                elevateAttributeModifier(modifier);
            }
            tag.remove("AttributeModifiers");
            modifiers.name = "attribute_modifiers";
            components.add(modifiers);
        }

        tag.rename("ench", "Enchantments");
        if (tag.contains("Enchantments")) {
            List<SNBT.Tag> enchantList = collectEnchants((SNBT.List)tag.get("Enchantments"));
            if (enchantList == null)
                glint = true;
            else
                components.add(new SNBT.Compound("enchantments", enchantList));
            tag.remove("Enchantments");
        }

        if (tag.contains("StoredEnchantments")) {
            List<SNBT.Tag> enchantList = collectEnchants((SNBT.List)tag.get("StoredEnchantments"));
            if (enchantList == null)
                glint = true;
            else
                components.add(new SNBT.Compound("stored_enchantments", enchantList));
            tag.remove("StoredEnchantments");
        }

        if (tag.contains("RepairCost")) {
            components.add(new SNBT.Int("repair_cost", tag.getInt("RepairCost")));
            tag.remove("RepairCost");
        }

        if (tag.contains("CanDestroy")) {
            List<SNBT.Tag> ids = tag.getList("CanDestroy");
            List<SNBT.Tag> blocks = new ArrayList<>();
            for (SNBT.Tag t : ids)
                blocks.add(new BlockPredicate(new StringIterator(((SNBT.Strings)t).value)).toItemComponentNBT());
            components.add(blocks.size() == 1 ? blocks.getFirst().name("can_break") : new SNBT.List("can_break", blocks));
            tag.remove("CanDestroy");
        }

        if (tag.contains("CanPlaceOn")) {
            List<SNBT.Tag> ids = tag.getList("CanPlaceOn");
            List<SNBT.Tag> blocks = new ArrayList<>();
            for (SNBT.Tag t : ids)
                blocks.add(new BlockPredicate(new StringIterator(((SNBT.Strings)t).value)).toItemComponentNBT());
            components.add(blocks.size() == 1 ? blocks.getFirst().name("can_place_on") : new SNBT.List("can_place_on", blocks));
            tag.remove("CanPlaceOn");
        }

        if (tag.contains("BlockEntityTag")) {
            SNBT.Compound beTag = tag.getCompound("BlockEntityTag");
            if (beTag.contains("note_block_sound")) {
                components.add(beTag.get("note_block_sound"));
                beTag.remove("note_block_sound");
            }
            if (beTag.contains("Base")) {
                components.add(new SNBT.Strings("base_color", LegacyData.renameBannerColor("" + beTag.getInt("Base"))));
                beTag.remove("Base");
            }
            if (beTag.contains("Patterns")) {
                for (SNBT.Tag t : beTag.getList("Patterns")) {
                    SNBT.Compound c = (SNBT.Compound)t;
                    c.rename("Pattern", "pattern");
                    c.set("pattern", LegacyData.renameBannerPattern(c.getString("pattern")));
                    c.add(new SNBT.Strings("color", LegacyData.renameBannerColor("" + c.getInt("Color"))));
                    c.remove("Color");
                }
                components.add(beTag.get("Patterns").name("banner_patterns"));
                beTag.remove("banner_patterns");
            }
            if (beTag.contains("sherds")) {
                components.add(beTag.get("sherds").name("pot_decorations"));
                beTag.remove("pot_decorations");
            }
            if (beTag.contains("Items")) {
                List<SNBT.Tag> items = new ArrayList<>();
                for (SNBT.Tag t : beTag.getList("Items")) {
                    SNBT.Compound c = (SNBT.Compound)t;
                    ItemComponents.elevateItem(c);
                    items.add(new SNBT.Compound(null, List.of(c.get("slot"), c.name("item"))));
                    c.remove("slot");
                }
                components.add(new SNBT.List("container", items));
                beTag.remove("Items");
            }
            if (beTag.contains("Bees")) {
                for (SNBT.Tag t : beTag.getList("Bees")) {
                    SNBT.Compound c = (SNBT.Compound)t;
                    c.rename("EntityData", "entity_data");
                    EntityData.elevateEntityData(c.getCompound("entity_data"), null);
                    c.add(new SNBT.Int("min_ticks_in_hive", 2400)); //default values from mcstacker
                    c.add(new SNBT.Int("ticks_in_hive", 5));
                }
                components.add(beTag.get("Bees").name("bees"));
                beTag.remove("bees");
            }
            if (beTag.contains("Lock")) {
                components.add(beTag.get("Lock").name("lock"));
                beTag.remove("lock");
            }
            if (beTag.contains("LootTable") || beTag.contains("LootTableSeed")) {
                List<SNBT.Tag> loot = new ArrayList<>();
                if (beTag.contains("LootTable")) {
                    loot.add(beTag.get("LootTable").name("loot_table"));
                    beTag.remove("LootTable");
                }
                if (beTag.contains("LootTableSeed")) {
                    loot.add(beTag.get("LootTableSeed").name("seed"));
                    beTag.remove("seed");
                }
                components.add(new SNBT.Compound("container_loot", loot));
            }
            if (!beTag.value.isEmpty()) {
                //the legacy block entity tag specifically excludes the block entity id which is required for the component. thanks mojang
                if (itemId != null) {
                    beTag.add(new SNBT.Strings("id", "minecraft:" + getBEId(itemId)));
                    beTag.name = "block_entity_data";
                    components.add(beTag);
                }
                tag.remove("BlockEntityTag");
            }
        }

        if (tag.contains("BlockStateTag")) {
            SNBT.Compound bsTag = tag.getCompound("BlockStateTag");
            tag.remove("BlockStateTag");
            bsTag.name = "block_state";
            components.add(bsTag);
        }

        if (tag.contains("Damage")) {
            components.add(new SNBT.Int("damage", tag.getInt("Damage")));
            tag.remove("Damage");
        }

        if (tag.contains("Unbreakable")) {
            if ((tag.getInt("Unbreakable") == 1))
                components.add(new SNBT.Compound("unbreakable", List.of()));
            tag.remove("Unbreakable");
        }

        List<SNBT.Tag> potionList = null;
        if (tag.contains("custom_potion_effects") || tag.contains("CustomPotionEffects")) {
            potionList = new ArrayList<>();
            SNBT.List l = tag.contains("custom_potion_effects") ? (SNBT.List)tag.get("custom_potion_effects") : (SNBT.List)tag.get("CustomPotionEffects");
            l.name = "custom_effects";
            for (SNBT.Tag t : l.value)
                EntityData.elevateEffect((SNBT.Compound)t);
            potionList.add(l);
            tag.remove("custom_effects");
        }
        if (tag.contains("Potion")) {
            if (potionList == null)
                potionList = new ArrayList<>();
            potionList.add(new SNBT.Strings("potion", tag.getString("Potion")));
            tag.remove("Potion");
        }
        if (tag.contains("CustomPotionColor")) {
            if (potionList == null)
                potionList = new ArrayList<>();
            potionList.add(new SNBT.Int("custom_color", tag.getInt("CustomPotionColor")));
            tag.remove("CustomPotionColor");
        }
        if (potionList != null)
            components.add(new SNBT.Compound("potion_contents", potionList));

        if (tag.contains("Trim")) {
            components.add(new SNBT.Compound("trim", tag.getCompound("Trim").value));
            tag.remove("Trim");
        }

        if (tag.contains("EntityTag")) {
            if (itemId != null && itemId.endsWith("_bucket")) {
                List<SNBT.Tag> data = new ArrayList<>();
                if (tag.contains("BucketVariantTag"))
                    data.add(tag.get("BucketVariantTag"));
                data.addAll(tag.getCompound("EntityTag").value);
                components.add(new SNBT.Compound("bucket_entity_data", data));
            } else
                components.add(new SNBT.Compound("entity_data", tag.getCompound("EntityTag").value));
            tag.remove("EntityTag");
        }

        if (tag.contains("pages") && itemId != null) {
            if (itemId.endsWith("writable_book")) {
                //decided to ignore the filtered pages because no one in their right mind would ever need to mess with those in a command
                components.add(new SNBT.Compound("writable_book_content", List.of(tag.get("pages"))));
            } else {
                List<SNBT.Tag> content = new ArrayList<>();
                List<SNBT.Tag> pages = new ArrayList<>();
                for (SNBT.Tag t : tag.getList("pages"))
                    pages.add(TextComponent.elevateNBT(t, false));
                content.add(new SNBT.List("pages", pages));
                if (tag.contains("author"))
                    content.add(tag.get("author"));
                if (tag.contains("title"))
                    content.add(new SNBT.Compound("title", List.of(new SNBT.Strings("raw", tag.getString("title")))));
                if (tag.contains("generation"))
                    content.add(tag.get("generation"));
                if (tag.contains("resolved"))
                    content.add(tag.get("resolved"));
                components.add(new SNBT.Compound("written_book_content", content));
            }
            tag.remove("pages");
        }

        if (tag.contains("Recipes")) {
            SNBT.List recipes = (SNBT.List)tag.get("Recipes");
            tag.remove("Recipes");
            recipes.name = "recipes";
            components.add(recipes);
        }

        if (tag.contains("Items")) {
            List<SNBT.Tag> items = new ArrayList<>();
            for (SNBT.Tag t : tag.getList("Items")) {
                SNBT.Compound item = (SNBT.Compound)t;
                elevateItem(item);
                items.add(item);
            }
            components.add(new SNBT.List("bundle_contents", items));
            tag.remove("Items");
        }

        List<SNBT.Tag> lodestoneList = null;
        if (tag.contains("LodestoneDimension") || tag.contains("LodestonePos")) {
            lodestoneList = new ArrayList<>();
            if (tag.contains("LodestoneDimension")) {
                lodestoneList.add(new SNBT.Strings("dimension", tag.getString("LodestoneDimension")));
                tag.remove("LodestoneDimension");
            }
            if (tag.contains("LodestonePos")) {
                SNBT.Compound pos = tag.getCompound("LodestonePos");
                int x = pos.getInt("X");
                int y = pos.getInt("Y");
                int z = pos.getInt("Z");
                lodestoneList.add(new SNBT.Array("pos", List.of(new SNBT.Int(null, x), new SNBT.Int(null, y), new SNBT.Int(null, z)), 'I'));
                tag.remove("LodestonePos");
            }
        }
        if (lodestoneList != null || tag.contains("LodestoneTracked")) {
            if (lodestoneList == null)
                lodestoneList = new ArrayList<>();
            else
                lodestoneList.add(new SNBT.Boolean("tracked", tag.getByte("LodestoneTracked") == 1));
            components.add(new SNBT.Compound("lodestone_tracker", lodestoneList));
        }

        if (tag.contains("ChargedProjectiles")) {
            List<SNBT.Tag> proj = tag.getList("ChargedProjectiles");
            for (SNBT.Tag t : proj)
                elevateItem((SNBT.Compound)t);
            components.add(new SNBT.List("charged_projectiles", proj));
            tag.remove("ChargedProjectiles");
        }

        if (tag.contains("DebugProperty")) {
            components.add(new SNBT.Compound("debug_stick_state", tag.getCompound("DebugProperty").value));
            tag.remove("DebugProperty");
        }

        if (tag.contains("Fireworks")) {
            SNBT.Compound fireworks = tag.getCompound("Fireworks");
            List<SNBT.Tag> l = new ArrayList<>();
            if (fireworks.contains("Explosions")) {
                SNBT.List expl = (SNBT.List)fireworks.get("Explosions");
                for (SNBT.Tag t : expl.value)
                    elevateExplosion((SNBT.Compound)t);
                expl.name = "explosions";
                l.add(expl);
            }
            if (fireworks.contains("Flight"))
                l.add(new SNBT.Byte("flight_duration", fireworks.getByte("Flight")));
            components.add(new SNBT.Compound("fireworks", l));
            tag.remove("Fireworks");
        }

        if (tag.contains("Explosion")) {
            SNBT.Compound expl = tag.getCompound("Explosion");
            elevateExplosion(expl);
            components.add(new SNBT.Compound("firework_explosion", expl.value));
            tag.remove("Explosion");
        }

        if (tag.contains("instrument")) {
            components.add(new SNBT.Strings("instrument", tag.getString("instrument")));
            tag.remove("instrument");
        }

        if (tag.contains("map")) {
            components.add(new SNBT.Int("map_id", tag.getInt("map")));
            tag.remove("map");
        }

        if (tag.contains("Decorations")) {
            List<SNBT.Tag> l = tag.getList("Decorations");
            for (SNBT.Tag t : l)
                elevateMapDecoration((SNBT.Compound)t);
            components.add(new SNBT.Compound("map_decorations", l));
            tag.remove("Decorations");
        }

        if (tag.contains("SkullOwner")) {
            if (tag.get("SkullOwner") instanceof SNBT.Strings str)
                components.add(new SNBT.Strings("profile", str.value));
            else if (tag.get("SkullOwner") instanceof SNBT.Compound owner) {
                owner.rename("Name", "name");
                owner.rename("Id", "id");
                SNBT.List textures = (SNBT.List)owner.getCompound("Properties").get("textures");
                textures.name = "properties";
                for (SNBT.Tag t : textures.value) {
                    SNBT.Compound c = (SNBT.Compound)t;
                    c.rename("Value", "value");
                    c.rename("Signature", "signature");
                    c.value.add(new SNBT.Strings("name", "textures"));
                }
                owner.remove("Properties");
                owner.add(textures);
                components.add(new SNBT.Compound("profile", owner.value));
            }
            tag.remove("SkullOwner");
        }

        if (tag.contains("effects")) {
            List<SNBT.Tag> effects = tag.getList("effects");
            components.add(new SNBT.List("suspicious_stew_effects", effects));
            tag.remove("effects");
        }

        if (!hiddenComponents.isEmpty()) {
            SNBT.List hidden = new SNBT.List("hidden_components", hiddenComponents.stream().map(s -> (SNBT.Tag)new SNBT.Strings(null, s)).toList());
            SNBT.Compound tooltip = new SNBT.Compound("tooltip_display", List.of(hidden));
            components.add(tooltip);
        }

        if (!tag.value.isEmpty()) {
            //everything that's left is considered custom data and put into the corresponding component
            components.add(new SNBT.Compound("custom_data", tag.value));
        }

        if (glint) {
            components.add(new SNBT.Boolean("enchantment_glint_override", true));
        }

        for (SNBT.Tag t : components)
            if (!t.name.startsWith("minecraft:"))
                t.name = "minecraft:" + t.name;

        return components;
    }

    private static List<SNBT.Tag> collectEnchants(SNBT.List enchantments) {
        if (enchantments.value.isEmpty())
            return null;
        List<SNBT.Tag> enchantList = new ArrayList<>();
        for (SNBT.Tag t : enchantments.value) {
            SNBT.Compound enchant = (SNBT.Compound)t;
            if (enchant.value.isEmpty())
                continue;
            String id;
            if (enchant.get("id") instanceof SNBT.Int i)
                id = LegacyData.renameEnchantmentId("" + i.value);
            else
                id = LegacyData.renameEnchantmentId(enchant.getString("id"));
            int level = enchant.getInt("lvl");
            enchantList.add(new SNBT.Int(id, level));
        }
        if (enchantList.isEmpty())
            return null;
        return enchantList;
    }

    public static String getBEId(String itemId) {
        if (itemId.startsWith("minecraft:"))
            itemId = itemId.substring(10);
        if (itemId.endsWith("_banner"))
            return "banner";
        if (itemId.endsWith("_bed"))
            return "bed";
        if (itemId.endsWith("bee_nest"))
            return "beehive";
        if (itemId.endsWith("_command_block"))
            return "command_block";
        if (itemId.endsWith("_hanging_sign"))
            return "hanging_sign";
        if (itemId.equals("spawner"))
            return "mob_spawner";
        if (itemId.endsWith("_shulker_box"))
            return "shulker_box";
        if (itemId.endsWith("_sign"))
            return "sign";
        if (itemId.endsWith("_skull") || itemId.endsWith("_head"))
            return "skull";
        return itemId;
    }

    private static void elevateExplosion(SNBT.Compound explosion) {
        explosion.rename("Colors", "colors");
        explosion.rename("FadeColors", "fade_colors");
        explosion.rename("Flicker", "has_twinkle");
        explosion.rename("Trail", "has_trail");
        if (explosion.contains("Type")) {
            int type = explosion.getInt("Type");
            String shape = switch (type) {
                case 0 -> "small_ball";
                case 1 -> "large_ball";
                case 2 -> "star";
                case 3 -> "creeper";
                case 4 -> "burst";
                default -> throw new ParseException("Unknown firework explosion type '" + type + "'.");
            };
            explosion.remove("Type");
            explosion.add(new SNBT.Strings("shape", shape));
        }
    }

    private static void elevateMapDecoration(SNBT.Compound deco) {
        if (deco.contains("id")) {
            deco.name = deco.getString("id");
            deco.remove("id");
        }
        if (deco.contains("rot")) {
            deco.add(new SNBT.Float("rotation", (float)deco.getDouble("rot")));
            deco.remove("rot");
        }
        if (deco.contains("type")) {
            int type = deco.getInt("type");
            String typ = switch (type) {
                case 0 -> "player";
                case 1 -> "frame";
                case 2 -> "red_marker";
                case 3 -> "blue_marker";
                case 4 -> "target_x";
                case 5 -> "target_point";
                case 6 -> "player_off_map";
                case 7 -> "player_off_limits";
                case 8 -> "mansion";
                case 9 -> "monument";
                case 10 -> "banner_white";
                case 11 -> "banner_orange";
                case 12 -> "banner_magenta";
                case 13 -> "banner_light_blue";
                case 14 -> "banner_yellow";
                case 15 -> "banner_lime";
                case 16 -> "banner_pink";
                case 17 -> "banner_gray";
                case 18 -> "banner_light_gray";
                case 19 -> "banner_cyan";
                case 20 -> "banner_purple";
                case 21 -> "banner_blue";
                case 22 -> "banner_brown";
                case 23 -> "banner_green";
                case 24 -> "banner_red";
                case 25 -> "banner_black";
                case 26 -> "red_x";
                case 27 -> "desert_village";
                case 28 -> "plains_village";
                case 29 -> "savanna_village";
                case 30 -> "snowy_village";
                case 31 -> "taiga_village";
                case 32 -> "jungle_pyramid";
                case 33 -> "swamp_hut";
                default -> throw new ParseException("Invalid map decoration type '" + type + "'.");
            };
            deco.remove("type");
            deco.add(new SNBT.Strings("type", typ));
        }
    }

    public static void elevateAttributeModifier(SNBT.Compound tag) {
        tag.rename("Amount", "amount");
        if (tag.contains("UUID")) {
            tag.add(new SNBT.Strings("id", "minecraft:" + SNBT.buildUUID(tag.getArray("UUID"))));
            tag.remove("UUID");
        }
        if (tag.contains("Operation")) {
            int op = tag.getInt("Operation");
            String operation = switch (op) {
                case 0 -> "add_value";
                case 1 -> "add_multiplied_base";
                case 2 -> "add_multiplied_total";
                default -> throw new ParseException("Invalid attribute modifier operation '" + op + "'.");
            };
            tag.remove("Operation");
            tag.add(new SNBT.Strings("operation", operation));
        }
        tag.remove("Name");
    }

    private static void elevateBlockPredicate(SNBT.Compound tag) {
        if (!tag.contains("blocks"))
            return;
        if (tag.get("blocks") instanceof SNBT.Strings s)
            s.value = LegacyData.renameBlockId(s.value);
        else {
            for (SNBT.Tag t : tag.getList("blocks"))
                ((SNBT.Strings)t).value = LegacyData.renameBlockId(((SNBT.Strings)t).value);
        }
    }
}
