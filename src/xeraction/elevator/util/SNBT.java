package xeraction.elevator.util;

import xeraction.elevator.ParseException;
import xeraction.elevator.StringIterator;

import java.util.ArrayList;
import java.util.UUID;
import java.util.regex.Pattern;

public class SNBT {

    //we're parsing snbt into objects to be easily able to rename nbt paths because mojang likes to do that a lot
    public static Compound parse(StringIterator iterator) {
        return (Compound)parseTag(iterator, false);
    }

    public static Tag parseTag(StringIterator iterator, boolean withName) {
        String name = null;
        if (withName) {
            name = readString(iterator);
            if (iterator.nextSkip() != ':')
                throw new ParseException("Missing ':' after tag name in SNBT.");
        }
        //determine what kind of tag we're dealing with
        switch (iterator.peekSkip()) {
            case '{' -> { //compound tag
                iterator.nextSkip();
                java.util.List<Tag> tags = new ArrayList<>();
                while (true) {
                    if (iterator.peekSkip() == ',')
                        iterator.nextSkip();
                    if (iterator.peekSkip() == '}') {
                        iterator.nextSkip();
                        return new Compound(name, tags);
                    }
                    tags.add(parseTag(iterator, true));
                }
            }
            case '[' -> { //list or array tag
                iterator.nextSkip();
                //we used to just read the number until the suffix was found, but they decided to make those optional so we read an entire tag now. thanks mojang
                //on the bright side, we get to put every array type into one class now
                char type = iterator.peek();
                if (type == 'B' || type == 'I' || type == 'L')
                    iterator.skip(2);
                java.util.List<Tag> tags = new ArrayList<>();
                boolean indices = iterator.peek() == '0' && iterator.peek(2) == ':'; //lists before 1.12 could include optional indices, e.g. Motion:[0:7.3,1:1.0,2:3.1]
                while (iterator.peekSkip() != ']') {
                    tags.add(parseTag(iterator, indices).name(null));
                    if (iterator.peekSkip() == ',')
                        iterator.nextSkip();
                }
                iterator.nextSkip();
                if (type != 'B' && type != 'I' && type != 'L')
                    return new List(name, tags);
                return new Array(name, tags, type);
            }
            case '"', '\'' -> { //quoted string tag (strings can also be unquoted - do that if nothing else fits)
                return new Strings(name, readString(iterator));
            }
            default -> { //every other tag - read it fully and decide by suffix
                String content = iterator.readUntilKeep(StringIterator::isTerminator);
                if ((content.startsWith("bool") || content.startsWith("uuid")) && content.contains("(")) //operations for qol i guess? thanks mojang
                    return new Operation(name, content);
                if (content.equals("true") || content.equals("false")) //boolean tag (actually a byte tag programmatically, but snbt disguises it with true/false so we're keeping it like that)
                    return new Boolean(name, content.equals("true"));
                if (numberStart(content)) {
                    if (content.endsWith("b") || content.endsWith("B")) //byte tag
                        return new Byte(name, java.lang.Byte.parseByte(content.substring(0, content.length() - 1)));
                    if (content.endsWith("s") || content.endsWith("S")) //short tag
                        return new Short(name, java.lang.Short.parseShort(content.substring(0, content.length() - 1)));
                    if (content.endsWith("l") || content.endsWith("L")) //long tag
                        return new Long(name, java.lang.Long.parseLong(content.substring(0, content.length() - 1)));
                    if (content.endsWith("f") || content.endsWith("F")) //float tag
                        return new Float(name, java.lang.Float.parseFloat(content.substring(0, content.length() - 1)));
                    //the only possible number tags remaining are integer and double
                    try {
                        return new Int(name, Integer.parseInt(content));
                    } catch (NumberFormatException ignored) {}
                    try {
                        return new Double(name, java.lang.Double.parseDouble(content));
                    } catch (NumberFormatException ignored) {}
                }
                //unquoted string
                return new Strings(name, content);
            }
        }
    }

