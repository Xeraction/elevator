package xeraction.elevator.argument;

import xeraction.elevator.StringIterator;
import xeraction.elevator.util.LegacyData;

public class SlotArgument extends StringOrInlineArgument {
    public boolean parse(StringIterator iterator) {
        boolean b = super.parse(iterator);
        if (string != null)
            string = LegacyData.renameSlot(string);

        return b;
    }
}
