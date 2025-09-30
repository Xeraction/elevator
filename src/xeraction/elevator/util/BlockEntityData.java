package xeraction.elevator.util;

import xeraction.elevator.StringIterator;
import xeraction.elevator.argument.ItemArgument;

import java.util.ArrayList;
import java.util.List;

public class BlockEntityData {

    public static void elevateBlockEntity(SNBT.Compound tag, String beId) {

        if (tag.contains("components"))
            ItemComponents.elevateComponents(tag.getCompound("components").value);

        TextComponent.jsonToSNBT(tag, "CustomName");

        if (tag.contains("Items")) {
            for (SNBT.Tag t : tag.getList("Items"))
                ItemComponents.elevateItem((SNBT.Compound)t);
        }

        if (tag.contains("Lock") || tag.contains("lock")) {
            tag.rename("Lock", "lock");
            ItemArgument arg = new ItemArgument();
            arg.parse(new StringIterator(tag.getString("lock")));
            tag.set("lock", arg.build());
        }

        if (beId == null && !tag.contains("id"))
            return;

        String id = LegacyData.renameBlockEntityId(beId == null ? tag.getString("id") : beId);
        if (id.startsWith("minecraft:"))
            id = id.substring(10);
        switch (id) {
            case "banner" -> {
                tag.rename("Patterns", "patterns");
                if (tag.contains("patterns")) {
                    for (SNBT.Tag t : tag.getList("patterns")) {
                        SNBT.Compound c = (SNBT.Compound) t;
                        c.rename("Pattern", "pattern");
                        c.set("pattern", LegacyData.renameBannerPattern(c.getString("pattern")));
                        c.add(new SNBT.Strings("color", LegacyData.renameBannerColor("" + c.getInt("Color"))));
                        c.remove("Color");
                    }
                }
            }
            case "beehive" -> {
                elevateBlockPos(tag, "FlowerPos", "flower_pos");
                tag.rename("Bees", "bees");
                if (tag.contains("bees")) {
                    for (SNBT.Tag t : tag.getList("bees")) {
                        SNBT.Compound c = (SNBT.Compound)t;
                        c.rename("EntityData", "entity_data");
                        EntityData.elevateEntityData(c.getCompound("entity_data"), null);
                        c.rename("TicksInHive", "ticks_in_hive");
                        c.rename("MinOccupationTicks", "min_ticks_in_hive");
                    }
                }
            }
            case "decorated_pot", "suspicious_sand", "suspicious_gravel" -> {
                if (tag.contains("item"))
                    ItemComponents.elevateItem(tag.getCompound("item"));
            }
            case "end_gateway" -> elevateBlockPos(tag, "ExitPortal", "exit_portal");
            case "furnace", "blast_furnace", "smoker" -> {
                tag.rename("CookTime", "cooking_time_spent");
                tag.rename("CookTimeTotal", "cooking_total_time");
                tag.rename("BurnTime", "lit_time_remaining");
            }
            case "hanging_sign" -> {
                if (tag.contains("front_text")) {
                    for (SNBT.Tag t : tag.getCompound("front_text").getList("messages")) {
                        SNBT.Strings s = (SNBT.Strings)t;
                        s.value = elevateJSONText(s.value);
                    }
                }
                if (tag.contains("back_text")) {
                    for (SNBT.Tag t : tag.getCompound("back_text").getList("messages")) {
                        SNBT.Strings s = (SNBT.Strings)t;
                        s.value = elevateJSONText(s.value);
                    }
                }
            }
            case "jukebox" -> {
                if (tag.contains("RecordItem"))
                    ItemComponents.elevateItem(tag.getCompound("RecordItem"));
            }
            case "lectern" -> {
                if (tag.contains("Book"))
                    ItemComponents.elevateItem(tag.getCompound("Book"));
            }
            case "mob_spawner" -> elevateSpawner(tag, false);
            case "sign" -> {
                if (tag.contains("front_text")) {
                    List<SNBT.Tag> newTags = new ArrayList<>();
                    SNBT.List msgs = (SNBT.List)tag.getCompound("front_text").get("messages");
                    for (SNBT.Tag t : msgs.value) {
                        if (t instanceof SNBT.Strings s)
                            newTags.add(SNBT.parseTag(new StringIterator(s.value), false));
                        else
                            newTags.add(t);
                    }
                    msgs.value = newTags;
                }
                if (tag.contains("back_text")) {
                    List<SNBT.Tag> newTags = new ArrayList<>();
                    SNBT.List msgs = (SNBT.List)tag.getCompound("back_text").get("messages");
                    for (SNBT.Tag t : msgs.value) {
                        if (t instanceof SNBT.Strings s)
                            newTags.add(SNBT.parseTag(new StringIterator(s.value), false));
                        else
                            newTags.add(t);
                    }
                    msgs.value = newTags;
                }
                //pre-1.20 sign data
                List<SNBT.Tag> legacyTags = new ArrayList<>();
                if (tag.contains("GlowingText")) {
                    legacyTags.add(new SNBT.Byte("has_glowing_text", tag.getByte("GlowingText")));
                    tag.remove("GlowingText");
                }
                if (tag.contains("Color")) {
                    legacyTags.add(new SNBT.Strings("color", tag.getString("Color")));
                    tag.remove("Color");
                }
                List<SNBT.Tag> legacyText = new ArrayList<>();
                if (tag.contains("Text1")) {
                    legacyText.add(SNBT.parseTag(new StringIterator(tag.getString("Text1")), false));
                    tag.remove("Text1");
                }
                if (tag.contains("Text2")) {
                    if (legacyText.isEmpty())
                        legacyText.add(new SNBT.Strings(null, ""));
                    legacyText.add(SNBT.parseTag(new StringIterator(tag.getString("Text2")), false));
                    tag.remove("Text2");
                }
                if (tag.contains("Text3")) {
                    while (legacyText.size() < 2)
                        legacyText.add(new SNBT.Strings(null, ""));
                    legacyText.add(SNBT.parseTag(new StringIterator(tag.getString("Text3")), false));
                    tag.remove("Text3");
                }
                if (tag.contains("Text4")) {
                    while (legacyText.size() < 3)
                        legacyText.add(new SNBT.Strings(null, ""));
                    legacyText.add(SNBT.parseTag(new StringIterator(tag.getString("Text4")), false));
                    tag.remove("Text4");
                }
                if (!legacyText.isEmpty()) {
                    while (legacyText.size() < 4)
                        legacyText.add(new SNBT.Strings(null, ""));
                    legacyTags.add(new SNBT.List("messages", legacyText));
                    tag.add(new SNBT.Compound("front_text", legacyTags));
                }
            }
            case "trial_spawner" -> {
                if (tag.contains("normal_config") && tag.get("normal_config") instanceof SNBT.Compound c)
                    elevateSpawner(c, true);
                if (tag.contains("ominous_config") && tag.get("ominous_config") instanceof SNBT.Compound c)
                    elevateSpawner(c, true);
                if (tag.contains("spawn_data"))
                    elevateSpawnData(tag.getCompound("spawn_data"));
            }
            case "vault" -> {
                if (tag.contains("config")) {
                    SNBT.Compound config = tag.getCompound("config");
                    if (config.contains("key_item"))
                        ItemComponents.elevateItem(config.getCompound("key_item"));
                }
                if (tag.contains("server_data")) {
                    SNBT.Compound data = tag.getCompound("server_data");
                    if (data.contains("items_to_eject")) {
                        for (SNBT.Tag t : data.getList("items_to_eject"))
                            ItemComponents.elevateItem((SNBT.Compound)t);
                    }
                }
                if (tag.contains("shared_data")) {
                    SNBT.Compound data = tag.getCompound("shared_data");
                    if (data.contains("display_item"))
                        ItemComponents.elevateItem(data.getCompound("display_item"));
                }
            }
        }
    }