    public static String readString(StringIterator iterator) {
        iterator.skipSpaces();
        StringBuilder b = new StringBuilder();
        if (iterator.peek() == '"' || iterator.peek() == '\'') {
            char quote = iterator.next();
            while (true) {
                if (iterator.peek() == '\\') //save strings unescaped to not break parsers that need to work with the strings
                    iterator.next();
                if ((iterator.peek() == quote) && iterator.current() != '\\') {
                    iterator.next();
                    return b.toString();
                }
                b.append(iterator.next());
            }
        }
        while (true) {
            char c = iterator.peekSkip();
            if (c == ',' || c == ':' || c == ']' || c == '}')
                return b.toString();
            b.append(iterator.next());
        }
    }

    public static String buildUUID(java.util.List<Tag> array) {
        long most = (long)((Int)array.get(0)).value << 32 | ((Int)array.get(1)).value;
        long least = (long)((Int)array.get(2)).value << 32 | ((Int)array.get(3)).value;
        return new UUID(most, least).toString();
    }

    public static abstract class Tag {
        public String name;

        protected Tag(String name) {
            this.name = name;
        }

        public Tag name(String name) {
            this.name = name;
            return this;
        }

        public abstract String build();

        protected String buildName() {
            if (name == null)
                return "";
            if (mustQuote(name))
                return "\"" + name + "\"" + ":";
            return name + ":";
        }
    }

    public static class Byte extends Tag {
        public byte value;

        public Byte(String name, byte value) {
            super(name);
            this.value = value;
        }

        public String build() {
            return buildName() + value + "b";
        }
    }

    public static class Boolean extends Tag {
        public boolean value;

        public Boolean(String name, boolean value) {
            super(name);
            this.value = value;
        }

        public String build() {
            return buildName() + value;
        }
    }

    public static class Short extends Tag {
        public short value;

        public Short(String name, short value) {
            super(name);
            this.value = value;
        }

        public String build() {
            return buildName() + value + "s";
        }
    }

    public static class Int extends Tag {
        public int value;

        public Int(String name, int value) {
            super(name);
            this.value = value;
        }

        public String build() {
            return buildName() + value;
        }
    }

    public static class Long extends Tag {
        public long value;

        public Long(String name, long value) {
            super(name);
            this.value = value;
        }

        public String build() {
            return buildName() + value + "l";
        }
    }

    public static class Float extends Tag {
        public float value;

        public Float(String name, float value) {
            super(name);
            this.value = value;
        }

        public String build() {
            return buildName() + value + "f";
        }
    }

    public static class Double extends Tag {
        public double value;

        public Double(String name, double value) {
            super(name);
            this.value = value;
        }

        public String build() {
            return buildName() + value + "d";
        }
    }

    public static class Strings extends Tag {
        public String value;

        public Strings(String name, String value) {
            super(name);
            this.value = value;
        }

        public String build() {
            if (value.isEmpty())
                return buildName() + "\"\"";
            if (mustQuote(value)) {
                if (value.contains("\""))
                    return buildName() + "'" + escape(value, '\'') + "'";
                return buildName() + '"' + escape(value, '"') + '"';
            }
            return buildName() + value;
        }
    }

    public static class List extends Tag {
        public java.util.List<Tag> value;

        public List(String name, java.util.List<Tag> value) {
            super(name);
            this.value = value;
        }

        public String build() {
            StringBuilder b = new StringBuilder(buildName()).append('[');
            if (!value.isEmpty()) {
                for (Tag tag : value)
                    b.append(tag.build()).append(',');
                b.deleteCharAt(b.length() - 1);
            }
            b.append(']');
            return b.toString();
        }
    }

    public static class Compound extends Tag {
        public java.util.List<Tag> value;

        public Compound(String name, java.util.List<Tag> value) {
            super(name);
            this.value = value;
        }

        public boolean contains(String name) {
            for (Tag tag : value)
                if (tag.name.equals(name))
                    return true;
            return false;
        }

        public Tag get(String name) {
            for (Tag tag : value)
                if (tag.name.equals(name))
                    return tag;
            return null;
        }

        public byte getByte(String name) {
            return ((Byte)get(name)).value;
        }

        public boolean getBoolean(String name) {
            if (get(name) instanceof Byte b)
                return b.value == 1;
            return ((Boolean)get(name)).value;
        }

        public short getShort(String name) {
            if (!(get(name) instanceof Short b))
                return getByte(name);
            return b.value;
        }

