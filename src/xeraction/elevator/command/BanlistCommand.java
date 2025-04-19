package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;

public class BanlistCommand implements Command {
    private boolean ips;

    public String build() {
        return "banlist " + (ips ? "ips" : "players");
    }

    public static final ParseSequence<BanlistCommand> SEQUENCE = new ParseSequence<>(BanlistCommand::new)
            .lit("* ips", cmd -> cmd.ips = true)
            .lit("* players", cmd -> cmd.ips = false)
            .rule("banlist [(ips|players)]");
}
