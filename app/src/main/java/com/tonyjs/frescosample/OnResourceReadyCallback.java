package com.tonyjs.frescosample;

import android.graphics.drawable.Drawable;

/**
 * Created by tonyjs on 15. 12. 8..
 */
public interface OnResourceReadyCallback {
    void onReady(Drawable bitmap);

    void onFail(Throwable cause);

    void onProgressUpdate(float progress);
}
