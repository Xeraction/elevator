package xeraction.elevator.argument;

import xeraction.elevator.StringIterator;

public class Vec3Argument implements Argument {
    private String vec;

    public boolean parse(StringIterator iterator) {
        vec = iterator.readWord();
        if (!checkFormat(vec)) //only check the first one to make differentiation in choice arguments possible
            return false;
        vec += " " + iterator.readWord();
        vec += " " + iterator.readWord();
        return true;
    }

    public String vec() {
        return vec;
    }

    public static boolean checkFormat(String part) {
        if (part.equals("~") || part.equals("^"))
            return true;
        if (part.startsWith("~") || part.startsWith("^"))
            part = part.substring(1);
        try {
            Double.parseDouble(part);
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }
}
