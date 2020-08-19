package net.fairsquare.worlddownloader.tasks;

import net.fairsquare.worlddownloader.WorldDownloader;
import org.bukkit.Bukkit;

import java.io.File;

public class DeleteTask extends AsyncTask<String> {

    private final File zipFile;

    public DeleteTask(WorldDownloader plugin, File zipFile, AsyncCallback<String> callback) {
        super(plugin, callback);
        this.zipFile = zipFile;
    }

    @Override
    public void run() {
        boolean deleted = zipFile.delete();
        if (deleted) {
            Bukkit.getScheduler().runTask(getPlugin(),
                    () -> getCallback().onComplete("Deleted archive"));
        } else {
            Bukkit.getScheduler().runTask(getPlugin(),
                    () -> getCallback().onFailure(new RuntimeException("Could not delete archive")));
        }
    }

}
