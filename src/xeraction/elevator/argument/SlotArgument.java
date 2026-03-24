package xeraction.elevator.argument;

import xeraction.elevator.StringIterator;
import xeraction.elevator.util.LegacyData;

public class SlotArgument implements Argument {
    private String slot;

    public boolean parse(StringIterator iterator) {
        slot = LegacyData.renameSlot(iterator.readWord());
        return true;
    }

    public String slot() {
        return slot;
    }
}
