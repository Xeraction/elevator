# Elevator

A tool to upgrade commands in Minecraft.

It's not complete, and I doubt it ever will be, but it should work reliably for commands from 1.13 upwards.

The big issues with this is NBT data in commands like /summon or /data. It will try to upgrade what it can with the information given, but it may not work 100% of the time. Especially for older commands because the documentation for changes in NBT data back then is virtually non-existent.

Numeric block and item ids and their metadata from before 1.13 will upgrade. The Flattening is back.

## Usage
java -jar elevator.jar \<options\> \<path\>

The tool has 3 modes: world mode, file mode, and single mode. One of those is required.
### World Mode
- Option: -world / -w
- Upgrades the commands of an entire world. Specifiy the path to the world folder. BACK UP YOUR WORLD BEFORE USING! Things can always go wrong.
### File Mode
- Option: -file / -f
- Upgrades commands in a file. Specifiy the path to the file. The file will be overwritten. This can be used to upgrade a function file, note though that macro commands are not supported.
### Single Mode
- Option: -single / -s
- Upgrades a single command. Directly input the command as the last argument. Mostly for quick testing purposes on my side as quotes need to be escaped. Wouldn't recommend anyone to actually use it.

### Optional Options
- -debug / -d : Prints additional debug information, creates a log file and doesn't save in world and file mode.
- -no-warnings / -nw : Doesn't print command warnings (mostly NBT-related). Still prints errors.
- -tp / -t : Puts tp commands that use ~-notation in an execute command to be executed by the target due to changes to command execution locations.

I recommend running the tool in debug mode first and then do a quick check through the upgraded commands in the log file and verify the more complicated ones, especially when it comes to NBT data. DO NOT 100% RELY ON THIS TOOL!

## Notes
- I use a slightly modified version of Querz' NBT library which you can find [here](https://github.com/Querz/NBT).
- The code is a mess in some places. This is mostly because the process of finding the information I needed was a mess. The Minecraft Wiki is not that good when it comes to documenting past versions of commands or entity data.
- As of yet, the tool has only been field tested on the [Broville](oldshoes.ca) map, which was the primary reason I made it in the first place. If you have anything to provide in terms of old commands, or basically anything that can improve the tool, message me on Discord (@xeraction).
