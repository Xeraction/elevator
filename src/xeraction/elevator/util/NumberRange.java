package xeraction.elevator.util;

import xeraction.elevator.StringIterator;

public record NumberRange(double from, double to) {
    public String build() {
        if (from == to)
            return fI(from);
        if (from == Double.NEGATIVE_INFINITY)
            return ".." + fI(to);
        if (to == Double.POSITIVE_INFINITY)
            return fI(from) + "..";
        return fI(from) + ".." + fI(to);
    }

    public static NumberRange parse(StringIterator iterator) {
        if (iterator.peek() == '.') {
            iterator.skip(2);
            return new NumberRange(Double.NEGATIVE_INFINITY, iterator.readNumber());
        }
        double val = iterator.readNumber();
        if (iterator.peek() == '.') {
            iterator.skip(2);
            if (Character.isDigit(iterator.peek()))
                return new NumberRange(val, iterator.readNumber());
            return new NumberRange(val, Double.POSITIVE_INFINITY);
        }
        return new NumberRange(val, val);
    }

    public static String fI(double num) {
        String s = String.valueOf(num);
        if (s.endsWith(".0"))
            return s.substring(0, s.length() - 2);
        return s;
    }
}
