package net.fairsquare.worlddownloader.tasks;

import net.fairsquare.worlddownloader.WorldDownloader;

public abstract class AsyncTask<T> implements Runnable {

    private final WorldDownloader plugin;
    private final AsyncCallback<T> callback;

    public AsyncTask(WorldDownloader plugin, AsyncCallback<T> callback) {
        this.plugin = plugin;
        this.callback = callback;
    }

    public WorldDownloader getPlugin() {
        return plugin;
    }

    public AsyncCallback<T> getCallback() {
        return callback;
    }

}
