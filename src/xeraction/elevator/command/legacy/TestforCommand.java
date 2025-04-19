package xeraction.elevator.command.legacy;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.NBTArgument;
import xeraction.elevator.argument.TargetSelectorArgument;
import xeraction.elevator.command.Command;
import xeraction.elevator.util.EntityData;
import xeraction.elevator.util.SNBT;

public class TestforCommand implements Command {
    private String target;
    private String data;

    public String build() {
        String t = target;
        if (data != null) {
            if (!target.startsWith("@"))
                t = "@a[name=" + t + ",nbt=" + data + "]";
            else if (t.endsWith("]"))
                t = t.substring(0, t.length() - 1) + ",nbt=" + data + "]";
            else
                t += "[nbt=" + data + "]";
        }
        return "execute if entity " + t;
    }

    public static final ParseSequence<TestforCommand> SEQUENCE = new ParseSequence<>(TestforCommand::new)
            .node("target", new TargetSelectorArgument(), (arg, cmd) -> cmd.target = arg.build())
            .node("data", new NBTArgument(), (arg, cmd) -> {
                SNBT.Compound c = (SNBT.Compound)arg.tag();
                if (cmd.target.contains("type=")) {
                    int start = cmd.target.indexOf("type=") + 5;
                    int end = cmd.target.indexOf(',', start);
                    if (end == -1)
                        end = cmd.target.indexOf(']');
                    EntityData.elevateEntityData(c, cmd.target.substring(start, end));
                } else
                    EntityData.elevateEntityData(c, null);
                cmd.data = c.build();
            })
            .rule("testfor <target> [<data>]");
}
