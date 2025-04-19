package xeraction.elevator.argument;

import xeraction.elevator.Elevator;
import xeraction.elevator.ParseException;
import xeraction.elevator.StringIterator;
import xeraction.elevator.argument.target.*;
import xeraction.elevator.util.EntityData;
import xeraction.elevator.util.NumberRange;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class TargetSelectorArgument implements Argument {
    private SelectorType type;
    private String selectorName;
    private List<SelectorArgument> args;

    public boolean parse(StringIterator iterator) {
        iterator.skipSpaces();
        args = null;
        if (iterator.peek() == '@') {
            iterator.next();
            type = SelectorType.get(iterator.next());
            if (type == null)
                return false;
            if (iterator.peek() != '[')
                return true;
            iterator.next();
            //parse individual selector arguments
            args = new ArrayList<>();
            do {
                if (Character.isDigit(iterator.peek()) || iterator.peek() == '-') { //pre-1.11 implicit coords
                    args.add(new ValueArgument.XArgument(iterator.readNumber())); iterator.nextSkip();
                    args.add(new ValueArgument.YArgument(iterator.readNumber())); iterator.nextSkip();
                    args.add(new ValueArgument.ZArgument(iterator.readNumber())); iterator.nextSkip();
                    args.add(new RangeArgument.DistanceArgument(iterator.readNumber()));
                    if (iterator.peekSkip() == ',')
                        iterator.nextSkip();
                    continue;
                }
                String name = iterator.readUntil('=').stripTrailing().toLowerCase();
                if (name.startsWith("\"")) //these can be quoted for some reason...
                    name = name.substring(1, name.length() - 2);
                args.add(parseArgument(name, iterator));
                if (iterator.peekSkip() == ',')
                    iterator.nextSkip();
            } while (iterator.peekSkip() != ']');
            iterator.nextSkip();

            //merge legacy range selectors
            List<String> done = new ArrayList<>();
            List<SelectorArgument> merged = new ArrayList<>();
            List<String> scores = new ArrayList<>();
            for (int i = 0; i < args.size(); i++) {
                if (args.get(i) instanceof LegacyRangeArgument range && !done.contains(range.toNew)) {
                    double min = Double.NEGATIVE_INFINITY, max = Double.POSITIVE_INFINITY;
                    if (range.max)
                        max = range.value;
                    else
                        min = range.value;
                    for (int j = i + 1; j < args.size(); j++) {
                        if (args.get(j) instanceof LegacyRangeArgument other && other.toNew.equals(range.toNew)) {
                            if (other.max)
                                max = other.value;
                            else
                                min = other.value;
                            break;
                        }
                    }
                    if (!arguments.containsKey(range.toNew)) {
                        scores.add(scoreName(range.name) + "=" + new NumberRange(min, max).build());
                    } else {
                        SelectorArgument arg = arguments.get(range.toNew).get();
                        arg.parseValue(new StringIterator(new NumberRange(min, max).build()));
                        merged.add(arg);
                    }
                    done.add(range.toNew);
                }
            }
            if (!scores.isEmpty()) {
                StringBuilder b = new StringBuilder("{");
                for (String score : scores)
                    b.append(score).append(",");
                b.deleteCharAt(b.length() - 1);
                b.append("}");
                ScoreArgument arg = new ScoreArgument();
                arg.parseValue(new StringIterator(b.toString()));
                merged.add(arg);
            }
            args.removeIf(arg -> arg instanceof LegacyRangeArgument);
            args.addAll(merged);

            //elevate entity data if possible (type and nbt argument present)
            top: for (SelectorArgument arg : args) {
                if (!(arg instanceof NbtArgument na))
                    continue;
                if (na.nbt().contains("id")) {
                    EntityData.elevateEntityData(na.nbt(), null);
                    break;
                }
                for (SelectorArgument arg2 : args) {
                    if (!(arg2 instanceof NegatableStringArgument.TypeArgument ta) || ta.negated())
                        continue;
                    ta.type(EntityData.elevateEntityData(na.nbt(), ta.type()));
                    break top;
                }
                EntityData.elevateEntityData(na.nbt(), null);
                Elevator.warn("Could not elevate entity-specific nbt in target selector due to missing entity specification. (type argument)");
                break;
            }
        } else {
            type = null;
            selectorName = iterator.readWord();
        }
        return true;
    }

    public String build() {
        if (type == null)
            return selectorName;
        if (args == null || args.isEmpty())
            return "@" + type.letter;
        StringBuilder builder = new StringBuilder("@").append(type.letter).append("[");
        for (SelectorArgument arg : args)
            builder.append(arg.build()).append(',');
        builder.deleteCharAt(builder.length() - 1);
        builder.append(']');
        return builder.toString();
    }

    public static String elevateTargetSelector(String old) {
        TargetSelectorArgument arg = new TargetSelectorArgument();
        arg.parse(new StringIterator(old));
        return arg.build();
    }

    private static SelectorArgument parseArgument(String name, StringIterator iterator) {
        if (!arguments.containsKey(name)) {
            if (name.startsWith("score_")) {
                String obj = scoreName(name);
                SelectorArgument arg = new LegacyRangeArgument(name, obj, !name.endsWith("_min"));
                arg.parseValue(iterator);
                return arg;
            }
            throw new ParseException("Unknown target selector argument '" + name + "'.");
        }
        SelectorArgument arg = arguments.get(name).get();
        arg.parseValue(iterator);
        return arg;
    }

    private static String scoreName(String score) {
        String[] sp = score.split("_");
        String name = sp[1];
        if (sp.length > 2)
            for (int i = 2; i < sp.length; i++)
                if (!sp[i].equals("min"))
                    name += "_" + sp[i];
        return name;
    }

    private static final Map<String, Supplier<? extends SelectorArgument>> arguments;

    static {
        arguments = new HashMap<>();
        arguments.put("advancements", AdvancementsArgument::new);
        arguments.put("nbt", NbtArgument::new);
        arguments.put("predicate", NegatableStringArgument.PredicateArgument::new);
        arguments.put("type", NegatableStringArgument.TypeArgument::new);
        arguments.put("name", NegatableStringArgument.NameArgument::new);
        arguments.put("gamemode", NegatableStringArgument.GamemodeArgument::new);
        arguments.put("team", NegatableStringArgument.TeamArgument::new);
        arguments.put("tag", NegatableStringArgument.TagArgument::new);
        arguments.put("x_rotation", RangeArgument.XRotationArgument::new);
        arguments.put("y_rotation", RangeArgument.YRotationArgument::new);
        arguments.put("level", RangeArgument.LevelArgument::new);
        arguments.put("distance", RangeArgument.DistanceArgument::new);
        arguments.put("scores", ScoreArgument::new);
        arguments.put("sort", SortArgument::new);
        arguments.put("x", ValueArgument.XArgument::new);
        arguments.put("y", ValueArgument.YArgument::new);
        arguments.put("z", ValueArgument.ZArgument::new);
        arguments.put("dx", ValueArgument.DxArgument::new);
        arguments.put("dy", ValueArgument.DyArgument::new);
        arguments.put("dz", ValueArgument.DzArgument::new);
        arguments.put("limit", ValueArgument.LimitArgument::new);
        //legacy arguments
        arguments.put("m", NegatableStringArgument.GamemodeArgument::new);
        arguments.put("l", () -> new LegacyRangeArgument("l", "level", true));
        arguments.put("lm", () -> new LegacyRangeArgument("lm", "level", false));
        arguments.put("r", () -> new LegacyRangeArgument("r", "distance", true));
        arguments.put("rm", () -> new LegacyRangeArgument("rm", "distance", false));
        arguments.put("rx", () -> new LegacyRangeArgument("rx", "x_rotation", true));
        arguments.put("rxm", () -> new LegacyRangeArgument("rxm", "x_rotation", false));
        arguments.put("ry", () -> new LegacyRangeArgument("ry", "y_rotation", true));
        arguments.put("rym", () -> new LegacyRangeArgument("rym", "y_rotation", false));
        arguments.put("c", ValueArgument.LimitArgument::new);
    }

    private enum SelectorType {
        AtP('p'), AtR('r'), AtN('n'), AtA('a'), AtE('e'), AtS('s');

        public final char letter;

        SelectorType(char letter) {
            this.letter = letter;
        }

        public static SelectorType get(char c) {
            for (SelectorType type : values())
                if (type.letter == c)
                    return type;
            return null;
        }
    }
}
