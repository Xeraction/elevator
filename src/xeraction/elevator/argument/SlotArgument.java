package xeraction.elevator.argument;

import xeraction.elevator.StringIterator;

public class SlotArgument implements Argument {
    private String slot;

    public boolean parse(StringIterator iterator) {
        slot = iterator.readWord();
        switch (slot) {
            case "horse.armor" -> slot = "armor.body";
            case "horse.saddle" -> slot = "saddle";
        }
        return true;
    }

    public String slot() {
        return slot;
    }
}
