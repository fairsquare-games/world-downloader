package net.fairsquare.worlddownloader.tasks;

import net.fairsquare.worlddownloader.WorldDownloader;
import org.bukkit.Bukkit;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;

public class ZipTask implements Runnable {

    private final WorldDownloader plugin;
    private final File input;
    private final File output;
    private final ZipCallback callback;

    public ZipTask(WorldDownloader plugin, File inputFile, File outputFile, ZipCallback callback) {
        this.plugin = plugin;
        this.input = inputFile;
        this.output = outputFile;
        this.callback = callback;
    }

    @Override
    public void run() {
        try {
            ZipUtil.pack(input, output);
        } catch (Exception ex) {
            Bukkit.getScheduler().runTask(plugin, () -> callback.onFailure(ex));
            return;
        }
        Bukkit.getScheduler().runTask(plugin, callback::onComplete);
    }

}
