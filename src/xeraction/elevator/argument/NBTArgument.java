package xeraction.elevator.argument;

import xeraction.elevator.StringIterator;
import xeraction.elevator.util.SNBT;

public class NBTArgument implements Argument {
    private SNBT.Tag tag;

    public boolean parse(StringIterator iterator) {
        tag = SNBT.parseTag(iterator, false);
        return true;
    }

    public SNBT.Tag tag() {
        return tag;
    }
}
