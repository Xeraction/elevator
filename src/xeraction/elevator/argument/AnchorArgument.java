package xeraction.elevator.argument;

import xeraction.elevator.StringIterator;

public class AnchorArgument implements Argument {
    private boolean eyes;

    public boolean parse(StringIterator iterator) {
        String val = iterator.readWord();
        if (val.equals("eyes")) {
            eyes = true;
            return true;
        }
        if (val.equals("feet")) {
            eyes = false;
            return true;
        }
        return false;
    }

    public String anchor() {
        return eyes ? "eyes" : "feet";
    }
}
