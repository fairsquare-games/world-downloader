package net.fairsquare.worlddownloader.commands;

import net.fairsquare.worlddownloader.WorldDownloader;
import net.fairsquare.worlddownloader.models.Message;
import net.fairsquare.worlddownloader.tasks.AsyncCallback;
import net.fairsquare.worlddownloader.tasks.WebUploaderTask;
import net.fairsquare.worlddownloader.tasks.ZipTask;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
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

        /* Fetch the world directory */
        File worldDirectory = getWorldDirectory(worldName);
        if (worldDirectory == null) {
            if (args.length > 0) {
                Message.WORLD_NOT_FOUND_NAME.send(sender, args[0]);
            } else {
                Message.WORLD_NOT_FOUND.send(sender);
            }
            return true;
        }

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

    /**
     * Parses the world name from either the location of the command sender (player) or the command
     * arguments.
     *
     * @param sender The command sender.
     * @param args   The command arguments.
     * @return The name of the world, or null if the world name could not be established.
     */
    private String getWorldName(CommandSender sender, String[] args) {
        if (args.length > 0) {
            return args[0];
        } else if (sender instanceof Player) {
            Player player = (Player) sender;
            return player.getWorld().getName();
        }
        return null;
    }

    /**
     * Gets the world directory using the provided world name. Returns null if the world directory
     * could not be retrieved (due to an invalid world name).
     *
     * @param worldName The name of the world to retrieve.
     * @return The directory of the world, or null if no world could be found.
     */
    private File getWorldDirectory(String worldName) {
        /* Get the server directory */
        File worldContainer = plugin.getServer().getWorldContainer();

        /* Fetch the world directory */
        File[] matchedWorldDirectories = worldContainer
                .listFiles((dir, name) -> name.equals(worldName));
        if (matchedWorldDirectories == null || matchedWorldDirectories.length != 1) {
            return null;
        }
        return matchedWorldDirectories[0];
    }

    /**
     * This method zips the world and continues with uploading the world afterwards.
     *
     * @param sender         The command sender.
     * @param worldDirectory The world directory to zip.
     * @param destination    The destination file to zip to.
     */
    private void zipWorld(final CommandSender sender, final File worldDirectory,
                          final File destination) {
        Message.CREATING_ZIP.send(sender, worldDirectory.getName());
        disableWorldSaving();
        ZipTask zipTask = new ZipTask(plugin, worldDirectory, destination,
                new AsyncCallback<File>() {
                    @Override
                    public void onComplete(File zippedWorldDirectory) {
                        enableWorldSaving();
                        Message.CREATED_ZIP.send(sender, worldDirectory.getName());
                        uploadWorld(sender, zippedWorldDirectory);
                    }

                    @Override
                    public void onFailure(Exception ex) {
                        enableWorldSaving();
                        Message.ERROR_CREATING_ZIP.send(sender);
                        Bukkit.getLogger().log(Level.SEVERE, "Could not create zip archive", ex);
                    }
                });
        Bukkit.getScheduler().runTaskAsynchronously(plugin, zipTask);
    }

    /**
     * This method uploads the world and sends the command sender the download URL.
     *
     * @param sender               The command sender.
     * @param zippedWorldDirectory The zip file to upload.
     */
    private void uploadWorld(final CommandSender sender, final File zippedWorldDirectory) {
        Message.UPLOADING_ZIP.send(sender);
        WebUploaderTask uploadTask = new WebUploaderTask(plugin, zippedWorldDirectory,
                new AsyncCallback<String>() {
                    @Override
                    public void onComplete(String downloadUrl) {
                        Message.UPLOADED_ZIP.send(sender);

                        BaseComponent[] components = Message.DOWNLOAD_URL.getTextComponent(downloadUrl);
                        for (BaseComponent component : components) {
                            component.setClickEvent(
                                    new ClickEvent(ClickEvent.Action.OPEN_URL, downloadUrl));
                        }
                        sender.spigot().sendMessage(components);
                    }

                    @Override
                    public void onFailure(Exception ex) {
                        Message.ERROR_UPLOADING_ZIP.send(sender);
                        Bukkit.getLogger().log(Level.SEVERE, "Could not upload zip archive", ex);
                    }
                });
        Bukkit.getScheduler().runTaskAsynchronously(plugin, uploadTask);
    }

    /**
     * Retrieves the plugin's data folder.
     *
     * @return The plugin's data folder.
     */
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

    /**
     * Retrieves the world output folder.
     *
     * @return The world output folder.
     */
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

    /**
     * Formats the name of the zip that is created.
     *
     * @param worldName The name of the world to zip.
     * @return The name of the zip archive that contains the world.
     */
    private String getZipName(String worldName) {
        LocalDateTime time = LocalDateTime.now(ZoneOffset.UTC);
        return worldName + "_" + time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmm"))
                + ".zip";
    }

    /**
     * Enables automatic world saving.
     */
    private void enableWorldSaving() {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-on");
    }

    /**
     * Disables automatic world saving.
     */
    private void disableWorldSaving() {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-off");
    }

}