        public int getInt(String name) {
            if (!(get(name) instanceof Int i))
                return getShort(name);
            return i.value;
        }

        public long getLong(String name) {
            if (!(get(name) instanceof Long l))
                return getInt(name);
            return l.value;
        }

        public float getFloat(String name) {
            return ((Float)get(name)).value;
        }

        public double getDouble(String name) {
            return ((Double)get(name)).value;
        }

        public String getString(String name) {
            return ((Strings)get(name)).value;
        }

        public java.util.List<Tag> getList(String name) {
            return ((List)get(name)).value;
        }

        public Compound getCompound(String name) {
            return (Compound)get(name);
        }

        public java.util.List<Tag> getArray(String name) {
            return ((Array)get(name)).value;
        }

        public void add(Tag tag) {
            value.add(tag);
        }

        public void set(String name, byte value) {
            if (contains(name))
                ((Byte)get(name)).value = value;
        }

        public void set(String name, boolean value) {
            if (contains(name))
                ((Boolean)get(name)).value = value;
        }

        public void set(String name, short value) {
            if (contains(name))
                ((Short)get(name)).value = value;
        }

        public void set(String name, int value) {
            if (contains(name))
                ((Int)get(name)).value = value;
        }

        public void set(String name, long value) {
            if (contains(name))
                ((Long)get(name)).value = value;
        }

        public void set(String name, float value) {
            if (contains(name))
                ((Float)get(name)).value = value;
        }

        public void set(String name, double value) {
            if (contains(name))
                ((Double)get(name)).value = value;
        }

        public void set(String name, String value) {
            if (contains(name))
                ((Strings)get(name)).value = value;
        }

        public void set(String name, java.util.List<Tag> value) {
            if (contains(name)) {
                Tag tag = get(name);
                if (tag instanceof List list)
                    list.value = value;
                else if (tag instanceof Compound c)
                    c.value = value;
                else if (tag instanceof Array array)
                    array.value = value;
            }
        }

        public void remove(String name) {
            for (int i = 0; i < value.size(); i++) {
                if (value.get(i).name.equals(name)) {
                    value.remove(i);
                    break;
                }
            }
        }

        public String build() {
            StringBuilder b = new StringBuilder(buildName()).append('{');
            if (!value.isEmpty()) {
                for (Tag tag : value)
                    b.append(tag.build()).append(',');
                b.deleteCharAt(b.length() - 1);
            }
            b.append('}');
            return b.toString();
        }

        public void rename(String oldName, String newName) {
            for (Tag tag : value)
                if (tag.name.equals(oldName)) {
                    tag.name = newName;
                    break;
                }
        }
    }

    public static class Array extends Tag {
        public java.util.List<Tag> value;
        public final char type;

        public Array(String name, java.util.List<Tag> value, char type) {
            super(name);
            this.value = value;
            this.type = type;
        }

        public String build() {
            StringBuilder b = new StringBuilder(buildName()).append("[").append(type).append(";");
            if (!value.isEmpty()) {
                for (Tag tag : value)
                    b.append(tag.build()).append(',');
                b.deleteCharAt(b.length() - 1);
            }
            b.append(']');
            return b.toString();
        }
    }

    //utility tag to keep bool() and uuid() operations as is (i didn't know they existed either)
    public static class Operation extends Tag {
        String op;

        public Operation(String name, String op) {
            super(name);
            this.op = op;
        }

        public String build() {
            return buildName() + op;
        }
    }

    private static final Pattern quotePattern = Pattern.compile("[A-Za-z][0-9A-Za-z_\\-.+]*");

    private static boolean mustQuote(String s) {
        if (s.equals("true") || s.equals("false"))
            return true;
        if (Character.isDigit(s.charAt(0)))
            return true;
        return !quotePattern.matcher(s).matches();
    }

    private static boolean numberStart(String s) {
        char first = s.charAt(0);
        return Character.isDigit(first) || first == '.' || first == '+' || first == '-';
    }

    private static String escape(String input, char quote) {
        StringBuilder b = new StringBuilder();
        StringIterator it = new StringIterator(input);
        while (it.hasMore()) {
            char c = it.next();
            if (c == '\\' || c == quote)
                b.append('\\');
            b.append(c);
        }
        return b.toString();
    }
}
