package xeraction.elevator.argument.target;

import xeraction.elevator.StringIterator;
import xeraction.elevator.util.SNBT;

public class NbtArgument extends SelectorArgument {
    private SNBT.Compound nbt;
    private boolean negated = false;

    public NbtArgument() {
        super("nbt");
    }

    public void parseValue(StringIterator iterator) {
        if (iterator.peekSkip() == '!') {
            negated = true;
            iterator.nextSkip();
        }
        nbt = SNBT.parse(iterator);
    }

    public String build() {
        return name + "=" + (negated ? "!" : "") + nbt.build();
    }

    public SNBT.Compound nbt() {
        return nbt;
    }
}
