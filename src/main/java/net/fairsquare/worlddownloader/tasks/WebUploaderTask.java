package net.fairsquare.worlddownloader.tasks;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fairsquare.worlddownloader.WorldDownloader;
import okhttp3.*;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;

public class WebUploaderTask extends AsyncTask<String> {

    private final File zipFile;
    private final String uploadUrl;
    private final String downloadUrl;

    public WebUploaderTask(WorldDownloader plugin, File zipFile, AsyncCallback<String> callback) {
        super(plugin, callback);
        this.zipFile = zipFile;
        this.uploadUrl = plugin.getConfig().getString("uploaders.web.upload-url");
        this.downloadUrl = plugin.getConfig().getString("uploaders.web.download-url");
    }

    @Override
    public void run() {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        MultipartBody requestBody = builder.setType(MultipartBody.FORM)
                .addFormDataPart("world", zipFile.getName(),
                        RequestBody.create(zipFile, MediaType.parse("application/zip")))
                .build();

        Request request = new Request.Builder()
                .url(this.uploadUrl)
                .post(requestBody)
                .build();

        OkHttpClient client = new OkHttpClient();
        try {
            Response response = client.newCall(request).execute();
            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                getCallback().onFailure(
                        new RuntimeException("No response body received from server"));
            } else {
                JsonObject obj = new JsonParser().parse(responseBody.string()).getAsJsonObject();
                if (obj.has("error")) {
                    Bukkit.getScheduler().runTask(getPlugin(),
                            () -> getCallback().onFailure(
                                    new RuntimeException(obj.get("error").getAsString())));
                } else {
                    Bukkit.getScheduler().runTask(getPlugin(),
                            () -> getCallback().onComplete(
                                    downloadUrl + "/" + obj.get("file").getAsString()));
                }
            }
        } catch (IOException e) {
            getCallback().onFailure(e);
        }
    }

}
