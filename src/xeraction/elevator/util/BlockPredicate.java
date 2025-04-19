package xeraction.elevator.util;

import xeraction.elevator.StringIterator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockPredicate {
    private String block;
    private Map<String, String> states;
    private SNBT.Compound nbt;

    public BlockPredicate() {}

    public BlockPredicate(StringIterator iterator) {
        parse(iterator);
    }

    public void parse(StringIterator iterator) {
        block = LegacyData.renameBlockId(iterator.readUntilKeep(c -> StringIterator.isOpener(c) || c == ' '));
        if (iterator.hasMore() && iterator.peek() == '[') {
            iterator.next();
            states = new HashMap<>();
            String stateList = iterator.readUntil(']');
            String[] commas = stateList.split(",");
            for (String com : commas) {
                if (com.isBlank())
                    continue;
                int ind = com.indexOf('=');
                states.put(com.substring(0, ind).strip(), com.substring(ind + 1).strip());
            }

            if (block.equals("minecraft:cauldron") && !states.isEmpty())
                block = "minecraft:water_cauldron";
        }
        if (iterator.hasMore() && iterator.peek() == '{') {
            nbt = (SNBT.Compound) SNBT.parseTag(iterator, false);
            BlockEntityData.elevateBlockEntity(nbt, ItemComponents.getBEId(block));
        }
    }

    public String build() {
        StringBuilder builder = new StringBuilder(block);
        if (states != null && !states.isEmpty()) {
            builder.append('[');
            states.forEach((state, value) -> builder.append(state).append('=').append(value).append(','));
            builder.deleteCharAt(builder.length() - 1);
            builder.append(']');
        }
        if (nbt != null)
            builder.append(nbt.build());
        return builder.toString();
    }

    public SNBT.Compound toGeneralNBT() {
        return toNBT("Name", "Properties", "");
    }

    public SNBT.Compound toItemComponentNBT() {
        return toNBT("blocks", "state", "nbt");
    }

    private SNBT.Compound toNBT(String blockName, String stateName, String nbtName) {
        List<SNBT.Tag> tags = new ArrayList<>();
        tags.add(new SNBT.Strings(blockName, block));
        if (nbt != null)
            tags.add(new SNBT.Strings(nbtName, nbt.build()));
        if (states != null && !states.isEmpty()) {
            List<SNBT.Tag> stateTag = new ArrayList<>();
            states.forEach((state, value) -> stateTag.add(new SNBT.Strings(state, value)));
            tags.add(new SNBT.Compound(stateName, stateTag));
        }
        return new SNBT.Compound(null, tags);
    }
}
