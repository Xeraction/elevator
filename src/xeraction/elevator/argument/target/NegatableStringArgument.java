package xeraction.elevator.argument.target;

import xeraction.elevator.StringIterator;
import xeraction.elevator.util.LegacyData;

public class NegatableStringArgument extends SelectorArgument {
    protected String value;
    private boolean negated;

    protected NegatableStringArgument(String name) {
        super(name);
        value = null;
        negated = false;
    }

    public void parseValue(StringIterator iterator) {
        if (StringIterator.isTerminator(iterator.peek()))
            return;
        if (iterator.peek() == '!') {
            iterator.skip(1);
            negated = true;
            if (StringIterator.isTerminator(iterator.peek()))
                return;
        }
        value = iterator.readUntilKeep(StringIterator::isTerminator).stripTrailing();
        if (value.startsWith("\""))
            value = value.substring(1, value.length() - 2);
    }

    public String build() {
        return name + "=" + (negated ? "!" : "") + value;
    }

    public boolean negated() {
        return negated;
    }

    public static class TagArgument extends NegatableStringArgument {
        public TagArgument() {
            super("tag");
        }
    }

    public static class TeamArgument extends NegatableStringArgument {
        public TeamArgument() {
            super("team");
        }
    }

    public static class GamemodeArgument extends NegatableStringArgument {
        public GamemodeArgument() {
            super("gamemode");
        }

        public void parseValue(StringIterator iterator) {
            super.parseValue(iterator);
            value = LegacyData.renameGamemode(value);
        }
    }

    public static class NameArgument extends NegatableStringArgument {
        public NameArgument() {
            super("name");
        }
    }

    public static class TypeArgument extends NegatableStringArgument {
        public TypeArgument() {
            super("type");
        }

        public void parseValue(StringIterator iterator) {
            super.parseValue(iterator);
            value = LegacyData.renameEntityId(value).substring(10);
        }

        public String type() {
            return value;
        }

        public void type(String type) {
            value = type;
        }
    }

    public static class PredicateArgument extends NegatableStringArgument {
        public PredicateArgument() {
            super("predicate");
        }
    }
}
