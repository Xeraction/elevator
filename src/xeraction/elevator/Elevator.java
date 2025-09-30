package xeraction.elevator;

import net.querz.mca.Chunk;
import net.querz.mca.MCAFile;
import net.querz.mca.MCAUtil;
import net.querz.nbt.tag.CompoundTag;
import xeraction.elevator.command.*;
import xeraction.elevator.command.legacy.*;
import xeraction.elevator.util.LegacyData;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Elevator {
    private static boolean debug = false;
    public static boolean executeTP = false;
    private static Mode mode = null;
    private static boolean noWarnings = false;
    private static int upgradesCommands = 0;

    private static int currentX = 0;
    private static int currentY = 0;
    private static int currentZ = 0;
    private static StringBuilder debugBuilder;

    public static void main(String[] args) {
        setup();

        if (args.length == 0) {
            System.out.println("--Elevator--");
            System.out.println("Usage: java -jar elevator.jar [options] <value>");
            System.out.println(" ");
            System.out.println("Available options:");
            System.out.println("#one of the following is required#");
            System.out.println("-world / -w : Elevates an entire world");
            System.out.println("-file / -f : Elevates a file with commands (one command per line, FUNCTION MACROS ARE NOT SUPPORTED)");
            System.out.println("-single / -s : Elevates a single command (remember to escape quotes)");
            System.out.println(" ");
            System.out.println("#these are optional#");
            System.out.println("-debug / -d : Prints out additional details and doesn't save in world and file mode");
            System.out.println("-no-warnings / -nw : Doesn't print command warnings (mostly NBT-related). Still prints errors.");
            System.out.println("-tp / -t : Puts tp commands that use ~-notation in an execute command to be executed by the target due to changes to command execution locations");
            System.out.println(" ");
            System.out.println("<value> :");
            System.out.println("  -in world mode: path to the world folder");
            System.out.println("  -in file mode: path to the file (will replace the file)");
            System.out.println("  -in single mode: the command to elevate");
        }

        int valueBegin = -1;
        for (int i = 0; i < args.length; i++) {
            if (!args[i].startsWith("-")) {
                valueBegin = i;
                break;
            }
            switch (args[i]) {
                case "-world", "-w" -> mode = Mode.World;
                case "-file", "-f" -> mode = Mode.File;
                case "-single", "-s" -> mode = Mode.Single;
                case "-debug", "-d" -> debug = true;
                case "-no-warnings", "-nw" -> noWarnings = true;
                case "-tp", "-t" -> executeTP = true;
                default -> {
                    System.out.println("Unknown option " + args[i]);
                    return;
                }
            }
        }

        if (valueBegin == -1) {
            System.out.println("Missing value");
            return;
        }

        if (mode == null) {
            System.out.println("Mode not specified");
            return;
        }

        String path = "";
        for (int i = valueBegin; i < args.length; i++) {
            path += args[i] + " ";
        }
        path = path.stripTrailing();

        if (debug)
            debugBuilder = new StringBuilder();

        switch (mode) {
            case World -> {
                File file = new File(path + "/region");
                if (!file.exists()) {
                    debugLine("Could not find world folder " + path);
                    return;
                }
                try {
                    File[] mcaFiles = file.listFiles((dir, name) -> name.endsWith(".mca"));
                    if (mcaFiles == null) {
                        debugLine("No region files found");
                        return;
                    }
                    int count = 1;
                    for (File f : mcaFiles) {
                        debugLine("");
                        debugLine(f.getName() + " (" + count++ + "/" + mcaFiles.length + ")");
                        MCAFile mca = MCAUtil.read(f);
                        for (Chunk chunk : mca) {
                            if (chunk == null || chunk.getTileEntities() == null)
                                continue;
                            for (CompoundTag tag : chunk.getTileEntities()) {
                                if (!tag.containsKey("id") || (!tag.getString("id").equals("minecraft:command_block") && !tag.getString("id").equals("Control")))
                                    continue;
                                if (!tag.containsKey("Command") || tag.getString("Command").isEmpty())
                                    continue;
                                currentX = tag.getInt("x");
                                currentY = tag.getInt("y");
                                currentZ = tag.getInt("z");
                                String command = tag.getString("Command");
                                Command cmd = parseCommand(command);
                                if (!debug && cmd != null)
                                    tag.putString("Command", cmd.build());
                            }
                        }
                        if (!debug)
                            MCAUtil.write(mca, f);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            case File -> {
                File file = new File(path);
                if (!file.exists()) {
                    debugLine("Could not find file " + path);
                    return;
                }
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    List<String> elevated = new ArrayList<>();
                    while (true) {
                        String line = reader.readLine();
                        if (line == null)
                            break;
                        if (line.isEmpty() || line.startsWith("#")) //no function support eh?
                            continue;
                        Command cmd = parseCommand(line);
                        if (cmd != null)
                            elevated.add(cmd.build());
                    }
                    if (!debug) {
                        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                        for (String s : elevated) {
                            writer.write(s);
                            writer.newLine();
                        }
                        writer.flush();
                        writer.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            case Single -> {
                Command cmd = parseCommand(path);
                if (cmd != null)
                    debugLine(cmd.build());
            }
        }

        debugLine("");
        if (upgradesCommands == 0)
            debugLine("All commands are up to date!");
        else
            debugLine("Upgraded " + upgradesCommands + " commands.");

        if (debug) {
            File debugFile = new File("debug.txt");
            try {
                if (!debugFile.exists())
                    debugFile.createNewFile();
                BufferedWriter writer = new BufferedWriter(new FileWriter(debugFile));
                writer.write(debugBuilder.toString());
                writer.flush();
                writer.close();
            } catch (Exception e) {
                System.out.println("Failed to write debug log to file.");
            }
        }
    }

    public static Command parseCommand(String command) {
        try {
            if (command.startsWith("/"))
                command = command.substring(1);
            command = command.stripTrailing();

            String cmd;
            if (!command.contains(" "))
                cmd = command;
            else
                cmd = command.substring(0, command.indexOf(' '));
            if (!commands.containsKey(cmd))
                return null;

            StringIterator iterator = new StringIterator(command);
            ParseSequence<? extends Command> sq = commands.get(cmd);
            Command parsed = sq.parse(iterator);

            if (parsed == null)
                throw new ParseException("Failed to parse command '" + cmd + "': incorrect syntax structure.");

            boolean same = command.equals(parsed.build());
            if (!same)
                upgradesCommands++;

            if (debug && !same) {
                debugLine("");
                if (mode == Mode.World)
                    debugLine(currentX + " " + currentY + " " + currentZ);
                debugLine(command);
                debugLine("->");
                debugLine(parsed.build());
            }

            return parsed;
        } catch (Exception e) {
            debugLine("");
            debugLine("Error while elevating command:");
            debugLine(command);

            if (mode == Mode.World)
                debugLine("at position " + currentX + " " + currentY + " " + currentZ);

            debugLine(e.toString());
            for (StackTraceElement trace : e.getStackTrace()) //printStackTrace is for some reason very unreliable here
                debugLine(trace.toString());
            return null;
        }
    }

    private static void debugLine(String line) {
        if (line.isEmpty()) {
            if (debug)
                debugBuilder.append("\n");
            System.out.println(" ");
        } else {
            if (debug)
                debugBuilder.append(line).append("\n");
            System.out.println(line);
        }
    }

    public static void warn(String msg) {
        if (noWarnings)
            return;

        debugLine("");
        if (mode == Mode.World)
            debugLine("WARNING FOR COMMAND AT " + currentX + " " + currentY + " " + currentZ + ":");
        else
            debugLine("WARNING:");
        debugLine(msg);
    }

    private enum Mode {
        World, File, Single
    }

    private static final Map<String, ParseSequence<? extends Command>> commands = new HashMap<>();

    private static void setup() {
        LegacyData.prepareFlatteningData();

        if (!commands.isEmpty())
            return;

        commands.put("advancement", AdvancementCommand.SEQUENCE);
        commands.put("attribute", AttributeCommand.SEQUENCE);
        commands.put("ban", BanCommand.SEQUENCE);
        commands.put("ban-ip", BanIpCommand.SEQUENCE);
        commands.put("banlist", BanlistCommand.SEQUENCE);
        commands.put("bossbar", BossbarCommand.SEQUENCE);
        commands.put("clear", ClearCommand.SEQUENCE);
        commands.put("clone", CloneCommand.SEQUENCE);
        commands.put("damage", DamageCommand.SEQUENCE);
        commands.put("data", DataCommand.SEQUENCE);
        commands.put("datapack", DatapackCommand.SEQUENCE);
        commands.put("debug", DebugCommand.SEQUENCE);
        commands.put("defaultgamemode", DefaultgamemodeCommand.SEQUENCE);
        commands.put("deop", DeopCommand.SEQUENCE);
        commands.put("dialog", DialogCommand.SEQUENCE);
        commands.put("difficulty", DifficultyCommand.SEQUENCE);
        commands.put("effect", EffectCommand.SEQUENCE);
        commands.put("enchant", EnchantCommand.SEQUENCE);
        commands.put("execute", ExecuteCommand.SEQUENCE);
        commands.put("experience", ExperienceCommand.SEQUENCE);
        commands.put("fetchprofile", FetchprofileCommand.SEQUENCE);
        commands.put("fill", FillCommand.SEQUENCE);
        commands.put("fillbiome", FillbiomeCommand.SEQUENCE);
        commands.put("forceload", ForceloadCommand.SEQUENCE);
        commands.put("function", FunctionCommand.SEQUENCE);
        commands.put("gamemode", GamemodeCommand.SEQUENCE);
        commands.put("gamerule", GameruleCommand.SEQUENCE);
        commands.put("give", GiveCommand.SEQUENCE);
        commands.put("help", HelpCommand.SEQUENCE);
        commands.put("item", ItemCommand.SEQUENCE);
        commands.put("jfr", JfrCommand.SEQUENCE);
        commands.put("kick", KickCommand.SEQUENCE);
        commands.put("kill", KillCommand.SEQUENCE);
        commands.put("list", ListCommand.SEQUENCE);
        commands.put("locate", LocateCommand.SEQUENCE);
        commands.put("loot", LootCommand.SEQUENCE);
        commands.put("me", MeCommand.SEQUENCE);
        commands.put("msg", MsgCommand.SEQUENCE);
        commands.put("op", OpCommand.SEQUENCE);
        commands.put("pardon", PardonCommand.SEQUENCE);
        commands.put("pardon-ip", PardonIpCommand.SEQUENCE);
        commands.put("particle", ParticleCommand.SEQUENCE);
        commands.put("perf", PerfCommand.SEQUENCE);
        commands.put("place", PlaceCommand.SEQUENCE);
        commands.put("playsound", PlaysoundCommand.SEQUENCE);
        commands.put("publish", PublishCommand.SEQUENCE);
        commands.put("random", RandomCommand.SEQUENCE);
        commands.put("recipe", RecipeCommand.SEQUENCE);
        commands.put("reload", ReloadCommand.SEQUENCE);
        //return command - datapacks are not supported, so i won't bother
        commands.put("ride", RideCommand.SEQUENCE);
        commands.put("rotate", RotateCommand.SEQUENCE);
        commands.put("save-all", SaveAllCommand.SEQUENCE);
        commands.put("save-off", SaveOnOffCommand.SEQUENCE);
        commands.put("save-on", SaveOnOffCommand.SEQUENCE);
        commands.put("say", SayCommand.SEQUENCE);
        commands.put("schedule", ScheduleCommand.SEQUENCE);
        commands.put("scoreboard", ScoreboardCommand.SEQUENCE);
        commands.put("seed", SeedCommand.SEQUENCE);
        commands.put("setblock", SetblockCommand.SEQUENCE);
        commands.put("setworldspawn", SetworldspawnCommand.SEQUENCE);
        commands.put("spawnpoint", SpawnpointCommand.SEQUENCE);
        commands.put("spectate", SpectateCommand.SEQUENCE);
        commands.put("spreadplayers", SpreadplayersCommand.SEQUENCE);
        commands.put("stop", StopCommand.SEQUENCE);
        commands.put("stopsound", StopsoundCommand.SEQUENCE);
        commands.put("summon", SummonCommand.SEQUENCE);
        commands.put("tag", TagCommand.SEQUENCE);
        commands.put("team", TeamCommand.SEQUENCE);
        commands.put("teammsg", TeammsgCommand.SEQUENCE);
        commands.put("teleport", TeleportCommand.SEQUENCE);
        commands.put("tell", MsgCommand.SEQUENCE);
        commands.put("tellraw", TellrawCommand.SEQUENCE);
        commands.put("test", TestCommand.SEQUENCE);
        commands.put("tick", TickCommand.SEQUENCE);
        commands.put("time", TimeCommand.SEQUENCE);
        commands.put("title", TitleCommand.SEQUENCE);
        commands.put("tm", TeammsgCommand.SEQUENCE);
        commands.put("tp", TeleportCommand.SEQUENCE);
        commands.put("transfer", TransferCommand.SEQUENCE);
        commands.put("trigger", TriggerCommand.SEQUENCE);
        commands.put("version", VersionCommand.SEQUENCE);
        commands.put("w", MsgCommand.SEQUENCE);
        commands.put("waypoint", WaypointCommand.SEQUENCE);
        commands.put("weather", WeatherCommand.SEQUENCE);
        commands.put("whitelist", WhitelistCommand.SEQUENCE);
        commands.put("worldborder", WorldborderCommand.SEQUENCE);
        commands.put("xp", ExperienceCommand.SEQUENCE);

        //legacy commands - translate to new commands on build (ordered most to least recent)
        commands.put("placefeature", PlacefeatureCommand.SEQUENCE);
        commands.put("locatebiome", LocatebiomeCommand.SEQUENCE);
        commands.put("replaceitem", ReplaceitemCommand.SEQUENCE);
        commands.put("stats", StatsCommand.SEQUENCE);
        commands.put("testfor", TestforCommand.SEQUENCE);
        commands.put("testforblock", TestforblockCommand.SEQUENCE);
        commands.put("testforblocks", TestforblockCommand.SEQUENCE);
        commands.put("toggledownfall", ToggledownfallCommand.SEQUENCE);
        commands.put("entitydata", EntitydataCommand.SEQUENCE);
        commands.put("blockdata", BlockdataCommand.SEQUENCE);
    }
}
