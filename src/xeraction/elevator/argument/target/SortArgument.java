package xeraction.elevator.argument.target;

import xeraction.elevator.ParseException;
import xeraction.elevator.StringIterator;

public class SortArgument extends SelectorArgument {
    private Sort sort;

    public SortArgument() {
        super("sort");
    }

    public void parseValue(StringIterator iterator) {
        String val = iterator.readUntil(StringIterator::isTerminator);
        sort = Sort.get(val);
        if (sort == null)
            throw new ParseException("Unexpected value in 'sort' target selector argument: '" + val + "'.");
    }

    public String build() {
        return name + "=" + sort.name;
    }

    private enum Sort {
        Nearest("nearest"), Furthest("furthest"), Random("random"), Arbitrary("arbitrary");

        public final String name;

        Sort(String name) {
            this.name = name;
        }

        public static Sort get(String s) {
            for (Sort sort : values())
                if (sort.name.equals(s))
                    return sort;
            return null;
        }
    }
}
