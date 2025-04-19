package xeraction.elevator.argument;

import xeraction.elevator.StringIterator;
import xeraction.elevator.util.SNBT;
import xeraction.elevator.util.TextComponent;

public class TextComponentArgument implements Argument {
    private SNBT.Tag tag; //text components can be compound, list, or simple string tags (snbt parser can parse legacy json as they're very similar)

    public boolean parse(StringIterator iterator) {
        tag = SNBT.parseTag(iterator, false);
        TextComponent.elevateCMD(tag);
        return true;
    }

    public SNBT.Tag tag() {
        return tag;
    }
}
