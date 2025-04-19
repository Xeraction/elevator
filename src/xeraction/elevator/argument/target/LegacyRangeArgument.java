package xeraction.elevator.argument.target;

import xeraction.elevator.StringIterator;

public class LegacyRangeArgument extends SelectorArgument {
    public final String toNew;
    public double value;
    public final boolean max;

    public LegacyRangeArgument(String name, String toNew, boolean max) {
        super(name);
        this.toNew = toNew;
        this.max = max;
    }

    public void parseValue(StringIterator iterator) {
        value = iterator.readNumber();
    }

    public String build() {
        return null;
    }
}
