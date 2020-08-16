package net.fairsquare.worlddownloader.commands;

import net.fairsquare.worlddownloader.Message;
import net.fairsquare.worlddownloader.WorldDownloader;
import net.fairsquare.worlddownloader.tasks.ZipCallback;
import net.fairsquare.worlddownloader.tasks.ZipTask;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;

public class DownloadCommand implements CommandExecutor {

    private final WorldDownloader plugin;

    public DownloadCommand(WorldDownloader plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        /* Extract the world name */
        String worldName = getWorldName(sender, args);
        if (worldName == null) {
            Message.NOT_A_PLAYER.send(sender);
            Message.COMMAND_USAGE.send(sender);
            return true;
        }

        /* Get the server directory */
        File worldContainer = plugin.getServer().getWorldContainer();

        /* Fetch the world directory */
        File[] matchedWorldDirectories = worldContainer
                .listFiles((dir, name) -> name.equals(worldName));
        if (matchedWorldDirectories == null || matchedWorldDirectories.length != 1) {
            if (args.length > 0) {
                Message.WORLD_NOT_FOUND_NAME.send(sender, args[0]);
            } else {
                Message.WORLD_NOT_FOUND.send(sender);
            }
            return true;
        }
        File worldDirectory = matchedWorldDirectories[0];

        /* Get the output directory and file */
        File outputFolder = getOutputFolder();
        if (outputFolder == null) {
            Message.DATA_FOLDER_INACCESSIBLE.send(sender);
            return true;
        }
        File destination = new File(outputFolder + File.separator + getZipName(worldName));

        /* Zip the world and continue from there */
        zipWorld(sender, worldDirectory, destination);
        return true;
    }

    private String getWorldName(CommandSender sender, String[] args) {
        if (args.length > 0) {
            return args[0];
        } else if (sender instanceof Player) {
            Player player = (Player) sender;
            return player.getWorld().getName();
        }
        return null;
    }

    private void zipWorld(final CommandSender sender, final File worldDirectory,
                          final File destination) {
        Message.CREATING_ZIP.send(sender, worldDirectory.getName());
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-off");
        ZipTask zipTask = new ZipTask(plugin, worldDirectory, destination, new ZipCallback() {
            @Override
            public void onComplete() {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-on");
                Message.CREATED_ZIP.send(sender, worldDirectory.getName());
                //TODO: Continue from here with uploading...
            }

            @Override
            public void onFailure(Exception ex) {
                Message.ERROR_CREATING_ZIP.send(sender);
                Bukkit.getLogger().log(Level.SEVERE, "Could not create zip archive", ex);
            }
        });
        Bukkit.getScheduler().runTaskAsynchronously(plugin, zipTask);
    }

    private void uploadWorld() {

    }

    private File getDataFolder() {
        try {
            File dataFolder = plugin.getDataFolder();
            if (!dataFolder.exists()) {
                boolean success = dataFolder.mkdir();
                if (!success) {
                    return null;
                }
            }
            return dataFolder;
        } catch (Exception ex) {
            return null;
        }
    }

    private File getOutputFolder() {
        File dataFolder = getDataFolder();
        if (dataFolder == null) {
            return null;
        }

        try {
            File outputFolder = new File(dataFolder + File.separator + "out");
            if (!outputFolder.exists()) {
                boolean success = outputFolder.mkdir();
                if (!success) {
                    return null;
                }
            }
            return outputFolder;
        } catch (Exception ex) {
            return null;
        }
    }

    private String getZipName(String worldName) {
        LocalDateTime time = LocalDateTime.now(ZoneOffset.UTC);
        return worldName + "_" + time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmm"))
                + ".zip";
    }

}
