package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.argument.StringArgument;

public class DatapackCommand implements Command {
    private String name;
    private String existing;
    private boolean enable;
    private Setting setting;

    public String build() {
        StringBuilder b = new StringBuilder("datapack ");
        if (name != null) {
            b.append(enable ? "enable " : "disable ");
            b.append(name);
            if (setting != null)
                b.append(" ").append(setting.name);
            if (existing != null)
                b.append(" ").append(existing);
        } else {
            b.append("list");
            if (setting != null)
                b.append(" ").append(setting.name);
        }
        return b.toString();
    }

    public static final ParseSequence<DatapackCommand> SEQUENCE = new ParseSequence<>(DatapackCommand::new)
            .node("name", new StringArgument(), (arg, cmd) -> cmd.name = arg.value())
            .node("existing", new StringArgument(), (arg, cmd) -> cmd.existing = arg.value())
            .lit("* enable", cmd -> cmd.enable = true).lit("* disable", cmd -> cmd.enable = false)
            .lit((cmd, lit) -> cmd.setting = Setting.parse(lit))
            .rule("datapack (disable <name>|enable <name> [((first|last)|(before|after) [<existing>])]|list [(available|enabled)])");

    private enum Setting {
        First("first"), Last("last"), Before("before"), After("after"), Available("available"), Enabled("enabled");

        public final String name;

        Setting(String name) {
            this.name = name;
        }

        public static Setting parse(String setting) {
            for (Setting s : values())
                if (s.name.equals(setting))
                    return s;
            return null;
        }
    }
}
