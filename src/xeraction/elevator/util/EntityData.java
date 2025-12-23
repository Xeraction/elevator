package xeraction.elevator.util;

import xeraction.elevator.Elevator;
import xeraction.elevator.command.Command;

import java.util.ArrayList;
import java.util.List;

public class EntityData {

    //returns the new entity id in case it was split
    public static String elevateEntityData(SNBT.Compound tag, String entityId) {

        if (tag.contains("ActiveEffects")) {
            SNBT.List effects = (SNBT.List)tag.get("ActiveEffects");
            effects.name = "active_effects";
            for (SNBT.Tag t : effects.value)
                elevateEffect((SNBT.Compound)t);
        }

        tag.rename("AngryAt", "angry_at");
        if (tag.contains("AngerTime")) {
            tag.add(new SNBT.Long("anger_end_time", tag.getInt("AngerTime")));
            tag.remove("AngerTime");
        }

        if (tag.contains("attributes")) {
            for (SNBT.Tag t : tag.getList("attributes")) {
                SNBT.Compound c = (SNBT.Compound)t;
                c.set("id", LegacyData.renameAttribute(c.getString("id")));
            }
        }

        if (tag.contains("Attributes")) {
            tag.rename("Attributes", "attributes");
            for (SNBT.Tag t : tag.getList("attributes")) {
                SNBT.Compound c = (SNBT.Compound)t;
                c.rename("Name", "id");
                c.set("id", LegacyData.renameAttribute(c.getString("id")));
                c.rename("Base", "base");
                if (c.contains("Modifiers")) {
                    c.rename("Modifiers", "modifiers");
                    for (SNBT.Tag tt : c.getList("modifiers"))
                        ItemComponents.elevateAttributeModifier((SNBT.Compound)tt);
                }
            }
        }

        if (tag.contains("Equipment")) {
            List<SNBT.Tag> eq = tag.getList("Equipment");
            tag.remove("Equipment");
            SNBT.Compound equip = new SNBT.Compound("equipment", new ArrayList<>());
            if (!((SNBT.Compound)eq.get(0)).value.isEmpty())
                equip.add(eq.get(0).name("mainhand"));
            if (eq.size() > 1 && !((SNBT.Compound)eq.get(1)).value.isEmpty())
                equip.add(eq.get(1).name("feet"));
            if (eq.size() > 2 && !((SNBT.Compound)eq.get(2)).value.isEmpty())
                equip.add(eq.get(2).name("legs"));
            if (eq.size() > 3 && !((SNBT.Compound)eq.get(3)).value.isEmpty())
                equip.add(eq.get(3).name("chest"));
            if (eq.size() > 4 && !((SNBT.Compound)eq.get(4)).value.isEmpty())
                equip.add(eq.get(4).name("head"));
            if (!equip.value.isEmpty()) {
                for (SNBT.Tag t : equip.value) {
                    SNBT.Compound c = (SNBT.Compound)t;
                    if (!c.value.isEmpty())
                        ItemComponents.elevateItem(c);
                }
                tag.add(equip);
            }
        }

        List<SNBT.Tag> equipment = tag.contains("equipment") ? tag.getCompound("equipment").value : new ArrayList<>();
        if (tag.contains("HandItems")) {
            List<SNBT.Tag> hand = tag.getList("HandItems");
            int size = hand.size();
            equipment.add(hand.get(0).name("mainhand"));
            if (size > 1)
                equipment.add(hand.get(1).name("offhand"));
            tag.remove("HandItems");
        }
        if (tag.contains("ArmorItems")) {
            List<SNBT.Tag> armor = tag.getList("ArmorItems");
            int size = armor.size();
            equipment.add(armor.get(0).name("feet"));
            if (size > 1)
                equipment.add(armor.get(1).name("legs"));
            if (size > 2)
                equipment.add(armor.get(2).name("chest"));
            if (size > 3)
                equipment.add(armor.get(3).name("head"));
            tag.remove("ArmorItems");
        }
        if (tag.contains("body_armor_item")) {
            equipment.add(tag.get("body_armor_item").name("body"));
            tag.remove("body_armor_item");
        }
        if (tag.contains("SaddleItem")) { //why is this not on the wiki?
            equipment.add(tag.get("SaddleItem").name("saddle"));
            tag.remove("SaddleItem");
        }
        equipment.removeIf(t -> ((SNBT.Compound)t).value.isEmpty());
        for (SNBT.Tag t : equipment)
            ItemComponents.elevateItem((SNBT.Compound)t);
        if (!equipment.isEmpty() && !tag.contains("equipment"))
            tag.add(new SNBT.Compound("equipment", equipment));

        if (tag.contains("DropChances")) {
            List<SNBT.Tag> dr = tag.getList("DropChances");
            tag.remove("DropChances");
            SNBT.Compound chance = new SNBT.Compound("drop_chances", new ArrayList<>());
            chance.add(dr.get(0).name("mainhand"));
            if (dr.size() > 1)
                chance.add(dr.get(1).name("feet"));
            if (dr.size() > 2)
                chance.add(dr.get(2).name("legs"));
            if (dr.size() > 3)
                chance.add(dr.get(3).name("chest"));
            if (dr.size() > 4)
                chance.add(dr.get(4).name("head"));
            if (!chance.value.isEmpty())
                tag.add(chance);
        }

        List<SNBT.Tag> dropChances = tag.contains("drop_chances") ? tag.getCompound("drop_chances").value : new ArrayList<>();
        if (tag.contains("HandDropChances")) {
            List<SNBT.Tag> hand = tag.getList("HandDropChances");
            int size = hand.size();
            dropChances.add(hand.get(0).name("mainhand"));
            if (size > 1)
                dropChances.add(hand.get(1).name("offhand"));
            tag.remove("HandDropChances");
        }
        if (tag.contains("ArmorDropChances")) {
            List<SNBT.Tag> armor = tag.getList("ArmorDropChances");
            int size = armor.size();
            dropChances.add(armor.get(0).name("feet"));
            if (size > 1)
                dropChances.add(armor.get(1).name("legs"));
            if (size > 2)
                dropChances.add(armor.get(2).name("chest"));
            if (size > 3)
                dropChances.add(armor.get(3).name("head"));
            tag.remove("ArmorDropChances");
        }
        if (tag.contains("body_armor_drop_chance")) {
            dropChances.add(tag.get("body_armor_drop_chance").name("body"));
            tag.remove("body_armor_drop_chance");
        }
        dropChances.removeIf(t -> ((SNBT.Float)t).value == 0.085F);
        if (!dropChances.isEmpty() && !tag.contains("drop_chances"))
            tag.add(new SNBT.Compound("drop_chances", dropChances));

        BlockEntityData.elevateBlockPos(tag, "Leash", "leash");
        BlockEntityData.elevateBlockPos(tag, "PatrolTarget", "patrol_target");

        mergeFields(tag, "sleeping_pos", "SleepingX", "SleepingY", "SleepingZ");

        if (tag.contains("FallDistance")) {
            tag.add(new SNBT.Double("fall_distance", tag.getFloat("FallDistance")));
            tag.remove("FallDistance");
        }

        if (tag.contains("Passengers")) {
            for (SNBT.Tag t : tag.getList("Passengers"))
                elevateEntityData((SNBT.Compound)t, null);
        }

        if (tag.contains("Inventory")) {
            for (SNBT.Tag t : tag.getList("Inventory"))
                ItemComponents.elevateItem((SNBT.Compound)t);
        }

        if (tag.contains("Items")) {
            for (SNBT.Tag t : tag.getList("Items"))
                ItemComponents.elevateItem((SNBT.Compound)t);
        }

        if (tag.contains("Item"))
            ItemComponents.elevateItem(tag.getCompound("Item"));

        TextComponent.jsonToSNBT(tag, "CustomName");

        if (entityId == null) {
            if (!tag.contains("id"))
                return "";
            entityId = tag.getString("id");
        }
        entityId = LegacyData.renameEntityId(entityId);
        if (entityId.startsWith("minecraft:"))
            entityId = entityId.substring(10);
        String newId = entityId;
        switch (entityId) {
            case "allay" -> tag.remove("CanDuplicate");
            case "area_effect_cloud" -> {
                tag.rename("Effects", "effects");
                if (!tag.contains("potion_contents")) {
                    List<SNBT.Tag> content = new ArrayList<>();
                    if (tag.contains("Potion")) {
                        content.add(new SNBT.Strings("potion", tag.getString("Potion")));
                        tag.remove("Potion");
                    }
                    if (tag.contains("Color")) {
                        content.add(new SNBT.Int("custom_color", tag.getInt("Color")));
                        tag.remove("Color");
                    }
                    if (tag.contains("effects")) {
                        for (SNBT.Tag t : tag.getList("effects"))
                            elevateEffect((SNBT.Compound)t);
                        content.add(new SNBT.List("custom_effects", tag.getList("effects")));
                        tag.remove("effects");
                    }
                    if (!content.isEmpty())
                        tag.add(new SNBT.Compound("potion_contents", content));
                }
                tag.rename("Particle", "custom_particle");
            }
            case "arrow", "spectral_arrow" -> {
                tag.rename("CustomPotionEffects", "custom_potion_effects");
                if (tag.contains("weapon"))
                    ItemComponents.elevateItem(tag.getCompound("weapon"));
                if (tag.contains("item"))
                    ItemComponents.elevateItem(tag.getCompound("item"));
                if (tag.contains("inBlockState"))
                    elevateBlockState(tag.getCompound("inBlockState"));
            }
            case "bee" -> {
                BlockEntityData.elevateBlockPos(tag, "FlowerPos", "flower_pos");
                BlockEntityData.elevateBlockPos(tag, "HivePos", "hive_pos");
            }
            case "block_display" -> {
                if (tag.contains("block_state"))
                    elevateBlockState(tag.getCompound("block_state"));
            }
            case "boat" -> {
                String type = tag.getString("Type");
                if (type.equals("bamboo"))
                    newId = "bamboo_raft";
                else
                    newId = type + "_boat";
                tag.remove("Type");
            }
            case "breeze_wind_charge", "dragon_fireball", "fireball", "large_fireball", "small_fireball", "wind_charge", "wither_skull" -> {
                if (tag.contains("power")) {
                    tag.add(new SNBT.Double("acceleration_power", 1)); //no idea how or if the game translates this
                    tag.remove("power");
                }
            }
            case "cat" -> {
                if (tag.contains("CatType")) {
                    tag.add(new SNBT.Strings("variant", LegacyData.renameCatVariant(tag.getInt("CatType"))));
                    tag.remove("CatType");
                }
            }
            case "chest_boat" -> {
                String type = tag.getString("Type");
                if (type.equals("bamboo"))
                    newId = "bamboo_chest_raft";
                else
                    newId = type + "_chest_boat";
                tag.remove("Type");
            }
            case "command_block_minecart" -> {
                if (tag.contains("Command") && !tag.getString("Command").isEmpty()) {
                    Command cmd = Elevator.parseCommand(tag.getString("Command"));
                    if (cmd != null)
                        tag.set("Command", cmd.build());
                }
            }
            case "dolphin" -> {
                tag.remove("TreasurePosX");
                tag.remove("TreasurePosY");
                tag.remove("TreasurePosZ");
            }
            case "enderman" -> {
                if (tag.contains("carriedBlockState"))
                    elevateBlockState(tag.getCompound("carriedBlockState"));
            }
            case "end_crystal" -> BlockEntityData.elevateBlockPos(tag, "BeamTarget", "beam_target");
            case "falling_block" -> {
                if (tag.contains("TileID")) {
                    int tileId = tag.getInt("TileID");
                    tag.remove("TileID");
                    SNBT.Compound bs = new SNBT.Compound("BlockState", new ArrayList<>());
                    String data = null;
                    if (tag.contains("Data")) {
                        data = "" + tag.getInt("Data");
                        tag.remove("Data");
                    }
                    bs.add(new SNBT.Strings("Name", LegacyData.flattenBlockNS("" + tileId, data)));
                    tag.add(bs);
                }
                if (tag.contains("BlockState")) {
                    elevateBlockState(tag.getCompound("BlockState"));
                    if (tag.contains("TileEntityData"))
                        BlockEntityData.elevateBlockEntity(tag.getCompound("TileEntityData"), ItemComponents.getBEId(tag.getCompound("BlockState").getString("Name")));
                }
            }
            case "firework_rocket" -> {
                if (tag.contains("FireworksItem"))
                    ItemComponents.elevateItem(tag.getCompound("FireworksItem"));
            }
            case "glow_item_frame", "item_frame", "leash_knot" -> mergeFields(tag, "block_pos", "TileX", "TileY", "TileZ");
            case "guardian" -> {
                if (tag.contains("Elder")) {
                    if (tag.getInt("Elder") == 1)
                        newId = "elder_guardian";
                    tag.remove("Elder");
                }
            }
            case "horse" -> {
                if (tag.contains("Type")) {
                    int typ = tag.getInt("Type");
                    tag.remove("Type");
                    switch (typ) {
                        case 1 -> newId = "donkey";
                        case 2 -> newId = "mule";
                        case 3 -> newId = "zombie_horse";
                        case 4 -> newId = "skeleton_horse";
                    }
                }
                if (tag.contains("ArmorItem"))
                    moveBodyToEquipment(tag, "ArmorItem");
            }
            case "item_display", "ominous_item_spawner" -> {
                if (tag.contains("item"))
                    ItemComponents.elevateItem(tag.getCompound("item"));
            }
            case "llama", "trader_llama" -> {
                if (tag.contains("DecorItem"))
                    moveBodyToEquipment(tag, "DecorItem");
            }
            case "mooshroom" -> {
                List<SNBT.Tag> legacy = new ArrayList<>();
                if (tag.contains("EffectId")) {
                    legacy.add(tag.get("EffectId"));
                    tag.remove("EffectId");
                }
                if (tag.contains("EffectDuration")) {
                    legacy.add(tag.get("EffectDuration"));
                    tag.remove("EffectDuration");
                }
                if (!legacy.isEmpty()) {
                    SNBT.Compound c = new SNBT.Compound(null, legacy);
                    c.rename("EffectId", "id");
                    c.rename("EffectDuration", "duration");
                    tag.add(new SNBT.List("stew_effects", List.of(c)));
                }
            }
            case "ocelot" -> {
                if (tag.contains("CatType")) {
                    int typ = tag.getInt("CatType");
                    tag.remove("CatType");
                    String type = null;
                    switch (typ) {
                        case 1 -> type = "black";
                        case 2 -> type = "tabby";
                        case 3 -> type = "siamese";
                    }
                    if (type != null) {
                        newId = "cat";
                        tag.add(new SNBT.Strings("variant", "minecraft:" + type));
                    }
                }
            }
            case "painting" -> {
                mergeFields(tag, "block_pos", "TileX", "TileY", "TileZ");
                tag.rename("Direction", "facing");
                if (tag.contains("Motive")) {
                    tag.rename("Motive", "variant");
                    tag.set("variant", LegacyData.renamePaintingId(tag.getString("variant")));
                }
            }
            case "phantom" -> {
                tag.rename("Size", "size");
                mergeFields(tag, "anchor_pos", "AX", "AY", "AZ");
            }
            case "pig", "strider" -> {
                if (tag.contains("Saddle") && tag.getInt("Saddle") == 1) {
                    if (!tag.contains("equipment"))
                        tag.value.add(new SNBT.Compound("equipment", new ArrayList<>()));
                    tag.getCompound("equipment").add(new SNBT.Compound("saddle", List.of(new SNBT.Strings("id", "minecraft:saddle"), new SNBT.Int("count", 1))));
                    tag.remove("Saddle");
                }
            }
            case "potion" -> {
                if (tag.contains("Item")) {
                    SNBT.Compound item = tag.getCompound("Item");
                    ItemComponents.elevateItem(item);
                    String id = item.getString("id");
                    if (id.equals("minecraft:lingering_potion"))
                        newId = "lingering_potion";
                    else
                        newId = "splash_potion";
                }
            }
            case "skeleton" -> {
                if (tag.contains("SkeletonType")) {
                    int typ = tag.getInt("SkeletonType");
                    tag.remove("SkeletonType");
                    switch (typ) {
                        case 1 -> newId = "wither_skeleton";
                        case 2 -> newId = "stray";
                    }
                }
            }
            case "spawner_minecart" -> BlockEntityData.elevateSpawner(tag, false);
            case "text_display" -> {
                if (tag.contains("text")) {
                    SNBT.Tag t = tag.get("text");
                    tag.remove("text");
                    tag.add(TextComponent.elevateNBT(t, true));
                }
            }
            case "tnt" -> {
                tag.rename("Fuse", "fuse");
                if (tag.contains("block_state"))
                    elevateBlockState(tag.getCompound("block_state"));
            }
            case "tnt_minecart" -> tag.rename("TNTFuse", "fuse");
            case "trident" -> {
                tag.rename("Trident", "item");
                if (tag.contains("item"))
                    ItemComponents.elevateItem(tag.getCompound("item"));
                if (tag.contains("inBlockState"))
                    elevateBlockState(tag.getCompound("inBlockState"));
            }
            case "turtle" -> {
                tag.rename("HasEgg", "has_egg");
                tag.remove("TravelPosX");
                tag.remove("TravelPosY");
                tag.remove("TravelPosZ");
                mergeFields(tag, "home_pos", "HomePosX", "HomePosY", "HomePosZ");
            }
            case "vex" -> {
                tag.rename("LifeTicks", "life_ticks");
                mergeFields(tag, "bound_pos", "BoundX", "BoundY", "BoundZ");
            }
            case "villager", "wandering_trader", "zombie_villager" -> {
                SNBT.Compound vd = tag.contains("VillagerData") ? tag.getCompound("VillagerData") : new SNBT.Compound("VillagerData", new ArrayList<>());
                if (tag.contains("Career")) {
                    int prof = tag.getInt("Profession");
                    tag.remove("Profession");
                    int career = tag.getInt("Career");
                    tag.remove("Career");
                    vd.add(new SNBT.Strings("profession", LegacyData.renameVillagerProfession(prof, career)));
                }
                if (tag.contains("CareerLevel")) {
                    vd.add(new SNBT.Int("level", tag.getInt("CareerLevel")));
                    tag.remove("CareerLevel");
                }
                if (tag.contains("Offers") || vd.contains("Offers")) {
                    SNBT.Compound offers = tag.contains("Offers") ? tag.getCompound("Offers") : vd.getCompound("Offers");
                    for (SNBT.Tag t : offers.getList("Recipes")) {
                        SNBT.Compound c = (SNBT.Compound)t;
                        if (c.contains("buy"))
                            ItemComponents.elevateItem(c.getCompound("buy"));
                        if (c.contains("buyB"))
                            ItemComponents.elevateItem(c.getCompound("buyB"));
                        if (c.contains("sell"))
                            ItemComponents.elevateItem(c.getCompound("sell"));
                    }
                    if (tag.contains("Offers")) {
                        vd.add(tag.getCompound("Offers"));
                        tag.remove("Offers");
                    }
                }
                if (!tag.contains("VillagerData") && !vd.value.isEmpty())
                    tag.add(vd);
                BlockEntityData.elevateBlockPos(tag, "WanderTarget", "wander_target");
            }
            case "zombie" -> {
                if (tag.contains("ZombieType")) {
                    int typ = tag.getInt("ZombieType");
                    tag.remove("ZombieType");
                    if (typ == 6)
                        newId = "husk";
                    else if (typ > 0) {
                        if (!tag.contains("VillagerData"))
                            tag.add(new SNBT.Compound("VillagerData", new ArrayList<>()));
                        tag.getCompound("VillagerData").add(new SNBT.Strings("profession", LegacyData.renameVillagerProfession(typ - 3, -1)));
                        newId = "zombie_villager";
                    }
                    if (typ > 0) {
                        if (tag.contains("id"))
                            tag.set("id", "minecraft:" + newId);
                        elevateEntityData(tag, newId); //just in case
                    }
                }
                if (tag.contains("IsVillager")) {
                    if (tag.getInt("IsVillager") == 1)
                        newId = "zombie_villager";
                    tag.remove("IsVillager");
                    if (tag.contains("id"))
                        tag.set("id", "minecraft:" + newId);
                    elevateEntityData(tag, newId);
                }
            }
        }

        if (entityId.endsWith("minecart")) {
            tag.remove("CustomDisplayTile");
            if (tag.contains("DisplayTile")) {
                String tile = tag.get("DisplayTile") instanceof SNBT.Strings s ? s.value : "" + tag.getInt("DisplayTile");
                tag.remove("DisplayTile");
                tag.add(new SNBT.Compound("DisplayState", new ArrayList<>()));
                tag.getCompound("DisplayState").add(new SNBT.Strings("Name", LegacyData.flattenBlockNS(tile, null)));
            } else if (tag.contains("DisplayState"))
                elevateBlockState(tag.getCompound("DisplayState"));
        }

        if (tag.contains("id"))
            tag.set("id", "minecraft:" + newId);

        return "minecraft:" + newId;
    }

