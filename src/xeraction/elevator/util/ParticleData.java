package xeraction.elevator.util;

public class ParticleData {
    public static void elevateParticle(SNBT.Compound tag, String type) {
        type = type != null ? type : tag.getString("type");
        type = LegacyData.renameParticle(type);
        tag.set("type", type);
        if (type.startsWith("minecraft:"))
            type = type.substring(10);
        switch (type) {
            case "item" -> {
                tag.rename("value", "item");
                if (tag.get("item") instanceof SNBT.Compound c)
                    ItemComponents.elevateItem(c);
            }
            case "block", "block_crumble", "block_marker", "dust_pillar", "falling_dust" -> tag.rename("value", "block_state");
            case "entity_effect" -> tag.rename("value", "color");
            case "dust_color_transition" -> {
                tag.rename("fromColor", "from_color");
                tag.rename("toColor", "to_color");
            }
            case "trail" -> tag.add(new SNBT.Int("duration", 20)); //TODO find the real default for this
        }
    }
}
