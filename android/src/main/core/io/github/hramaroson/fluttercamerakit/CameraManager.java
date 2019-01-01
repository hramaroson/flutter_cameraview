package io.github.hramaroson.fluttercamerakit;

import java.util.List;
import java.util.Map;

abstract class CameraManager {
    public abstract int getNumberOfCameras();
    public abstract List<Map<String,Object>> getCameraDescriptionList();
}
