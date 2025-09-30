package xeraction.elevator.command;

import xeraction.elevator.ParseSequence;
import xeraction.elevator.StringIterator;
import xeraction.elevator.argument.Argument;
import xeraction.elevator.argument.Vec3Argument;
import xeraction.elevator.argument.StringArgument;
import xeraction.elevator.argument.TargetSelectorArgument;
import xeraction.elevator.util.BlockPredicate;
import xeraction.elevator.util.LegacyData;
import xeraction.elevator.util.ParticleData;
import xeraction.elevator.util.SNBT;

import java.util.ArrayList;
import java.util.List;

public class ParticleCommand implements Command {
    private String particle;
    private String pos;
    private String delta;
    private String speed;
    private String count;
    private String option;
    private String viewer;

    public String build() {
        String s = "particle " + particle;
        if (pos != null && !Character.isDigit(particle.charAt(particle.length() - 1)))
            s += " " + pos;
        if (delta != null)
            s += " " + delta + " " + speed + " " + count;
        if (option != null)
            s += " " + option;
        if (viewer != null)
            s += " " + viewer;
        return s;
    }

    public static final ParseSequence<ParticleCommand> SEQUENCE = new ParseSequence<>(ParticleCommand::new)
            .node("name", new ParticleArgument(), (arg, cmd) -> cmd.particle = arg.particle())
            .node("pos", new Vec3Argument(), (arg, cmd) -> cmd.pos = arg.vec())
            .node("delta", new Vec3Argument(), (arg, cmd) -> cmd.delta = arg.vec())
            .node("speed", new StringArgument(), (arg, cmd) -> cmd.speed = arg.value())
            .node("count", new StringArgument(), (arg, cmd) -> cmd.count = arg.value())
            .node("viewer", new TargetSelectorArgument(), (arg, cmd) -> cmd.viewer = arg.build())
            .node("params", new StringArgument(true), (arg, cmd) -> {
                ParticleArgument pa = new ParticleArgument();
                pa.parse(new StringIterator(cmd.particle + " " + arg.value()));
                cmd.particle = pa.particle();
            })
            .lit("* force", cmd -> cmd.option = "force").lit("* normal", cmd -> cmd.option = "normal")
            .rule("particle <name> [<pos>] [<delta> <speed> <count> [(force|normal)] [<viewer>] [<params>]]");

    private static class ParticleArgument implements Argument {
        private String name;
        private SNBT.Compound data;
        private String extra;

        public boolean parse(StringIterator iterator) {
            name = LegacyData.renameParticle(iterator.readUntilKeep(c -> c == ' ' || c == '{'));
            extra = null;
            if (iterator.peek() == '{') {
                data = SNBT.parse(iterator);
                ParticleData.elevateParticle(data, name);
            } else {
                //parse legacy particle options that need extra data
                List<SNBT.Tag> options = new ArrayList<>();
                if (name.contains("blockcrack_")) {
                    options.add(new SNBT.Strings("block_state", LegacyData.flattenBlock(name.split("_")[1], null, null)));
                    name = "minecraft:block";
                } else switch (name) {
                    case "minecraft:block", "minecraft:block_marker", "falling_dust" -> {
                        if (iterator.peekWord().equals("-1")) {
                            options.add(new SNBT.Strings("block_state", "minecraft:air"));
                            iterator.readWord();
                        }
                        else
                            options.add(new BlockPredicate(iterator).toGeneralNBT().name("block_state"));
                    }
                    case "minecraft:dust", "minecraft:dust_color_transition" -> {
                        float r = Float.parseFloat(iterator.readWord());
                        float g = Float.parseFloat(iterator.readWord());
                        float b = Float.parseFloat(iterator.readWord());
                        float scale = Float.parseFloat(iterator.readWord());
                        options.add(new SNBT.List(name.equals("minecraft:dust") ? "color" : "from_color", List.of(new SNBT.Float(null, r), new SNBT.Float(null, g), new SNBT.Float(null, b))));
                        options.add(new SNBT.Float("scale", scale));
                        if (name.equals("minecraft:dust_color_transition")) {
                            float r2 = Float.parseFloat(iterator.readWord());
                            float g2 = Float.parseFloat(iterator.readWord());
                            float b2 = Float.parseFloat(iterator.readWord());
                            options.add(new SNBT.List("to_color", List.of(new SNBT.Float(null, r2), new SNBT.Float(null, g2), new SNBT.Float(null, b2))));
                        }
                    }
                    case "minecraft:entity_effect" -> {
                        //the color values are actually the delta values. thanks mojang
                        int pointer = iterator.getPointer();
                        for (int i = 0; i < 3 && iterator.hasMore(); i++)
                            iterator.readWord();
                        if (iterator.hasMore()) {
                            float r = Float.parseFloat(iterator.readWord());
                            float g = Float.parseFloat(iterator.readWord());
                            float b = Float.parseFloat(iterator.readWord());
                            options.add(new SNBT.List("color", List.of(new SNBT.Float(null, r), new SNBT.Float(null, g), new SNBT.Float(null, b), new SNBT.Float(null, 1.0F))));
                        }
                        iterator.setPointer(pointer);
                    }
                    case "minecraft:flash" -> options.add(new SNBT.Int("color", 0xffffffff));
                    case "minecraft:item" -> options.add(new SNBT.Strings("item", LegacyData.renameItemId(iterator.readWord())));
                    case "minecraft:shriek" -> options.add(new SNBT.Int("delay", Integer.parseInt(iterator.readWord())));
                    case "minecraft:vibration" -> {
                        int fromX = Integer.parseInt(iterator.readWord());
                        int fromY = Integer.parseInt(iterator.readWord());
                        int fromZ = Integer.parseInt(iterator.readWord());
                        int toX = Integer.parseInt(iterator.readWord());
                        int toY = Integer.parseInt(iterator.readWord());
                        int toZ = Integer.parseInt(iterator.readWord());
                        int travelTime = Integer.parseInt(iterator.readWord());
                        options.add(new SNBT.Int("arrival_in_ticks", travelTime));
                        options.add(new SNBT.Compound("destination", List.of(new SNBT.Strings("type", "block"), new SNBT.List("pos", List.of(new SNBT.Int(null, toX), new SNBT.Int(null, toY), new SNBT.Int(null, toZ))))));
                        //fromXYZ is now the position of the particle which means we have to outdribble the system a bit
                        extra = " " + fromX + " " + fromY + " " + fromZ;
                    }
                }
                if (!options.isEmpty())
                    data = new SNBT.Compound(null, options);
                else
                    data = null;
            }
            return true;
        }

        public String particle() {
            if (data == null)
                return name;
            return name + data.build() + (extra != null ? extra : "");
        }
    }
}