    public static void elevateEffect(SNBT.Compound effect) {
        effect.rename("Ambient", "ambient");
        effect.rename("Amplifier", "amplifier");
        effect.rename("Duration", "duration");
        effect.rename("HiddenEffect", "hidden_effect");
        effect.rename("ShowIcon", "show_icon");
        effect.rename("ShowParticles", "show_particles");
        if (effect.contains("Id")) {
            effect.add(new SNBT.Strings("id", LegacyData.renameEffectId("" + effect.getInt("Id"))));
            effect.remove("Id");
        }
    }

    private static void moveBodyToEquipment(SNBT.Compound tag, String name) {
        SNBT.Compound c = tag.getCompound(name);
        tag.remove(name);
        c.name = "body";
        if (!tag.contains("equipment"))
            tag.add(new SNBT.Compound("equipment", new ArrayList<>()));
        tag.getCompound("equipment").add(c);
    }

    private static void mergeFields(SNBT.Compound tag, String mergeName, String... names) {
        List<SNBT.Tag> merged = new ArrayList<>();
        boolean dont = false;
        for (String name : names) {
            if (tag.contains(name)) {
                SNBT.Tag t = tag.get(name);
                tag.remove(name);
                if (t instanceof SNBT.Strings s && s.value.equals("~")) //not allowed anymore
                    dont = true;
                t.name = null;
                merged.add(t);
            }
        }
        if (merged.isEmpty() || dont)
            return;
        if (merged.getFirst() instanceof SNBT.Int)
            tag.add(new SNBT.Array(mergeName, merged, 'I'));
        else if (merged.getFirst() instanceof SNBT.Long)
            tag.add(new SNBT.Array(mergeName, merged, 'L'));
        else if (merged.getFirst() instanceof SNBT.Byte)
            tag.add(new SNBT.Array(mergeName, merged, 'B'));
        else
            tag.add(new SNBT.List(mergeName, merged));
    }

    private static void elevateBlockState(SNBT.Compound tag) {
        tag.set("Name", LegacyData.renameBlockId(tag.getString("Name")));
    }
}
