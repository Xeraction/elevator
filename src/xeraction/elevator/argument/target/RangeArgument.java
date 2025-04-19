package xeraction.elevator.argument.target;

import xeraction.elevator.StringIterator;
import xeraction.elevator.util.NumberRange;

public class RangeArgument extends SelectorArgument {
    public NumberRange range;

    protected RangeArgument(String name) {
        super(name);
    }

    public void parseValue(StringIterator iterator) {
        range = NumberRange.parse(iterator);
    }

    public String build() {
        return name + "=" + range.build();
    }

    public static class DistanceArgument extends RangeArgument {
        public DistanceArgument() {
            super("distance");
        }

        public DistanceArgument(double max) {
            this();
            range = new NumberRange(Double.NEGATIVE_INFINITY, max);
        }
    }

    public static class LevelArgument extends RangeArgument {
        public LevelArgument() {
            super("level");
        }
    }

    public static class XRotationArgument extends RangeArgument {
        public XRotationArgument() {
            super("x_rotation");
        }
    }

    public static class YRotationArgument extends RangeArgument {
        public YRotationArgument() {
            super("y_rotation");
        }
    }
}
