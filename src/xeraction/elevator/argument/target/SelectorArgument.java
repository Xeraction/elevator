package xeraction.elevator.argument.target;

import xeraction.elevator.StringIterator;

public abstract class SelectorArgument {
    public final String name;

    protected SelectorArgument(String name) {
        this.name = name;
    }

    public abstract void parseValue(StringIterator iterator);
    public abstract String build();
}
