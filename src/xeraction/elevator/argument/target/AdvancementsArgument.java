package xeraction.elevator.argument.target;

import xeraction.elevator.ParseException;
import xeraction.elevator.StringIterator;

public class AdvancementsArgument extends SelectorArgument {
    private String advancement;
    private String criteria;
    private boolean value;

    public AdvancementsArgument() {
        super("advancements");
    }

    public void parseValue(StringIterator iterator) {
        if (iterator.next() != '{')
            throw new ParseException("Expected '{' at the start of 'advancements' target selector argument.");
        advancement = iterator.readUntil('=');
        if (iterator.peek() == '{') {
            iterator.skip(1);
            criteria = iterator.readUntil('=');
        }
        value = iterator.readUntil('}').equals("true");
        if (criteria != null)
            iterator.skip(1);
    }

    public String build() {
        return "advancements={" + advancement + "=" + (criteria == null ? value : "{" + criteria + "=" + value + "}") + "}";
    }
}
