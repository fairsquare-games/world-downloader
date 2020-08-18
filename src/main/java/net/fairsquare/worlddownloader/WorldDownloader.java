package net.fairsquare.worlddownloader;

import net.fairsquare.worlddownloader.commands.DownloadCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class WorldDownloader extends JavaPlugin {

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.getCommand("downloadworld").setExecutor(new DownloadCommand(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

}
