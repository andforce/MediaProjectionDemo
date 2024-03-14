package com.cry.cry.mediaprojectioncode;

import android.view.Surface;

import androidx.annotation.Nullable;

public interface SurfaceFactory {

    @Nullable
    Surface createSurface(int width, int height);

    void stop();
}
