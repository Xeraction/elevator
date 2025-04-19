package xeraction.elevator.argument;

import xeraction.elevator.StringIterator;
import xeraction.elevator.util.ItemComponents;
import xeraction.elevator.util.LegacyData;
import xeraction.elevator.util.SNBT;

import java.util.ArrayList;
import java.util.List;

public class ItemArgument implements Argument {
    private String item;
    private List<PredicateTest> components;

    public boolean parse(StringIterator iterator) {
        item = iterator.readUntilKeep(c -> c == '[' || c == '{' || c == ' ');
        item = LegacyData.renameItemId(item);
        components = new ArrayList<>();
        if (!iterator.hasMore())
            return true;
        char c = iterator.peek();
        if (c == '[') {
            iterator.next();
            boolean nextOrChain = false;
            List<SNBT.Tag> comps = new ArrayList<>();
            while (true) {
                String name = iterator.readUntilKeep(ch -> ch == ',' || ch == '|' || ch == ']' || ch == '=' || ch == '~');
                boolean negated = false;
                if (name.startsWith("!")) {
                    negated = true;
                    name = name.substring(1);
                }
                if (!name.startsWith("minecraft:"))
                    name = "minecraft:" + name;
                if (name.equals("minecraft:count")) {
                    switch (iterator.peek()) {
                        case '=' -> {
                            iterator.next();
                            components.add(new Count(true, SNBT.parseTag(iterator, false), negated, nextOrChain));
                        }
                        case '~' -> {
                            iterator.next();
                            components.add(new Count(false, SNBT.parseTag(iterator, false), negated, nextOrChain));
                        }
                        default -> components.add(new Count(false, null, negated, nextOrChain));
                    }
                } else {
                    switch (iterator.peek()) {
                        case '=' -> {
                            iterator.next();
                            SNBT.Tag compTag = SNBT.parseTag(iterator, false);
                            compTag.name = name;
                            comps.add(compTag);
                            components.add(new Component(name, null, negated, nextOrChain));
                        }
                        case '~' -> {
                            iterator.next();
                            SNBT.Tag predTag = SNBT.parseTag(iterator, false);
                            ItemComponents.elevateComponentPredicate(predTag);
                            components.add(new Predicate(name, predTag, negated, nextOrChain));
                        }
                        default -> components.add(new Component(name, null, negated, nextOrChain));
                    }
                }
                nextOrChain = false;
                char n = iterator.next();
                if (n == ']')
                    break;
                if (n == '|')
                    nextOrChain = true;
            }
            if (!comps.isEmpty()) {
                ItemComponents.elevateComponents(comps);
                outer: for (SNBT.Tag tag : comps) {
                    for (int i = 0; i < components.size(); i++) {
                        if (components.get(i) instanceof Component co && co.name != null && co.name.equals(tag.name)) {
                            co.tag = tag;
                            tag.name = null;
                            continue outer;
                        }
                    }
                    components.add(new Component(tag.name, tag, false, false)); //could maybe cause problems with certain condition sequences?
                    tag.name = null;
                }
            }
        } else if (c == '{') { //turn legacy nbt into components
            SNBT.Compound tag = SNBT.parse(iterator);
            List<SNBT.Tag> comps = ItemComponents.fromNBT(tag, item);
            for (SNBT.Tag cmp : comps)
                components.add(new Component(cmp.name, cmp.name(null), false, false));
        }
        return true;
    }

    public String build() {
        if (components.isEmpty())
            return item;
        StringBuilder builder = new StringBuilder();
        for (PredicateTest test : components) {
            builder.append(test.orChained() ? "|" : ",");
            builder.append(test.build());
        }
        builder.deleteCharAt(0);
        return item + "[" + builder + "]";
    }

    private static class Component implements PredicateTest {
        public final String name;
        public SNBT.Tag tag;
        public final boolean negated;
        public final boolean orChained;

        public Component(String name, SNBT.Tag tag, boolean negated, boolean orChained) {
            this.name = name;
            this.tag = tag;
            this.negated = negated;
            this.orChained = orChained;
        }

        public String build() {
            return (negated ? "!" : "") + name + (tag != null ? "=" + tag.build() : "");
        }

        public boolean orChained() {
            return orChained;
        }
    }

    private record Predicate(String name, SNBT.Tag tag, boolean negated, boolean orChained) implements PredicateTest {
        public String build() {
            return (negated ? "!" : "") + name + "~" + tag.build();
        }
    }

    private record Count(boolean equal, SNBT.Tag value, boolean negated, boolean orChained) implements PredicateTest {
        public String build() {
            return (negated ? "!" : "") + "minecraft:count" + (value != null ? ((equal ? "=" : "~") + value.build()) : "");
        }
    }

    private interface PredicateTest {
        String build();
        boolean orChained();
    }
}
