package xeraction.elevator.argument;

import xeraction.elevator.StringIterator;
import xeraction.elevator.util.BlockPredicate;

public class BlockPredicateArgument implements Argument {
    private BlockPredicate predicate;

    public boolean parse(StringIterator iterator) {
        predicate = new BlockPredicate(iterator);
        return true;
    }

    public BlockPredicate predicate() {
        return predicate;
    }
}