    public static void elevateBlockPos(SNBT.Compound tag, String oldName, String newName) {
        if (!tag.contains(oldName))
            return;
        SNBT.Compound old = tag.getCompound(oldName);
        SNBT.Array array = new SNBT.Array(newName, new ArrayList<>(), 'I');
        array.value.add(old.get("X").name(null));
        array.value.add(old.get("Y").name(null));
        array.value.add(old.get("Z").name(null));
        tag.remove(oldName);
        tag.add(array);
    }

    public static void elevateSpawner(SNBT.Compound tag, boolean trial) {
        if (tag.contains("EntityId")) {
            setupSpawnData(tag.getString("EntityId"), tag);
            tag.remove("EntityId");
        }
        if (tag.contains("SpawnData"))
            elevateSpawnData(tag.getCompound("SpawnData"));
        if (tag.contains(trial ? "spawn_potentials" : "SpawnPotentials")) {
            for (SNBT.Tag t : tag.getList(trial ? "spawn_potentials" : "SpawnPotentials")) {
                SNBT.Compound c = (SNBT.Compound)t;
                if (c.contains("data"))
                    elevateSpawnData(c.getCompound("data"));
                else if (!c.value.isEmpty()) {
                    c.rename("Weight", "weight");
                    if (c.value.size() > 1) {
                        SNBT.Compound data = new SNBT.Compound("data", new ArrayList<>());
                        if (c.contains("Type")) {
                            data.add(new SNBT.Strings("id", LegacyData.renameEntityId(c.getString("Type"))));
                            c.remove("Type");
                        }
                        if (c.contains("Properties")) {
                            data.value.addAll(c.getCompound("Properties").value);
                            c.remove("Properties");
                        }
                        elevateSpawnData(data);
                        c.add(data);
                    }
                }
            }
        }
    }

    private static void elevateSpawnData(SNBT.Compound tag) {
        if (!tag.contains("entity")) {
            SNBT.Compound entity = new SNBT.Compound("entity", new ArrayList<>());
            entity.value.addAll(tag.value);
            tag.value.removeIf(a -> true);
            tag.add(entity);
        }
        EntityData.elevateEntityData(tag.getCompound("entity"), null);
    }

    public static String elevateJSONText(String old) {
        return TextComponent.elevateNBT(SNBT.parseTag(new StringIterator(old), false), true).build();
    }

    private static void setupSpawnData(String entityId, SNBT.Compound tag) {
        entityId = LegacyData.renameEntityId(entityId);
        if (!tag.contains("SpawnData"))
            tag.add(new SNBT.Compound("SpawnData", new ArrayList<>()));
        tag.getCompound("SpawnData").add(new SNBT.Strings("id", entityId));
    }
}
