package io.github.hramaroson.fluttercamerakit.core;

import java.util.List;
import java.util.Map;

public abstract  class CameraControllerManager {
    public abstract int getNumberOfCameras();
    public abstract List<Map<String,Object>> getCameraDescriptionList();

}
