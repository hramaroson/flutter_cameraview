package io.github.hramaroson.fluttercamerakit.core;

import android.hardware.Camera;

@SuppressWarnings("deprecation")
public class CameraControllerManager1 extends CameraControllerManager {
    @Override
    public int getNumberOfCameras() {
        return Camera.getNumberOfCameras();
    }
}
