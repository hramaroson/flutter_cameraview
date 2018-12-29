package io.github.hramaroson.fluttercamerakit;

import static android.view.OrientationEventListener.ORIENTATION_UNKNOWN;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.OrientationEventListener;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import io.flutter.view.FlutterView;

import io.github.hramaroson.fluttercamerakit.core.CameraException;
import io.github.hramaroson.fluttercamerakit.util.Size;

public class FlutterCamerakitPlugin implements MethodCallHandler {
    private static final int CAMERA_REQUEST_ID = 513469796;
    private static final String TAG = "FlutterCamerakitPlugin";

    private final FlutterView view;
    private Registrar registrar;
    private Camera camera;
    private Activity activity;
    private Application.ActivityLifecycleCallbacks activityLifecycleCallbacks;
    private Runnable cameraPermissionContinuation;
    private boolean requestingPermission;
    private final OrientationEventListener orientationEventListener;
    private int currentOrientation = ORIENTATION_UNKNOWN;

    private FlutterCamerakitPlugin(Registrar registrar, FlutterView view, Activity activity) {
        this.registrar = registrar;
        this.view = view;
        this.activity = activity;

        orientationEventListener = new OrientationEventListener(activity.getApplicationContext()) {
            @Override
            public void onOrientationChanged(int i) {
                if(i == ORIENTATION_UNKNOWN){
                    return;
                }
                // Convert the raw deg angle to the nearest multiple of 90.
                currentOrientation = (int) Math.round(i / 90.0) * 90;
            }
        };

        registrar.addRequestPermissionsResultListener(new CameraRequestPermissionsListener());

        this.activityLifecycleCallbacks = new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
                if(requestingPermission) {
                    requestingPermission = false;
                    return;
                }
                orientationEventListener.enable();
                if(activity == FlutterCamerakitPlugin.this.activity) {
                    if(camera != null){
                        camera.open(null);
                    }
                }

            }

            @Override
            public void onActivityPaused(Activity activity) {
                if (activity == FlutterCamerakitPlugin.this.activity) {
                    orientationEventListener.disable();
                    if (camera != null) {
                        camera.close();
                    }
                }

            }

            @Override
            public void onActivityStopped(Activity activity) {
                if (activity == FlutterCamerakitPlugin.this.activity) {
                    if (camera != null) {
                        camera.close();
                    }
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        };

    }
    /**
     * Plugin registration.
     */
    public static void registerWith(Registrar registrar) {
        final MethodChannel channel = new MethodChannel(registrar.messenger(),
                "flutter_camerakit");

        channel.setMethodCallHandler(
                new FlutterCamerakitPlugin(registrar, registrar.view(), registrar.activity()));
    }

    @Override
    public void onMethodCall(MethodCall call, Result result) {
        switch (call.method) {
            case "init": {
                if(camera != null){
                    camera.close();
                }
                result.success(null);
                break;
            }
            case "initialize": {
                String cameraName = call.argument("cameraName");
                String resolutionPreset = call.argument("resolutionPreset");
                if(camera != null){
                    camera.close();
                }
                camera = new Camera(cameraName,resolutionPreset, result);

                this.activity.getApplication().registerActivityLifecycleCallbacks(
                        this.activityLifecycleCallbacks);
                break;
            }
            case "dispose":{
                if(camera != null){
                    camera.dispose();
                }
                if(this.activity != null && this.activityLifecycleCallbacks !=null){
                    this.activity.getApplication().unregisterActivityLifecycleCallbacks(
                            this.activityLifecycleCallbacks);
                }
                result.success(null);
                break;
            }
            default:
                result.notImplemented();
                break;
        }
    }

    private class CameraRequestPermissionsListener
            implements PluginRegistry.RequestPermissionsResultListener {
        @Override
        public boolean onRequestPermissionsResult(int id, String[] permissions, int[] grantResults) {
            if (id == CAMERA_REQUEST_ID) {
                cameraPermissionContinuation.run();
                return true;
            }
            return false;
        }
    }
    private class Camera {
        private final FlutterView.SurfaceTextureEntry textureEntry;
        private String cameraName;
        private Size previewSize;

        private Camera(final String cameraName, final String resolutionPreset,
                      @NonNull final MethodChannel.Result result) {
            this.cameraName = cameraName;
            textureEntry = view.createSurfaceTexture();

            try {
                Size minPreviewSize;
                switch(resolutionPreset){
                    case "high":
                        minPreviewSize = new Size(1024, 768);
                        break;

                    case "medium":
                        minPreviewSize = new Size(640, 480);
                        break;

                    case "low":
                        minPreviewSize = new Size(320, 240);
                        break;

                    default:
                        throw new IllegalArgumentException("Unkown preset: " + resolutionPreset);

                }
                if(cameraPermissionContinuation != null) {
                    result.error("cameraPermission", "Camera permission request ongoing", null);
                }
                cameraPermissionContinuation = new Runnable() {
                    @Override
                    public void run() {
                        cameraPermissionContinuation = null;
                        if(!hasCameraPermission()){
                            result.error("cameraPermission", "Camera permission not granted", null);
                            return;
                        }
                        open(result);
                    }
                };
            }
            catch (CameraException e){
                result.error("CameraAccess" ,e.getMessage(),null);
            }
            catch (IllegalArgumentException e){
                result.error("IllegalArgumentException", e.getMessage(),null);
            }
        }

        private boolean hasCameraPermission() {
            return Build.VERSION.SDK_INT < Build.VERSION_CODES.M
                    || (activity.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
        }
        private void open (@Nullable Result result){

        }
        public void close() {

        }

        public void dispose(){
            close();
        }
    }
}
