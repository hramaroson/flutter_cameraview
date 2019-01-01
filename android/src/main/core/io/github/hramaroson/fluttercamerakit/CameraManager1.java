package io.github.hramaroson.fluttercamerakit;

import android.hardware.Camera;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")
class CameraManager1 extends CameraManager {
    @Override
    public int getNumberOfCameras() {
        return Camera.getNumberOfCameras();
    }

    @Override
    public List<Map<String, Object>> getCameraDescriptionList() {
        List<Map<String,Object>> cameras = new ArrayList<>();

        for(int i=0, camera_count = Camera.getNumberOfCameras(); i <camera_count ; i++){
            HashMap<String, Object> details = new HashMap<>();
            details.put("name", i);
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(i, cameraInfo);
            switch (cameraInfo.facing){
                case Camera.CameraInfo.CAMERA_FACING_FRONT:
                    details.put("lensFacing", "front");
                    break;
                case Camera.CameraInfo.CAMERA_FACING_BACK:
                    details.put("lensFacing", "back");
                    break;
            }
            cameras.add(details);
        }
        return cameras;
    }
}
