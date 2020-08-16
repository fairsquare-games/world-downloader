package net.fairsquare.worlddownloader.tasks;

public interface ZipCallback {

    void onComplete();
    void onFailure(Exception ex);

}
