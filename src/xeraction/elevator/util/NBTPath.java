package xeraction.elevator.util;

import xeraction.elevator.StringIterator;

import java.util.ArrayList;
import java.util.List;

public class NBTPath {
    private final List<String> parts = new ArrayList<>();

    public NBTPath(StringIterator iterator) {
        while (true) {
            if (iterator.peek() == '.')
                iterator.skip(1);
            if (iterator.peek() == '"' || iterator.peek() == '\'') {
                parts.add(SNBT.readString(iterator));
                if (iterator.peek() != '.')
                    break;
            }
            parts.add(iterator.readUntilKeep(c -> c == '.' || c == ' '));
            if (iterator.peek() != '.')
                break;
        }
    }

    public String build() {
        StringBuilder b = new StringBuilder();
        for (String s : parts)
            b.append(s).append(".");
        b.deleteCharAt(b.length() - 1);
        return b.toString();
    }
}
