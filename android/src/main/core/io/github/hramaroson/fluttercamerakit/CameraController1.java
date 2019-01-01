package io.github.hramaroson.fluttercamerakit;

import android.support.annotation.WorkerThread;

class CameraController1 extends CameraController {
    CameraController1 (FlutterCamerakitPlugin.CameraCallbacks cameraCallbacks){
        super(cameraCallbacks);
    }

    @WorkerThread
    @Override
    void onStart() {

    }

    @WorkerThread
    @Override
    void onStop() {

    }
}
