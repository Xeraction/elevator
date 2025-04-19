package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.StringIterator;
import xeraction.elevator.argument.Argument;
import xeraction.elevator.argument.Vec3Argument;
import xeraction.elevator.argument.StringArgument;
import xeraction.elevator.argument.TargetSelectorArgument;
import xeraction.elevator.util.LegacyData;

public class PlaysoundCommand implements Command {
    private String sound;
    private String source;
    private String target;
    private String pos;
    private String volume;
    private String pitch;
    private String minVol;

    public String build() {
        StringBuilder b = new StringBuilder("playsound ").append(sound);
        if (source != null)
            b.append(" ").append(source);
        if (target != null) {
            if (source == null)
                b.append(" master");
            b.append(" ").append(target);
        }
        if (pos != null)
            b.append(" ").append(pos);
        if (volume != null)
            b.append(" ").append(volume);
        if (pitch != null)
            b.append(" ").append(pitch);
        if (minVol != null)
            b.append(" ").append(minVol);
        return b.toString();
    }

    public static final ParseSequence<PlaysoundCommand> SEQUENCE = new ParseSequence<>(PlaysoundCommand::new)
            .node("sound", new StringArgument(), (arg, cmd) -> cmd.sound = LegacyData.renameSound(arg.value()))
            .node("source", new SourceArgument(), (arg, cmd) -> cmd.source = arg.source)
            .node("target", new TargetSelectorArgument(), (arg, cmd) -> cmd.target = arg.build())
            .node("pos", new Vec3Argument(), (arg, cmd) -> cmd.pos = arg.vec())
            .node("volume", new StringArgument(), (arg, cmd) -> cmd.volume = arg.value())
            .node("pitch", new StringArgument(), (arg, cmd) -> cmd.pitch = arg.value())
            .node("minVol", new StringArgument(), (arg, cmd) -> cmd.minVol = arg.value())
            .rule("playsound <sound> {<source>} [<target>] [<pos>] [<volume>] [<pitch>] [<minVol>]");

    private static class SourceArgument implements Argument { //apparently source could be skipped or didn't exist in earlier versions?
        private String source;

        public boolean parse(StringIterator iterator) {
            if (iterator.peekSkip() == '@')
                return false;
            source = iterator.readWord();
            return true;
        }
    }
}
