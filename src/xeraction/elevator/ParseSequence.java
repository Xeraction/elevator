package xeraction.elevator;

import xeraction.elevator.argument.Argument;
import xeraction.elevator.command.Command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ParseSequence<C extends Command> {
    private final Supplier<C> commandSuppler;
    private C command;
    private Node rootNode;
    private final Map<String, ParseNode<?>> arguments;
    private final Map<String, Consumer<C>> literalActions;
    private BiConsumer<C, String> generalLit = null;
    private final Map<String, String> substitutions;
    private String literalSequence;

    public ParseSequence(Supplier<C> command) {
        this.commandSuppler = command;
        this.arguments = new HashMap<>();
        this.literalActions = new HashMap<>();
        this.substitutions = new HashMap<>();
    }

    public <A extends Argument> ParseSequence<C> node(String id, A arg, BiConsumer<A, C> worker) {
        arguments.put(id, new ParseNode<>(arg, worker));
        return this;
    }

    public ParseSequence<C> lit(String lit, Consumer<C> action) {
        literalActions.put(lit, action);
        return this;
    }

    public ParseSequence<C> lit(BiConsumer<C, String> action) {
        generalLit = action;
        return this;
    }

    public ParseSequence<C> sub(String sub, String with) {
        substitutions.put(sub, with);
        return this;
    }

    public ParseSequence<C> rule(String rule) {
        StringIterator it = new StringIterator(rule);
        rootNode = parseNode(it);
        return this;
    }

    private Node parseNode(StringIterator iterator) {
        Node n;
        switch (iterator.peekSkip()) {
            case '<' -> {
                iterator.nextSkip();
                n = arguments.get(iterator.readUntil('>')).clone();
            }
            case '/' -> {
                iterator.nextSkip();
                n = parseNode(new StringIterator(substitutions.get(iterator.readUntil('/'))));
            }
            case '[' -> {
                iterator.nextSkip();
                n = new OptionalNode(parseNode(iterator));
                iterator.skip(1);
            }
            case '{' -> {
                iterator.nextSkip();
                n = new SkipableNode(parseNode(iterator));
                iterator.skip(1);
            }
            case '(' -> {
                iterator.nextSkip();
                List<Node> choices = new ArrayList<>();
                while (true) {
                    choices.add(parseNode(iterator));
                    if (iterator.peek() != '|')
                        break;
                    iterator.skip(1);
                }
                iterator.skip(1);
                n = new ChoiceNode(choices);
            }
            default -> n = new LiteralNode(iterator.readUntilKeep(c -> c == ' ' || c == '|' || c == ')' || c == ']' || c == '}'));
        }
        if (iterator.hasMore() && iterator.peek() == ' ')
            n.next = parseNode(iterator);
        return n;
    }

    public Command parse(StringIterator iterator) {
        command = commandSuppler.get();
        literalSequence = "";
        if (rootNode.parse(iterator))
            return command;
        return null;
    }

    private static abstract class Node {
        protected Node next = null;

        public abstract boolean parse(StringIterator iterator);
    }

    private class ParseNode<A extends Argument> extends Node {
        private final A argument;
        private final BiConsumer<A, C> worker;

        public ParseNode(A argument, BiConsumer<A, C> worker) {
            this.argument = argument;
            this.worker = worker;
        }

        public boolean parse(StringIterator iterator) {
            if (!argument.parse(iterator))
                return false;
            if (worker != null)
                worker.accept(argument, command);
            if (next != null)
                return next.parse(iterator);
            return true;
        }

        public ParseNode<A> clone() {
            return new ParseNode<>(argument, worker);
        }

        public String toString() {
            if (next == null)
                return "<>";
            return "<> " + next.toString();
        }
    }

    private class LiteralNode extends Node {
        private final String literal;

        public LiteralNode(String literal) {
            this.literal = literal;
        }

        public boolean parse(StringIterator iterator) {
            if (iterator.peekWord().equalsIgnoreCase(literal)) {
                iterator.readWord();
                literalSequence += (literalSequence.isEmpty() ? literal : " " + literal);
                if (literalActions.containsKey(literalSequence))
                    literalActions.get(literalSequence).accept(command);
                else if (literalActions.containsKey("* " + literal))
                    literalActions.get("* " + literal).accept(command);
                else if (generalLit != null)
                    generalLit.accept(command, literal);
                if (next != null)
                    return next.parse(iterator);
                return true;
            }
            return false;
        }

        public String toString() {
            if (next == null)
                return literal;
            return literal + " " + next;
        }
    }

    private static class OptionalNode extends Node {
        private final Node optional;

        public OptionalNode(Node optional) {
            this.optional = optional;
        }

        public boolean parse(StringIterator iterator) {
            if (iterator.hasMore() && optional.parse(iterator) && next != null)
                return next.parse(iterator);
            return true;
        }

        public String toString() {
            String s = "[" + optional.toString() + "]";
            if (next == null)
                return s;
            return s + " " + next.toString();
        }
    }

    private static class SkipableNode extends Node {
        private final Node skipable;

        public SkipableNode(Node skipable) {
            this.skipable = skipable;
        }

        public boolean parse(StringIterator iterator) {
            if (iterator.hasMore()) {
                skipable.parse(iterator);
                if (next != null)
                    return next.parse(iterator);
            }
            return true;
        }

        public String toString() {
            String s = "{" + skipable.toString() + "}";
            if (next == null)
                return s;
            return s + " " + next.toString();
        }
    }

    private class ChoiceNode extends Node {
        private final List<Node> choices;

        public ChoiceNode(List<Node> choices) {
            this.choices = choices;
        }

        public boolean parse(StringIterator iterator) {
            int ptr = iterator.getPointer();
            String seq = literalSequence;
            for (Node node : choices) {
                if (node.parse(iterator)) {
                    if (next != null)
                        return next.parse(iterator);
                    return true;
                }
                iterator.setPointer(ptr);
                literalSequence = seq;
            }
            return false;
        }

        public String toString() {
            StringBuilder b = new StringBuilder("(");
            for (Node n : choices)
                b.append(n.toString()).append("|");
            b.deleteCharAt(b.length() - 1);
            b.append(")");
            if (next == null)
                return b.toString();
            return b.toString() + " " + next.toString();
        }
    }
}
