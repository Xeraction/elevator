package xeraction.elevator.argument;

import xeraction.elevator.StringIterator;

public class Vec2Argument implements Argument {
    private String vec;

    public boolean parse(StringIterator iterator) {
        vec = iterator.readWord();
        if (!Vec3Argument.checkFormat(vec))
            return false;
        vec += " " + iterator.readWord();
        return true;
    }

    public String vec() {
        return vec;
    }
}
