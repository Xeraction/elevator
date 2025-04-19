package xeraction.elevator.command.execute;

import xeraction.elevator.Elevator;
import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.StringArgument;

public class ValueSub implements Subcommand {
    private final String name;
    protected String value;

    private ValueSub(String name) {
        this.name = name;
    }

    public String build() {
        return name + " " + value;
    }

    public boolean requiresNext() {
        return false;
    }

    public static class DimensionSub extends ValueSub {
        public DimensionSub() {
            super("dimension");
        }

        public static final ParseSequence<DimensionSub> SEQUENCE = new ParseSequence<>(DimensionSub::new)
                .node("val", new StringArgument(), (arg, cmd) -> cmd.value = arg.value())
                .rule("dimension <val>");
    }

    public static class FunctionSub extends ValueSub {
        public FunctionSub() {
            super("function");
        }

        public boolean requiresNext() {
            return true;
        }

        public static final ParseSequence<FunctionSub> SEQUENCE = new ParseSequence<>(FunctionSub::new)
                .node("val", new StringArgument(), (arg, cmd) -> cmd.value = arg.value())
                .rule("function <val>");
    }

    public static class PredicateSub extends ValueSub {
        public PredicateSub() {
            super("predicate");
        }

        public static final ParseSequence<PredicateSub> SEQUENCE = new ParseSequence<>(PredicateSub::new)
                .node("val", new StringArgument(), (arg, cmd) -> {
                    cmd.value = arg.value();
                    Elevator.warn("Inline predicate definition will not be upgraded because I'm not crazy.");
                })
                .rule("predicate <val>");
    }
}
