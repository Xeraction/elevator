package xeraction.elevator;

import java.util.function.Predicate;

public class StringIterator {
    private final String string;
    private int pointer;

    public StringIterator(String string) {
        this.string = string;
        this.pointer = 0;
    }

    public boolean hasMore() {
        return pointer <= string.length() - 1;
    }

    public void reset() {
        pointer = 0;
    }

    public char current() {
        if (pointer - 1 < 0)
            throw new ParseException(errorMsg(pointer - 1));
        return string.charAt(pointer - 1);
    }

    public char next() {
        if (pointer >= string.length())
            throw new ParseException(errorMsg(pointer));
        return string.charAt(pointer++);
    }

    public char nextSkip() {
        skipSpaces();
        return next();
    }

    public String peekString(int amount) {
        if (pointer + amount >= string.length())
            throw new ParseException(errorMsg(pointer + amount));
        return string.substring(pointer, pointer + amount);
    }

    public void skipToEnd() {
        pointer = string.length();
    }

    public char peek() {
        return peek(1);
    }

    public char peek(int amount) {
        if (pointer + amount - 1 >= string.length())
            return '\u0000';
        return string.charAt(pointer + amount - 1);
    }

    public char peekSkip() {
        int tmp = pointer;
        skipSpaces();
        char c = peek();
        pointer = tmp;
        return c;
    }

    public void skip(int amount) {
        pointer += amount;
    }

    public char previous() {
        return previous(1);
    }

    public char previous(int amount) {
        return string.charAt(pointer - amount);
    }

    public String readWord() {
        return readUntil(' ');
    }

    public String peekWord() {
        int tmp = pointer;
        String word = readWord();
        pointer = tmp;
        return word;
    }

    public int remainingWords() {
        boolean space = string.charAt(pointer) == ' ';
        int words = 0;
        for (int i = pointer + 1; i < string.length(); i++) {
            char c = string.charAt(i);
            if (c == ' ' && !space)
                space = true;
            else if (c != ' ' && space) {
                space = false;
                words++;
            }
        }
        return words;
    }

    public String getRemaining() {
        return string.substring(pointer);
    }

    public double readNumber() {
        StringBuilder num = new StringBuilder();
        while (isNumPart(peek()))
            num.append(next());
        if (peek() == '.' && isNumPart(peek(2))) {
            num.append('.');
            skip(1);
            while (isNumPart(peek()))
                num.append(next());
        }
        return Double.parseDouble(num.toString());
    }

    private boolean isNumPart(char c) {
        return Character.isDigit(c) || c == '-' || c == '+';
    }

    public String readUntil(char c) {
        skipSpaces();
        StringBuilder builder = new StringBuilder();
        while (pointer < string.length() && next() != c)
            builder.append(current());
        return builder.toString();
    }

    public String readUntil(Predicate<Character> condition) {
        skipSpaces();
        StringBuilder builder = new StringBuilder();
        while (pointer < string.length() && !condition.test(next()))
            builder.append(current());
        return builder.toString();
    }

    public String readUntilKeep(Predicate<Character> condition) {
        skipSpaces();
        StringBuilder builder = new StringBuilder();
        while (pointer < string.length() && !condition.test(peek()))
            builder.append(next());
        return builder.toString();
    }

    public void skipSpaces() {
        while (peek() == ' ' || peek() == '\t')
            pointer++;
    }

    public boolean skipIfNext(char c) {
        if (peek() == c) {
            skip(1);
            return true;
        }
        return false;
    }

    private int findNextSpace() {
        return findNextChar(' ');
    }

    private int findNextChar(char c) {
        int tmp = pointer;
        while (string.charAt(tmp) != c) {
            tmp++;
            if (tmp >= string.length())
                throw new ParseException("Could not find character '" + c + "' in string '" + string + "'.");
        }
        return tmp;
    }

    public StringIterator copy() {
        StringIterator it = new StringIterator(string);
        it.pointer = pointer;
        return it;
    }

    public int getPointer() {
        return pointer;
    }

    public void setPointer(int pointer) {
        this.pointer = pointer;
    }

    public static boolean isTerminator(char c) {
        return c == ',' || c == ']' || c == '}' || c == '|';
    }

    public static boolean isOpener(char c) {
        return c == '(' || c == '[' || c == '{';
    }

    private String errorMsg(int loc) {
        return "Tried to access character at an invalid index (" + loc + "). of string '" + string + "'.";
    }
}
