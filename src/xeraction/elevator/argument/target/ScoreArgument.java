package xeraction.elevator.argument.target;

import xeraction.elevator.ParseException;
import xeraction.elevator.StringIterator;
import xeraction.elevator.util.NumberRange;

import java.util.HashMap;
import java.util.Map;

public class ScoreArgument extends SelectorArgument {
    private final Map<String, NumberRange> scores;

    public ScoreArgument() {
        super("scores");
        scores = new HashMap<>();
    }

    public void parseValue(StringIterator iterator) {
        if (iterator.next() != '{')
            throw new ParseException("Expected '{' at the start of 'scores' target selector argument.");
        do {
            String score = iterator.readUntil('=');
            scores.put(score, NumberRange.parse(iterator));
        } while (iterator.skipIfNext(','));
        if (iterator.next() != '}')
            throw new ParseException("Expected '}' at the end of 'scores' target selector argument.");
    }

    public String build() {
        StringBuilder builder = new StringBuilder(name).append("={");
        scores.forEach((name, range) -> builder.append(name).append('=').append(range.build()).append(','));
        builder.deleteCharAt(builder.length() - 1);
        builder.append('}');
        return builder.toString();
    }
}
