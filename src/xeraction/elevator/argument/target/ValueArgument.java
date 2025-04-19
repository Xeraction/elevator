package xeraction.elevator.argument.target;

import xeraction.elevator.StringIterator;
import xeraction.elevator.util.NumberRange;

public class ValueArgument extends SelectorArgument {
    protected double value;

    protected ValueArgument(String name) {
        super(name);
    }

    public void parseValue(StringIterator iterator) {
        value = iterator.readNumber();
    }

    public String build() {
        return name + "=" + NumberRange.fI(value);
    }

    public static class XArgument extends ValueArgument {
        public XArgument() {
            super("x");
        }

        public XArgument(double val) {
            this();
            value = val;
        }
    }

    public static class YArgument extends ValueArgument {
        public YArgument() {
            super("y");
        }

        public YArgument(double val) {
            this();
            value = val;
        }
    }

    public static class ZArgument extends ValueArgument {
        public ZArgument() {
            super("z");
        }

        public ZArgument(double val) {
            this();
            value = val;
        }
    }

    public static class DxArgument extends ValueArgument {
        public DxArgument() {
            super("dx");
        }
    }

    public static class DyArgument extends ValueArgument {
        public DyArgument() {
            super("dy");
        }
    }

    public static class DzArgument extends ValueArgument {
        public DzArgument() {
            super("dz");
        }
    }

    public static class LimitArgument extends ValueArgument {
        public LimitArgument() {
            super("limit");
        }
    }
}
