package net.fairsquare.worlddownloader.tasks;

public interface AsyncCallback<T> {

    void onComplete(T response);
    void onFailure(Exception ex);

}
