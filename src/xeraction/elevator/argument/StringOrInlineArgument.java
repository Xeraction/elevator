package xeraction.elevator.argument;

import xeraction.elevator.StringIterator;
import xeraction.elevator.util.SNBT;

public class StringOrInlineArgument implements Argument {
    SNBT.Tag tag = null;
    String string = null;

    //TODO add support for inline definitions of dialogs, features, loot modifiers, loot predicates, loot tables, slot sources, and number providers
    // only do this when i have nothing better to do tho because it will be a fuck ton of work
    // thanks mojang

    public boolean parse(StringIterator iterator) {
        char c = iterator.peek();
        if (c == '{' || c == '[')
            tag = SNBT.parseTag(iterator, false);
        else
            string = iterator.readWord();
        return true;
    }

    public String build() {
        if (tag != null)
            return tag.build();
        else
            return string;
    }
}
