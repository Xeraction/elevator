package xeraction.elevator.argument;

import xeraction.elevator.StringIterator;

public class RotationArgument implements Argument {
    private String rot;

    public boolean parse(StringIterator iterator) {
        rot = iterator.readWord() + " " + iterator.readWord();
        return true;
    }

    public String rot() {
        return rot;
    }
}
