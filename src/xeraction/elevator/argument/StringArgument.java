package xeraction.elevator.argument;

import xeraction.elevator.StringIterator;

public class StringArgument implements Argument {
    private final boolean greedy;
    private String value;

    public StringArgument() {
        greedy = false;
    }

    public StringArgument(boolean greedy) {
        this.greedy = greedy;
    }

    public boolean parse(StringIterator iterator) {
        if (greedy) {
            iterator.skipSpaces();
            value = iterator.getRemaining();
        } else
            value = iterator.readWord();
        return true;
    }

    public String value() {
        return value;
    }
}
