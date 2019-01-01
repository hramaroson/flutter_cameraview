package io.github.hramaroson.fluttercamerakit;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.os.Build;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class CameraManager2 extends io.github.hramaroson.fluttercamerakit.CameraManager {
    private final Context context;

    CameraManager2(Context context) {
        this.context = context;
    }

    @Override
    public int getNumberOfCameras() {
        CameraManager manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        try {
            return manager.getCameraIdList().length;
        }
        catch (Throwable e){
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public List<Map<String,Object>> getCameraDescriptionList(){
        CameraManager manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        try {
            String[] cameraNames = manager.getCameraIdList();
            List<Map<String,Object>> cameras = new ArrayList<>();
            for (String cameraName : cameraNames) {
                HashMap<String, Object> details = new HashMap<>();
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraName);
                details.put("name", cameraName);

                @SuppressWarnings("ConstantConditions")
                int lensFacing = characteristics.get(CameraCharacteristics.LENS_FACING);
                switch (lensFacing) {
                    case CameraMetadata.LENS_FACING_FRONT:
                        details.put("lensFacing", "front");
                        break;
                    case CameraMetadata.LENS_FACING_BACK:
                        details.put("lensFacing", "back");
                        break;
                    case CameraMetadata.LENS_FACING_EXTERNAL:
                        details.put("lensFacing", "external");
                        break;
                }
                cameras.add(details);
            }
            return cameras;
        }
        catch (Throwable e){
            e.printStackTrace();
        }

        return null;
    }

    private static boolean isHardwareLevelSupported(CameraCharacteristics c, int requiredLevel) {
        int deviceLevel = c.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);

        if (deviceLevel == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY) {
            return requiredLevel == deviceLevel;
        }
        return requiredLevel <= deviceLevel;
    }

    public boolean allowCamera2Support(int cameraId) {
        CameraManager manager = (CameraManager)context.getSystemService(Context.CAMERA_SERVICE);
        try {
            String cameraIdS = manager.getCameraIdList()[cameraId];
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraIdS);
            return isHardwareLevelSupported(characteristics,
                    CameraMetadata.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED);
        }
        catch(Throwable e) {
            e.printStackTrace();
        }
        return false;
    }
}
