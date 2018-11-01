package com.mikechoch.prism.callback.action;

import android.net.Uri;

public interface OnUploadFileCallback {

    void onFileUploadSuccess(Uri downloadUri);
    void onProgressUpdate(int progress);
    void onFailure(Exception e);
}
