package xeraction.elevator.util;

//welcome to the bowels.
//here, you will find nothing but torture and misery.
//awaiting you here are snaking caverns of switches with hundreds of cases and old tech debt data mined from the deepest depths of the game.
//be warned: the longer you linger, the closer you will get to mind-numbingly typing out conversion tables, having no fewer than three wiki pages open at all times, all while listening to every horror video on youtube.
//don't be like me. decide your own fate. (no, i'm not going not insane. please help.)

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LegacyData {

    public static String flattenBlock(String id, String states, SNBT.Compound data) {
        //turn numeric block id into string
        if (Character.isDigit(id.charAt(0)))
            id = blockIds.get(Integer.parseInt(id));

        if (id.startsWith("minecraft:"))
            id = id.substring(10);

        //turn numeric block metadata into states
        if (states != null && Character.isDigit(states.charAt(0))) {
            if (!blockData.containsKey(id))
                states = null;
            else
                states = blockData.get(id).get(Integer.parseInt(states));
        }

        if (states != null && (states.equals("*") || states.equals("-1")))
            states = null;

        //combine block id and states into new block
        if (blockStates.containsKey(id))
            return blockStates.get(id).flatten(states, data, true);

        id = renameBlockId(id);

        if (data != null && !data.value.isEmpty())
            BlockEntityData.elevateBlockEntity(data, ItemComponents.getBEId(id));

        return id + (states != null ? "[" + states + "]" : "") + (data != null ? data.build() : "");
    }

    public static String flattenBlockNS(String id, String data) {
        String flattened = flattenBlock(id, data, null);
        if (flattened.contains("["))
            flattened = flattened.substring(0, flattened.indexOf('['));
        return flattened;
    }

    public static String flattenItem(String id, String meta, SNBT.Compound data) {
        if (Character.isDigit(id.charAt(0))) {
            int num = Integer.parseInt(id);
            if (num < 256)
                id = blockIds.get(num);
            else
                id = itemIds.get(num);
        }

        if (id.startsWith("minecraft:"))
            id = id.substring(10);

        if (meta != null && meta.equals("-1"))
            meta = null;

        if (itemData.containsKey(id)) {
            Map<Integer, String> vals = itemData.get(id);
            if (meta == null)
                id = vals.get(0);
            else
                id = vals.get(Integer.parseInt(meta));
        } else {
            if (blockData.containsKey(id))
                meta = blockData.get(id).get(meta == null ? -1 : Integer.parseInt(meta));
            if (blockStates.containsKey(id))
                return blockStates.get(id).flatten(meta, data, false);
        }

        if (id.equals("filled_map")) {
            if (data == null)
                data = new SNBT.Compound(null, new ArrayList<>());
            data.add(new SNBT.Int("map", meta == null ? 0 : Integer.parseInt(meta)));
        } else if (id.equals("spawn_egg")) {
            if (data == null)
                id = "creeper_spawn_egg";
            else {
                id = renameEntityId(data.getCompound("EntityTag").getString("id")).substring(10) + "_spawn_egg";
                if (data.getCompound("EntityTag").value.size() == 1) //remove entity tag if it doesn't contain extra information
                    data.remove("EntityTag");
            }
        }

        if (data != null && !data.value.isEmpty()) {
            List<SNBT.Tag> comps = ItemComponents.fromNBT(data, id);
            StringBuilder b = new StringBuilder(renameItemId(id)).append("[");
            comps.forEach(comp -> b.append(comp.name).append("=").append(comp.name(null).build()).append(","));
            b.deleteCharAt(b.length() - 1);
            b.append("]");
            return b.toString();
        }

        return renameItemId(id);
    }

    public static String flattenItemNS(String id, String data) {
        String flattened = flattenItem(id, data, null);
        if (flattened.contains("["))
            flattened = flattened.substring(0, flattened.indexOf('['));
        return flattened;
    }

    public static String renameBlockId(String old) {
        if (Character.isDigit(old.charAt(0))) //flattening is handled in the commands themselves
            return old;
        old = old.toLowerCase();
        if (old.startsWith("minecraft:"))
            old = old.substring(10);
        return "minecraft:" + switch (old) {
            case "grass" -> "short_grass";
            case "grass_path" -> "dirt_path";
            case "sign" -> "oak_sign";
            case "wall_sign" -> "oak_wall_sign";
            case "chain" -> "iron_chain";
            default -> old;
        };
    }

    public static String renameItemId(String old) {
        old = renameBlockId(old).toLowerCase();
        if (old.startsWith("minecraft:"))
            old = old.substring(10);
        if (old.startsWith("record_"))
            return "minecraft:music_disc_" + old.substring(7);
        return "minecraft:" + switch (old) {
            case "scute" -> "turtle_scute";
            case "dandelion_yellow" -> "yellow_dye";
            case "cactus_green" -> "green_dye";
            case "rose_red" -> "red_dye";
            case "totem" -> "totem_of_undying";
            case "wooden_door" -> "oak_door";
            case "reeds" -> "sugar_cane";
            case "melon" -> "melon_slice";
            case "speckled_melon" -> "glistering_melon_slice";
            case "chorus_fruit_popped" -> "popped_chorus_fruit";
            default -> old;
        };
    }

    public static String renameEntityId(String old) {
        if (old.startsWith("minecraft:"))
            old = old.substring(10);
        return "minecraft:" + switch (old) {
            case "AreaEffectCloud" -> "area_effect_cloud";
            case "ArmorStand" -> "armor_stand";
            case "Arrow" -> "arrow";
            case "Bat" -> "bat";
            case "Boat" -> "boat";
            case "ThrownExpBottle", "xp_bottle" -> "experience_bottle";
            case "Blaze" -> "blaze";
            case "CaveSpider" -> "cave_spider";
            case "Chicken" -> "chicken";
            case "Creeper" -> "creeper";
            case "EntityHorse.Donkey" -> "donkey";
            case "DragonFireball" -> "dragon_fireball";
            case "ThrownEgg" -> "egg";
            case "Guardian.Elder" -> "elder_guardian";
            case "EnderCrystal", "ender_crystal" -> "end_crystal";
            case "EnderDragon" -> "ender_dragon";
            case "ThrownEnderpearl" -> "ender_pearl";
            case "Enderman" -> "enderman";
            case "Endermite" -> "endermite";
            case "EyeOfEnderSignal", "eye_of_ender_signal" -> "eye_of_ender";
            case "XPOrb", "xp_orb" -> "experience_orb";
            case "FallingSand" -> "falling_block";
            case "Fireball", "SmallFireball", "small_fireball" -> "fireball"; //are Fireball and SmallFireball the same??
            case "FireworksRocketEntity", "fireworks_rocket" -> "firework_rocket";
            case "Ghast" -> "ghast";
            case "Guardian" -> "guardian";
            case "EntityHorse" -> "horse";
            case "Zombie.Husk" -> "husk";
            case "VillagerGolem", "villager_golem" -> "iron_golem";
            case "ItemFrame" -> "item_frame";
            case "LeashKnot" -> "leash_knot";
            case "LightningBolt" -> "lightning_bolt";
            case "LavaSlime" -> "magma_cube";
            case "MinecartRideable" -> "minecart";
            case "MinecartChest" -> "chest_minecart";
            case "MinecartCommandBlock", "commandblock_minecart" -> "command_block_minecart";
            case "MinecartFurnace" -> "furnace_minecart";
            case "MinecartHopper" -> "hopper_minecart";
            case "MinecartSpawner" -> "spawner_minecart";
            case "MinecartTNT" -> "tnt_minecart";
            case "MushroomCow" -> "mooshroom";
            case "EntityHorse.Mule" -> "mule";
            case "Ozelot" -> "ocelot";
            case "Painting" -> "painting";
            case "Pig" -> "pig";
            case "PigZombie", "zombie_pigman" -> "zombified_piglin";
            case "PolarBear" -> "polar_bear";
            case "Rabbit" -> "rabbit";
            case "Sheep" -> "sheep";
            case "ShulkerBullet" -> "shulker_bullet";
            case "Shulker" -> "shulker";
            case "Silverfish" -> "silverfish";
            case "Skeleton" -> "skeleton";
            case "EntityHorse.Skeleton" -> "skeleton_horse";
            case "Slime" -> "slime";
            case "SnowMan", "snowman" -> "snow_golem";
            case "Snowball" -> "snowball";
            case "SpectralArrow" -> "spectral_arrow";
            case "Spider" -> "spider";
            case "ThrownPotion" -> "potion";
            case "Skeleton.Stray" -> "stray";
            case "Squid" -> "squid";
            case "PrimedTNT" -> "tnt";
            case "Villager" -> "villager";
            case "Witch" -> "witch";
            case "WitherBoss" -> "wither";
            case "Skeleton.Wither" -> "wither_skeleton";
            case "WitherSkull" -> "wither_skull";
            case "Wolf" -> "wolf";
            case "Zombie" -> "zombie";
            case "EntityHorse.Zombie" -> "zombie_horse";
            case "Zombie.Villager" -> "zombie_villager";
            case "evocation_fangs" -> "evoker_fangs";
            case "evocation_illager" -> "evoker";
            case "vindication_illager" -> "vindicator";
            case "illusion_illager" -> "illusioner";
            case "Player" -> "player";
            default -> old;
        };
    }

    public static String renameBlockEntityId(String old) {
        if (old.startsWith("minecraft:"))
            old = old.substring(10);
        return "minecraft:" + switch (old) {
            case "Beacon" -> "beacon";
            case "Cauldron" -> "brewing_stand";
            case "Control" -> "command_block";
            case "DLDetector" -> "daylight_detector";
            case "Trap" -> "dispenser";
            case "EnchantTable" -> "enchanting_table";
            case "EndGateway" -> "end_gateway";
            case "AirPortal" -> "end_portal";
            case "EnderChest" -> "ender_chest";
            case "FlowerPot" -> "flower_pot";
            case "RecordPlayer" -> "jukebox";
            case "MobSpawner" -> "mob_spawner";
            case "Music" -> "note_block";
            case "Structure" -> "structure_block";
            default -> old;
        };
    }

    public static String renameEnchantmentId(String old) {
        old = old.toLowerCase();
        if (old.startsWith("minecraft:"))
            old = old.substring(10);
        return "minecraft:" + switch (old) {
            case "0" -> "protection";
            case "1" -> "fire_protection";
            case "2" -> "feather_falling";
            case "3" -> "blast_protection";
            case "4" -> "projectile_protection";
            case "5" -> "respiration";
            case "6" -> "aqua_affinity";
            case "7" -> "thorns";
            case "8" -> "depth_strider";
            case "9" -> "frost_walker";
            case "10" -> "binding_curse";
            case "16" -> "sharpness";
            case "17" -> "smite";
            case "18" -> "bane_of_arthropods";
            case "19" -> "knockback";
            case "20" -> "fire_aspect";
            case "21" -> "looting";
            case "22", "sweeping" -> "sweeping_edge";
            case "32" -> "efficiency";
            case "33" -> "silk_touch";
            case "34" -> "unbreaking";
            case "35" -> "fortune";
            case "48" -> "power";
            case "49" -> "punch";
            case "50" -> "flame";
            case "51" -> "infinity";
            case "61" -> "luck_of_the_sea";
            case "62" -> "lure";
            case "70" -> "mending";
            case "71" -> "vanishing_curse";
            default -> old;
        };
    }

    public static String renameEffectId(String old) {
        old = old.toLowerCase();
        if (old.startsWith("minecraft:"))
            old = old.substring(10);
        return "minecraft:" + switch (old) {
            case "1" -> "speed";
            case "2" -> "slowness";
            case "3" -> "haste";
            case "4" -> "mining_fatigue";
            case "5" -> "strength";
            case "6" -> "instant_health";
            case "7" -> "instant_damage";
            case "8" -> "jump_boost";
            case "9" -> "nausea";
            case "10" -> "regeneration";
            case "11" -> "resistance";
            case "12" -> "fire_resistance";
            case "13" -> "water_breathing";
            case "14" -> "invisibility";
            case "15" -> "blindness";
            case "16" -> "night_vision";
            case "17" -> "hunger";
            case "18" -> "weakness";
            case "19" -> "poison";
            case "20" -> "wither";
            case "21" -> "health_boost";
            case "22" -> "absorption";
            case "23" -> "saturation";
            case "24" -> "glowing";
            case "25" -> "levitation";
            case "26" -> "luck";
            case "27" -> "unluck";
            default -> old;
        };
    }

    public static String renameGamemode(String gm) {
        gm = gm.toLowerCase();
        return switch (gm) {
            case "0", "s" -> "survival";
            case "1", "c" -> "creative";
            case "2", "a" -> "adventure";
            case "3", "sp" -> "spectator";
            default -> gm;
        };
    }

    public static String renameStructure(String old) {
        old = old.toLowerCase();
        if (old.endsWith("endcity"))
            old = "end_city";
        return old;
    }

    public static String renameParticle(String old) {
        old = old.toLowerCase();
        if (old.startsWith("minecraft:"))
            old = old.substring(10);
        return "minecraft:" + switch (old) {
            case "angryvillager" -> "angry_villager";
            case "blockdust", "blockcrack" -> "block";
            case "damageindicator" -> "damage_indicator";
            case "dragonbreath" -> "dragon_breath";
            case "driplava" -> "dripping_lava";
            case "dripwater" -> "dripping_water";
            case "droplet" -> "rain";
            case "enchantmenttable" -> "enchant";
            case "endrod" -> "end_rod";
            case "explode", "snowshovel" -> "poof";
            case "fallingdust" -> "falling_dust";
            case "fireworksspark" -> "firework";
            case "happyvillager" -> "happy_villager";
            case "hugeexplosion" -> "explosion_emitter";
            case "iconcrack" -> "item";
            case "instantspell" -> "instant_effect";
            case "largeexplosion" -> "explosion";
            case "largesmoke" -> "large_smoke";
            case "magiccrit" -> "enchanted_hit";
            case "mobappearance" -> "elder_guardian";
            case "mobspell", "mobspellambient", "ambient_entity_effect" -> "entity_effect";
            case "reddust" -> "dust";
            case "slime" -> "item_slime";
            case "snowballpoof" -> "item_snowball";
            case "spell" -> "effect";
            case "suspended" -> "underwater";
            case "sweepattack" -> "sweep_attack";
            case "totem" -> "totem_of_undying";
            case "townaura" -> "mycelium";
            case "wake" -> "fishing";
            case "witchmagic" -> "witch";
            default -> old;
        };
    }

    public static String renameSound(String old) {
        if (old.startsWith("minecraft:"))
            old = old.substring(10);
        old = renamePre1_9Sound(old);
        if (old.startsWith("mob."))
            old = "entity." + old.substring(4);
        if (old.startsWith("entity.parrot.imitate.")) {
            String s = "entity.parrot.imitate.";
            return "minecraft:" + switch (old.substring(22)) {
                case "enderdragon" -> s + "ender_dragon";
                case "evocation_illager" -> s + "evoker";
                case "illusion_illager" -> s + "illusioner";
                case "magmacube" -> s + "magma_cube";
                case "vindication_illager" -> s + "vindicator";
                default -> old;
            };
        }
        if (old.equals("entity.player.splash.highspeed"))
            return "minecraft:entity.player.splash.high_speed";
        if (old.equals("entity.polar_bear.baby_ambient"))
            return "minecraft:entity.polar_bear.ambient_baby";
        if (old.startsWith("entity.small_magmacube."))
            return "minecraft:entity.magma_cube." + old.substring(23) + "_small";
        if (old.startsWith("entity.small_slime."))
            return "minecraft:entity.slime." + old.substring(19) + "_small";
        if (old.equals("entity.zombie.attack_door_wood"))
            return "minecraft:entity.zombie.attack_wooden_door";
        if (old.equals("entity.zombie.break_door_wood"))
            return "minecraft:entity.zombie.break_wooden_door";
        if (old.startsWith("record.") || old.startsWith("records."))
            return "minecraft:music_disc." + old.substring(7);
        String[] split = old.split("\\.");
        if (split.length != 3)
            return "minecraft:" + old;
        String middle = switch (split[1]) {
            case "cloth" -> "wool";
            case "enderchest" -> "ender_chest";
            case "metal_pressureplate" -> "metal_pressure_plate";
            case "note" -> "note_block";
            case "slime" -> "slime_block";
            case "stone_pressureplate" -> "stone_pressure_plate";
            case "waterlily" -> "lily_pad";
            case "wood_pressureplate" -> "wooden_pressure_plate";
            case "wood_button" -> "wooden_button";
            case "armorstand" -> "armor_stand";
            case "bobber" -> "fishing_bobber";
            case "enderdragon" -> "ender_dragon";
            case "enderdragon_fireball" -> "dragon_fireball";
            case "endereye" -> "ender_eye";
            case "endermen" -> "enderman";
            case "enderpearl" -> "ender_pearl";
            case "evocation_fangs" -> "evoker_fangs";
            case "evocation_illager" -> "evoker";
            case "firework" -> "firework_rocket";
            case "illusion_illager" -> "illusioner";
            case "irongolem" -> "iron_golem";
            case "itemframe" -> "item_frame";
            case "leashknot" -> "leash_knot";
            case "lightning" -> "lightning_bolt";
            case "lingeringpotion" -> "lingering_potion";
            case "magmacube" -> "magma_cube";
            case "snowman" -> "snow_golem";
            case "vindication_illager" -> "vindicator";
            case "zombie_pig", "zombie_pigman" -> "zombified_piglin";
            default -> split[1];
        };
        old = split[0] + "." + middle + "." + split[2];

        //add new renames into here, upstairs is a mess
        switch (old) {
            case "block.sand.wind" -> old = "block.dry_grass.ambient";
            case "entity.leash_knot.break" -> old = "item.lead.break"; //could also be item.lead.untied but whatever
            case "entity.leash_knot.place" -> old = "item.lead.tied";
        }

        return "minecraft:" + old;
    }

    private static String renamePre1_9Sound(String old) {
        //if you ever feel chaotic and stupid and unnecessary and inconsistent, remember that minecraft had these sound names once
        return switch (old) {
            case "ambient.cave.cave" -> "ambient.cave";
            case "ambient.weather.rain" -> "weather.rain";
            case "ambient.weather.thunder" -> "entity.lightning_bolt.thunder";
            case "game.player.hurt.fall.big" -> "entity.player.big_fall";
            case "game.player.hurt.fall.small" -> "entity.player.small_fall";
            case "game.neutral.hurt.fall.big" -> "entity.generic.big_fall";
            case "game.neutral.hurt.fall.small" -> "entity.generic.small_fall";
            case "game.hostile.hurt.fall.big" -> "entity.hostile.big_fall";
            case "game.hostile.hurt.fall.small" -> "entity.hostile.small_fall";
            case "game.player.hurt" -> "entity.player.hurt";
            case "game.neutral.hurt" -> "entity.generic.hurt";
            case "game.hostile.hurt" -> "entity.hostile.hurt";
            case "game.player.die" -> "entity.player.death";
            case "game.neutral.die" -> "entity.generic.death";
            case "game.hostile.die" -> "entity.hostile.death";
            case "dig.cloth" -> "block.wool.break";
            case "dig.grass" -> "block.grass.break";
            case "dig.gravel" -> "block.gravel.break";
            case "dig.sand" -> "block.sand.break";
            case "dig.snow" -> "block.snow.break";
            case "dig.stone" -> "block.stone.break";
            case "dig.wood" -> "block.wood.break";
            case "fire.fire" -> "block.fire.ambient";
            case "fire.ignite" -> "item.flintandsteel.use";
            case "item.fireCharge.use" -> "item.firecharge.use";
            case "fireworks.blast" -> "entity.firework_rocket.blast";
            case "fireworks.blast_far" -> "entity.firework_rocket.blast_far";
            case "fireworks.largeBlast" -> "entity.firework_rocket.large_blast";
            case "fireworks.largeBlast_far" -> "entity.firework_rocket.large_blast_far";
            case "fireworks.launch" -> "entity.firework_rocket.launch";
            case "fireworks.twinkle" -> "entity.firework_rocket.twinkle";
            case "fireworks.twinkle_far" -> "entity.firework_rocket.twinkle_far";
            case "liquid.lava" -> "block.lava.ambient";
            case "liquid.lavapop" -> "block.lava.pop";
            case "game.neutral.swim.splash" -> "entity.generic.splash";
            case "game.player.swim.splash" -> "entity.player.splash";
            case "game.hostile.swim.splash" -> "entity.hostile.splash";
            case "liquid.water" -> "block.water.ambient";
            case "minecart.base" -> "entity.minecart.riding";
            case "minecart.inside" -> "entity.minecart.inside";
            case "mob.bat.idle" -> "entity.bat.ambient";
            case "mob.blaze.breathe" -> "entity.blaze.ambient";
            case "mob.blaze.hit" -> "entity.blaze.hurt";
            case "mob.guardian.hit" -> "entity.guardian.hurt";
            case "mob.guardian.idle" -> "entity.guardian.ambient";
            case "mob.guardian.elder.hit" -> "entity.elder_guardian.hurt";
            case "mob.guardian.elder.idle" -> "entity.elder_guardian.ambient";
            case "mob.guardian.elder.death" -> "entity.elder_guardian.death";
            case "mob.guardian.land.hit" -> "entity.guardian.hurt_land";
            case "mob.guardian.land.idle" -> "entity.guardian.ambient_land";
            case "mob.guardian.land.death" -> "entity.guardian.death_land";
            case "mob.guardian.curse" -> "entity.elder_guardian.curse";
            case "mob.cat.hitt" -> "entity.cat.hurt"; //seems weird but the wiki says hitt
            case "mob.chicken.plop" -> "entity.chicken.egg";
            case "mob.cow.say" -> "entity.cow.ambient";
            case "mob.creeper.say" -> "entity.creeper.hurt";
            case "mob.enderdragon.hit" -> "entity.ender_dragon.hurt";
            case "mob.enderdragon.wings" -> "entity.ender_dragon.flap";
            case "mob.endermen.hit" -> "entity.enderman.hurt";
            case "mob.endermen.idle" -> "entity.enderman.ambient";
            case "mob.endermen.portal" -> "entity.enderman.teleport";
            case "mob.ghast.affectionate_scream" -> "entity.ghast.scream";
            case "mob.ghast.charge" -> "entity.ghast.warn";
            case "mob.ghast.fireball" -> "entity.ghast.shoot";
            case "mob.ghast.moan" -> "entity.ghast.ambient";
            case "mob.ghast.scream" -> "entity.ghast.hurt";
            case "mob.horse.donkey.angry" -> "entity.donkey.angry";
            case "mob.horse.donkey.death" -> "entity.donkey.death";
            case "mob.horse.donkey.hit" -> "entity.donkey.hurt";
            case "mob.horse.donkey.idle" -> "entity.donkey.ambient";
            case "mob.horse.hit" -> "entity.horse.hurt";
            case "mob.horse.idle" -> "entity.horse.ambient";
            case "mob.horse.leather" -> "entity.horse.saddle";
            case "mob.horse.skeleton.death" -> "entity.skeleton_horse.death";
            case "mob.horse.skeleton.hit" -> "entity.skeleton_horse.hurt";
            case "mob.horse.skeleton.idle" -> "entity.skeleton_horse.ambient";
            case "mob.horse.soft" -> "entity.horse.step";
            case "mob.horse.wood" -> "entity.horse.step_wood";
            case "mob.horse.zombie.death" -> "entity.zombie_horse.death";
            case "mob.horse.zombie.hit" -> "entity.zombie_horse.hurt";
            case "mob.horse.zombie.idle" -> "entity.zombie_horse.ambient";
            case "mob.irongolem.hit" -> "entity.iron_golem.hurt";
            case "mob.irongolem.throw" -> "entity.iron_golem.attack";
            case "mob.irongolem.walk" -> "entity.iron_golem.step";
            case "mob.magmacube.big" -> "entity.magma_cube.hurt";
            case "mob.magmacube.small" -> "entity.magma_cube.hurt_small";
            case "mob.pig.say" -> "entity.pig.ambient";
            case "mob.rabbit.idle" -> "entity.rabbit.ambient";
            case "mob.rabbit.hop" -> "entity.rabbit.jump";
            case "mob.sheep.say" -> "entity.sheep.ambient";
            case "mob.silverfish.hit" -> "entity.silverfish.hurt";
            case "mob.silverfish.kill" -> "entity.silverfish.death";
            case "mob.silverfish.say" -> "entity.silverfish.ambient";
            case "mob.skeleton.say" -> "entity.skeleton.ambient";
            case "mob.slime.big" -> "entity.slime.hurt";
            case "mob.slime.small" -> "entity.slime.hurt_small";
            case "mob.spider.say" -> "entity.spider.ambient";
            case "mob.villager.haggle" -> "entity.villager.trade";
            case "mob.villager.hit" -> "entity.villager.hurt";
            case "mob.villager.idle" -> "entity.villager.ambient";
            case "mob.wither.idle" -> "entity.wither.ambient";
            case "mob.wolf.bark", "mob.wolf.howl" -> "entity.wolf.ambient"; //mob.wolf.howl has been removed?
            case "mob.zombie.metal" -> "entity.zombie.attack_iron_door";
            case "mob.zombie.remedy" -> "entity.zombie_villager.cure";
            case "mob.zombie.say" -> "entity.zombie.ambient";
            case "mob.zombie.unfect" -> "entity.zombie_villager.converted";
            case "mob.zombie.wood" -> "entity.zombie.attack_wooden_door";
            case "mob.zombie.woodbreak" -> "entity.zombie.break_wooden_door";
            case "mob.zombiepig.zpig" -> "entity.zombified_piglin.ambient";
            case "mob.zombiepig.zpigangry" -> "entity.zombified_piglin.angry";
            case "mob.zombiepig.zpigdeath" -> "entity.zombified_piglin.death";
            case "mob.zombiepig.zpighurt" -> "entity.zombified_piglin.hurt";
            case "note.bass", "note.bassattack" -> "block.note_block.bass";
            case "note.bd" -> "block.note_block.basedrum";
            case "note.harp" -> "block.note_block.harp";
            case "note.hat" -> "block.note_block.hat";
            case "note.pling" -> "block.note_block.pling";
            case "note.snare" -> "block.note_block.snare";
            case "portal.portal" -> "block.portal.ambient";
            case "portal.travel" -> "block.portal.travel";
            case "portal.trigger" -> "block.portal.trigger";
            case "random.anvil_break" -> "block.anvil.break";
            case "random.anvil_land" -> "block.anvil.land";
            case "random.anvil_use" -> "block.anvil.use";
            case "random.bow" -> "entity.arrow.shoot";
            case "random.bowhit" -> "entity.arrow.hit";
            case "random.break" -> "entity.item.break";
            case "random.burp" -> "entity.player.burp";
            case "random.chestclosed" -> "block.chest.close";
            case "random.chestopen" -> "block.chest.open";
            case "gui.button.press" -> "ui.button.click";
            case "random.click" -> "block.dispenser.dispense";
            case "random.door_close" -> "block.wooden_door.close";
            case "random.door_open" -> "block.wooden_door.open";
            case "random.drink" -> "entity.generic.drink";
            case "random.eat" -> "entity.generic.eat";
            case "random.explode" -> "entity.generic.explode";
            case "random.fizz" -> "block.fire.extinguish";
            case "game.tnt.primed" -> "entity.tnt.primed";
            case "creeper.primed" -> "entity.creeper.primed";
            case "dig.glass" -> "block.glass.break";
            case "game.potion.smash" -> "entity.splash_potion.break";
            case "random.levelup" -> "entity.player.levelup";
            case "random.orb" -> "entity.experience_orb.pickup";
            case "random.pop" -> "entity.item.pickup";
            case "random.splash" -> "entity.fishing_bobber.splash";
            case "random.successful_hit" -> "entity.arrow.hit_player";
            case "random.wood_click" -> "block.wooden_pressure_plate.click_on";
            case "step.cloth" -> "block.wool.step";
            case "step.grass" -> "block.grass.step";
            case "step.gravel" -> "block.gravel.step";
            case "step.ladder" -> "block.ladder.step";
            case "step.sand" -> "block.sand.step";
            case "step.snow" -> "block.snow.step";
            case "step.stone" -> "block.stone.step";
            case "step.wood" -> "block.wood.step";
            case "tile.piston.in" -> "block.piston.contract";
            case "tile.piston.out" -> "block.piston.extend";
            case "music.game.creative" -> "music.creative";
            case "music.game.end" -> "music.end";
            case "music.game.end.dragon" -> "music.dragon";
            case "music.game.end.credits" -> "music.credits";
            case "music.game.nether" -> "music.nether.nether_wastes";
            default -> old;
        };
    }

    public static String renameBannerColor(String old) {
        return switch (old) {
            case "0" -> "white";
            case "1" -> "orange";
            case "2" -> "magenta";
            case "3" -> "light_blue";
            case "4" -> "yellow";
            case "5" -> "lime";
            case "6" -> "pink";
            case "7" -> "gray";
            case "8" -> "light_gray";
            case "9" -> "cyan";
            case "10" -> "purple";
            case "11" -> "blue";
            case "12" -> "brown";
            case "13" -> "green";
            case "14" -> "red";
            case "15" -> "black";
            default -> old;
        };
    }

    public static String renameBannerPattern(String old) {
        if (old.startsWith("minecraft:"))
            old = old.substring(10);
        return "minecraft:" + switch (old) {
            case "b" -> "base";
            case "bs" -> "stripe_bottom";
            case "ts" -> "stripe_top";
            case "ls" -> "stripe_left";
            case "rs" -> "stripe_right";
            case "cs" -> "stripe_center";
            case "ms" -> "stripe_middle";
            case "drs" -> "stripe_downright";
            case "dls" -> "stripe_downleft";
            case "ss" -> "small_stripes";
            case "cr" -> "cross";
            case "sc" -> "straight_cross";
            case "ld" -> "diagonal_left";
            case "rud" -> "diagonal_right";
            case "lud" -> "diagonal_up_left";
            case "rd" -> "diagonal_up_right";
            case "vh" -> "half_vertical";
            case "vhr" -> "half_vertical_right";
            case "hh" -> "half_horizontal";
            case "hhb" -> "half_horizontal_bottom";
            case "bl" -> "square_bottom_left";
            case "br" -> "square_bottom_right";
            case "tl" -> "square_top_left";
            case "tr" -> "square_top_right";
            case "bt" -> "triangle_bottom";
            case "tt" -> "triangle_top";
            case "bts" -> "triangles_bottom";
            case "tts" -> "triangles_top";
            case "mc" -> "circle";
            case "mr" -> "rhombus";
            case "bo" -> "border";
            case "cbo" -> "curly_border";
            case "bri" -> "bricks";
            case "gra" -> "gradient";
            case "gru" -> "gradient_up";
            case "cre" -> "creeper";
            case "sku" -> "skull";
            case "flo" -> "flower";
            case "moj" -> "mojang";
            case "glb" -> "globe";
            case "pig" -> "piglin";
            case "flw" -> "flow";
            case "gus" -> "guster";
            default -> old;
        };
    }

    public static String renamePaintingId(String old) {
        if (old.startsWith("minecraft:"))
            old = old.substring(10);
        old = old.toLowerCase();
        return "minecraft:" + switch (old) {
            case "skullandroses" -> "skull_and_roses";
            case "donkeykong" -> "donkey_kong";
            case "burningskull" -> "burning_skull";
            default -> old;
        };
    }

    public static String renameCatVariant(int old) {
        return "minecraft:" + switch (old) {
            case 0 -> "white";
            case 1 -> "black";
            case 2 -> "red";
            case 3 -> "siamese";
            case 4 -> "british_shorthair";
            case 5 -> "calico";
            case 6 -> "persian";
            case 7 -> "ragdoll";
            case 8 -> "tabby";
            case 9 -> "all_black";
            default -> "jellie"; //:)
        };
    }

    public static String renameAttribute(String attribute) {
        if (attribute.startsWith("minecraft:"))
            attribute = attribute.substring(10);
        //removed the generic., player., etc. prefixes
        if (attribute.contains("."))
            attribute = attribute.split("\\.")[1];

        return "minecraft:" + switch (attribute) {
            case "maxHealth" -> "max_health";
            case "spawnReinforcements" -> "spawn_reinforcements";
            case "jumpStrength" -> "jump_strength";
            case "followRange" -> "follow_range";
            case "knockbackResistance" -> "knockback_resistance";
            case "movementSpeed" -> "movement_speed";
            case "flyingSpeed" -> "flying_speed";
            case "attackDamage" -> "attack_damage";
            case "attackKnockback" -> "attack_knockback";
            case "attackSpeed" -> "attack_speed";
            case "armorToughness" -> "armor_toughness";
            default -> attribute;
        };
    }

    public static String renameDifficulty(String old) {
        return switch (old) {
            case "0", "p" -> "peaceful";
            case "1", "e" -> "easy";
            case "2", "n" -> "normal";
            case "3", "h" -> "hard";
            default -> old;
        };
    }

    public static String renameVillagerProfession(int profession, int career) {
        return "minecraft:" + switch (profession) {
            case 0 -> switch (career) {
                default -> "farmer";
                case 2 -> "fisherman";
                case 3 -> "shepherd";
                case 4 -> "fletcher";
            };
            case 1 -> career == 2 ? "cartographer" : "librarian";
            case 2 -> "cleric";
            case 3 -> switch (career) {
                default -> "armorer";
                case 2 -> "weaponsmith";
                case 3 -> "toolsmith";
            };
            case 4 -> career == 2 ? "leatherworker" : "butcher";
            default -> "nitwit";
        };
    }

    private static final Map<Integer, String> blockIds = new HashMap<>();
    private static final Map<String, Map<Integer, String>> blockData = new HashMap<>();
    private static final Map<String, FlatState> blockStates = new HashMap<>();
    private static final Map<Integer, String> itemIds = new HashMap<>();
    private static final Map<String, Map<Integer, String>> itemData = new HashMap<>();
    public static final List<String> retainDamage = new ArrayList<>();

    public static void prepareFlatteningData() {

        if (blockIds.isEmpty()) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(LegacyData.class.getResourceAsStream("flattening/block_ids.txt")))) {
                while (true) {
                    String s = reader.readLine();
                    if (s == null)
                        break;
                    String[] split = s.split(" ");
                    blockIds.put(Integer.parseInt(split[0]), split[1]);
                }
            } catch (Exception e) {
                System.out.println("Failed to load data 'flattening/block_ids.txt'");
                e.printStackTrace();
                System.exit(-1);
            }
        }

        if (blockData.isEmpty()) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(LegacyData.class.getResourceAsStream("flattening/block_data_values.txt")))) {
                while (true) {
                    String block = reader.readLine();
                    if (block == null)
                        break;
                    Map<Integer, String> states = new HashMap<>();
                    while (true) {
                        String state = reader.readLine();
                        if (state.startsWith("-"))
                            break;
                        if (state.startsWith(">")) {
                            state = state.substring(1);
                            for (int i = 0; i <= 15; i++)
                                states.put(i, state);
                            reader.readLine();
                            break;
                        }
                        String[] split = state.split(" ");
                        states.put(Integer.parseInt(split[0]), split[1]);
                    }
                    if (block.contains(",")) {
                        for (String sp : block.split(","))
                            blockData.put(sp, states);
                    } else
                        blockData.put(block, states);
                }
            } catch (Exception e) {
                System.out.println("Failed to load data 'flattening/block_data_values.txt'");
                e.printStackTrace();
                System.exit(-1);
            }
        }

        if (blockStates.isEmpty()) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(LegacyData.class.getResourceAsStream("flattening/block_states.txt")))) {
                while (true) {
                    String block = reader.readLine();
                    if (block == null)
                        break;
                    String[] bl = block.split(" ");
                    FlatState flat = new FlatState(bl[1], bl.length == 3);
                    while (true) {
                        String act = reader.readLine();
                        if (act.startsWith("-"))
                            break;
                        flat.addAction(parseAction(act, true));
                    }
                    blockStates.put(bl[0], flat);
                }
            } catch (Exception e) {
                System.out.println("Failed to load data 'flattening/block_states.txt'");
                e.printStackTrace();
                System.exit(-1);
            }
        }

        if (itemIds.isEmpty()) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(LegacyData.class.getResourceAsStream("flattening/item_ids.txt")))) {
                while (true) {
                    String s = reader.readLine();
                    if (s == null)
                        break;
                    String[] split = s.split(" ");
                    itemIds.put(Integer.parseInt(split[0]), split[1]);
                }
            } catch (Exception e) {
                System.out.println("Failed to load data 'flattening/item_ids.txt'");
                e.printStackTrace();
                System.exit(-1);
            }
        }

        if (itemData.isEmpty()) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(LegacyData.class.getResourceAsStream("flattening/item_data_values.txt")))) {
                while (true) {
                    String item = reader.readLine();
                    if (item == null)
                        break;
                    Map<Integer, String> values = new HashMap<>();
                    while (true) {
                        String data = reader.readLine();
                        if (data.startsWith("-"))
                            break;
                        String[] split = data.split(" ");
                        if (split.length == 3)
                            retainDamage.add("minecraft:" + split[1]);
                        values.put(Integer.parseInt(split[0]), split[1]);
                    }
                    itemData.put(item, values);
                }
            } catch (Exception e) {
                System.out.println("Failed to load data 'flattening/item_data_values.txt");
                e.printStackTrace();
                System.exit(-1);
            }
        }
    }

    private static FlatState.Action parseAction(String in, boolean top) {
        if (in.startsWith("#"))
            return new FlatState.RenameAction(in.substring(1));
        if (in.startsWith("/"))
            return new FlatState.CutAction(in.substring(1));
        if (top || in.startsWith("?")) {
            String[] split = in.split(" ");
            List<FlatState.Action> actions = new ArrayList<>();
            for (int i = 1; i < split.length; i++)
                actions.add(parseAction(split[i], false));
            if (in.startsWith("?"))
                return new FlatState.FallbackAction(actions);
            return new FlatState.CompareAction(split[0], actions);
        }
        return new FlatState.AddStateAction(in);
    }

    private static class FlatState {
        private final String renameTo;
        private final boolean blockEntity;
        private final List<Action> actions;

        public FlatState(String renameTo, boolean blockEntity) {
            this.renameTo = renameTo;
            this.blockEntity = blockEntity;
            this.actions = new ArrayList<>();
        }

        public void addAction(Action action) {
            actions.add(action);
        }

        public String flatten(String states, SNBT.Compound data, boolean block) {
            StateWrapper wrapper = new StateWrapper(renameTo, states == null ? "" : states);
            for (Action action : actions)
                action.perform(wrapper);

            if (blockEntity && data != null) {
                switch (wrapper.name) {
                    case "note_block" -> {
                        if (data.contains("note")) {
                            int note = data.getInt("note");
                            data.remove("note");
                            wrapper.states.put("note", "" + note);
                        }
                        if (data.contains("powered")) {
                            wrapper.states.put("powered", data.getInt("powered") == 1 ? "true" : "false");
                            data.remove("powered");
                        }
                    }
                    case "bed" -> {
                        if (!data.contains("color")) {
                            wrapper.name = "red_bed";
                        } else {
                            int color = data.getInt("color");
                            data.remove("color");
                            wrapper.name = renameBannerColor("" + color) + "_bed";
                        }
                    }
                    case "flower_pot" -> {
                        if (data.contains("Item")) {
                            String item;
                            if (data.get("Item") instanceof SNBT.Strings s)
                                item = s.value;
                            else
                                item = flattenItemNS("" + data.getInt("Item"), null);
                            data.remove("Item");
                            if (item.startsWith("minecraft:"))
                                item = item.substring(10);
                            switch (item) {
                                case "sapling" -> {
                                    if (!data.contains("Data"))
                                        wrapper.name = "potted_oak_sapling";
                                    else {
                                        int dv = data.getInt("Data");
                                        data.remove("Data");
                                        wrapper.name = "potted_" + switch (dv) {
                                            case 1 -> "spruce";
                                            case 2 -> "birch";
                                            case 3 -> "jungle";
                                            case 4 -> "acacia";
                                            case 5 -> "dark_oak";
                                            default -> "oak";
                                        } + "_sapling";
                                    }
                                }
                                case "tallgrass" -> {
                                    if (!data.contains("Data"))
                                        wrapper.name = "potted_dead_bush";
                                    else {
                                        int dv = data.getInt("Data");
                                        data.remove("Data");
                                        wrapper.name = "potted_" + switch (dv) {
                                            case 1 -> "cactus"; //potted short grass doesn't exist anymore
                                            case 2 -> "fern";
                                            default -> "dead_bush";
                                        };
                                    }
                                }
                                case "deadbush" -> wrapper.name = "potted_dead_bush";
                                case "yellow_flower" -> wrapper.name = "potted_dandelion";
                                case "red_flower" -> {
                                    if (!data.contains("Data"))
                                        wrapper.name = "potted_poppy";
                                    else {
                                        int dv = data.getInt("Data");
                                        data.remove("Data");
                                        wrapper.name = "potted_" + switch (dv) {
                                            case 1 -> "blue_orchid";
                                            case 2 -> "allium";
                                            case 3 -> "azure_bluet";
                                            case 4 -> "red_tulip";
                                            case 5 -> "orange_tulip";
                                            case 6 -> "white_tulip";
                                            case 7 -> "pink_tulip";
                                            case 8 -> "oxeye_daisy";
                                            default -> "poppy";
                                        };
                                    }
                                }
                                case "brown_mushroom" -> wrapper.name = "potted_brown_mushroom";
                                case "red_mushroom" -> wrapper.name = "potted_red_mushroom";
                                default -> wrapper.name = "potted_cactus"; //everything that's not a valid pot content will become cactus :)
                            }
                        }
                    }
                    case "skull" -> {
                        String type;
                        if (!data.contains("SkullType"))
                            type = "skeleton";
                        else {
                            type = switch (data.getInt("SkullType")) {
                                case 1 -> "wither_skeleton";
                                case 2 -> "zombie";
                                case 3 -> "player";
                                case 4 -> "creeper";
                                case 5 -> "dragon";
                                default -> "skeleton";
                            };
                            data.remove("SkullType");
                        }
                        String hs = type.contains("skeleton") ? "_skull" : "_head";
                        if (wrapper.states.containsKey("facing")) {
                            wrapper.name = type + "_wall" + hs;
                            data.remove("Rot");
                        } else {
                            wrapper.name = type + hs;
                            int rot = data.contains("Rot") ? data.getInt("Rot") : 0;
                            data.remove("Rot");
                            if (rot < 0)
                                rot += 16;
                            wrapper.states.put("rotation", "" + rot);
                        }
                    }
                    case "standing_banner", "wall_banner" -> {
                        if (wrapper.name.equals("standing_banner"))
                            wrapper.name = "banner";
                        if (!data.contains("Base"))
                            wrapper.name = "white_" + wrapper.name;
                        else {
                            int color = data.getInt("Base");
                            data.remove("Base");
                            wrapper.name = renameBannerColor("" + (15 - color)) + wrapper.name;
                        }
                    }
                }
            }

            wrapper.name = block ? renameBlockId(wrapper.name) : renameItemId(wrapper.name);
            if (data != null && block)
                BlockEntityData.elevateBlockEntity(data, ItemComponents.getBEId(wrapper.name));

            StringBuilder b = new StringBuilder(wrapper.name);
            if (!wrapper.states.isEmpty() && block) {
                b.append("[");
                wrapper.states.forEach((key, value) -> b.append(key).append("=").append(value).append(","));
                b.deleteCharAt(b.length() - 1);
                b.append("]");
            } else if (!block && data != null) {
                List<SNBT.Tag> comps = ItemComponents.fromNBT(data, wrapper.name);
                b.append("[");
                comps.forEach(comp -> b.append(comp.name).append("=").append(comp.build()).append(","));
                b.deleteCharAt(b.length() - 1);
                b.append("]");
            } else if (data != null && !data.value.isEmpty())
                b.append(data.build());
            return b.toString();
        }

        public interface Action {
            void perform(StateWrapper input);
        }

        public static class CompareAction implements Action {
            private final Map<String, String> compareStates;
            private final List<Action> actions;

            public CompareAction(String compareStates, List<Action> actions) {
                if (compareStates.equals("*"))
                    this.compareStates = null;
                else {
                    this.compareStates = new HashMap<>();
                    for (String state : compareStates.split(",")) {
                        String[] sp = state.split("=");
                        this.compareStates.put(sp[0], sp[1]);
                    }
                }
                this.actions = actions;
            }

            public void perform(StateWrapper input) {
                boolean matches = true;
                if (compareStates != null && input.states != null) {
                    for (Map.Entry<String, String> state : compareStates.entrySet()) {
                        if (!input.states.containsKey(state.getKey()) || !input.states.get(state.getKey()).matches(state.getValue())) {
                            matches = false;
                            break;
                        }
                    }
                }
                if (matches && (compareStates == null || input.states != null)) {
                    for (Action action : actions)
                        action.perform(input);
                }
            }
        }

        public static class AddStateAction implements Action {
            private final Couple<String, String> state;

            public AddStateAction(String state) {
                String[] split = state.split("=");
                this.state = new Couple<>(split[0], split[1]);
            }

            public void perform(StateWrapper input) {
                input.states.put(state.first(), state.second());
            }
        }

        public static class RenameAction implements Action {
            private final String renameTo;

            public RenameAction(String renameTo) {
                this.renameTo = renameTo;
            }

            public void perform(StateWrapper input) {
                input.name = renameTo;
            }
        }

        public static class CutAction implements Action {
            private final String toCut;

            public CutAction(String toCut) {
                this.toCut = toCut;
            }

            public void perform(StateWrapper input) {
                if (toCut.equals("*"))
                    input.states.clear();
                else
                    input.states.remove(toCut);
            }
        }

        public static class FallbackAction implements Action {
            private final List<Action> actions;

            public FallbackAction(List<Action> actions) {
                this.actions = actions;
            }

            public void perform(StateWrapper input) {
                if (input.name.equals("?"))
                    for (Action action : actions)
                        action.perform(input);
            }
        }

        private static class StateWrapper {
            public String name;
            public final Map<String, String> states;

            public StateWrapper(String name, String states) {
                this.name = name;
                this.states = new HashMap<>();
                for (String state : states.split(",")) {
                    if (state.contains("=")) {
                        String[] sp = state.split("=");
                        this.states.put(sp[0], sp[1]);
                    }
                }
            }
        }
    }
}
