package net.fairsquare.worlddownloader.tasks;

import net.fairsquare.worlddownloader.WorldDownloader;
import org.bukkit.Bukkit;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;

public class ZipTask extends AsyncTask<File> {

    private final File inputFile;
    private final File outputFile;

    public ZipTask(WorldDownloader plugin, File inputFile, File outputFile,
                   AsyncCallback<File> callback) {
        super(plugin, callback);
        this.inputFile = inputFile;
        this.outputFile = outputFile;
    }

    @Override
    public void run() {
        try {
            ZipUtil.pack(inputFile, outputFile);
        } catch (Exception ex) {
            Bukkit.getScheduler().runTask(getPlugin(), () -> getCallback().onFailure(ex));
            return;
        }
        Bukkit.getScheduler().runTask(getPlugin(), () -> getCallback().onComplete(outputFile));
    }

}
