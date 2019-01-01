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

import java.util.List;
import java.util.Map;

import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import io.flutter.view.FlutterView;

public class FlutterCamerakitPlugin implements MethodCallHandler {
    private static final int CAMERA_REQUEST_ID = 513469796;
    private static final String TAG = "FlutterCamerakitPlugin";

    private static CameraManager mCameraManager;
    private static boolean mHasCamera2Support;

    private final FlutterView mView;
    private Registrar mRegistrar;
    private Camera mCamera;
    private Activity mActivity;
    private Application.ActivityLifecycleCallbacks mActivityLifecycleCallbacks;
    private Runnable mCameraPermissionContinuation;
    private boolean mRequestingPermission;
    private final OrientationEventListener mOrientationEventListener;
    private int mCurrentOrientation = ORIENTATION_UNKNOWN;

    private FlutterCamerakitPlugin(Registrar registrar, FlutterView view, Activity activity) {
        this.mRegistrar = registrar;
        this.mView = view;
        this.mActivity = activity;

        initSupportCamera2(null);
        if(mHasCamera2Support){
            mCameraManager = new CameraManager2(activity);
        }
        else {
            mCameraManager = new CameraManager1();
        }

        mOrientationEventListener = new OrientationEventListener(this.mActivity.getApplicationContext()) {
            @Override
            public void onOrientationChanged(int i) {
                if(i == ORIENTATION_UNKNOWN){
                    return;
                }
                // Convert the raw deg angle to the nearest multiple of 90.
                mCurrentOrientation = (int) Math.round(i / 90.0) * 90;
            }
        };

        mRegistrar.addRequestPermissionsResultListener(new CameraRequestPermissionsListener());

        this.mActivityLifecycleCallbacks = new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
                if(mRequestingPermission) {
                    mRequestingPermission = false;
                    return;
                }
                mOrientationEventListener.enable();
                if(activity == FlutterCamerakitPlugin.this.mActivity) {
                    if(mCamera != null){
                        mCamera.open(null);
                    }
                }

            }

            @Override
            public void onActivityPaused(Activity activity) {
                if (activity == FlutterCamerakitPlugin.this.mActivity) {
                    mOrientationEventListener.disable();
                    if (mCamera != null) {
                        mCamera.close();
                    }
                }

            }

            @Override
            public void onActivityStopped(Activity activity) {
                if (activity == FlutterCamerakitPlugin.this.mActivity) {
                    if (mCamera != null) {
                        mCamera.close();
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
                "plugins.flutter.io/flutter_camerakit");

        channel.setMethodCallHandler(
                new FlutterCamerakitPlugin(registrar, registrar.view(), registrar.activity()));
    }

    @Override
    public void onMethodCall(MethodCall call, Result result) {
        switch (call.method) {
            case "init": {
                if(mCamera != null){
                    mCamera.close();
                }
                result.success(null);
                break;
            }
            case "availableCameras": {
                List<Map<String,Object>> cameras = mCameraManager.getCameraDescriptionList();
                result.success(cameras);
                break;
            }
            case "initialize": {
                String cameraName = call.argument("cameraName");
                if(mCamera != null){
                    mCamera.close();
                }
                mCamera = new Camera(cameraName, result);

                this.mActivity.getApplication().registerActivityLifecycleCallbacks(
                        this.mActivityLifecycleCallbacks);
                break;
            }
            case "dispose":{
                if(mCamera != null){
                    mCamera.dispose();
                }
                if(this.mActivity != null && this.mActivityLifecycleCallbacks !=null){
                    this.mActivity.getApplication().unregisterActivityLifecycleCallbacks(
                            this.mActivityLifecycleCallbacks);
                }
                result.success(null);
                break;
            }
            default:
                result.notImplemented();
                break;
        }
    }

    private  void initSupportCamera2(@Nullable Result result) {
        mHasCamera2Support = false;
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {
            CameraManager2 manager2 = new CameraManager2(mActivity);
            mHasCamera2Support = true;
            if( manager2.getNumberOfCameras() == 0 ) {
                mHasCamera2Support = false;
            }
            for(int i=0;i<manager2.getNumberOfCameras() && mHasCamera2Support;i++) {
                if( !manager2.allowCamera2Support(i) ) {
                    mHasCamera2Support = false;
                }
            }
        }
    }

    private class CameraRequestPermissionsListener
            implements PluginRegistry.RequestPermissionsResultListener {
        @Override
        public boolean onRequestPermissionsResult(int id, String[] permissions, int[] grantResults) {
            if (id == CAMERA_REQUEST_ID) {
                mCameraPermissionContinuation.run();
                return true;
            }
            return false;
        }
    }

    public class Camera {
        private final FlutterView.SurfaceTextureEntry mTextureEntry;
        private EventChannel.EventSink mEventSink;
        private String mCameraName;
        private OrientationHelper mOrientationHelper;
        private CameraController mCameraController;
        private CameraCallbacks mCameraCallbacks;

        private Camera(final String cameraName, @NonNull final MethodChannel.Result result) {
            this.mCameraName = cameraName;
            this.mTextureEntry = mView.createSurfaceTexture();
            registerEventChannel();

            if(mCameraPermissionContinuation != null) {
                result.error("cameraPermission", "Camera permission request ongoing", null);
            }
            mCameraPermissionContinuation = new Runnable() {
                @Override
                public void run() {
                    mCameraPermissionContinuation = null;
                    if(!hasCameraPermission()){
                        result.error("cameraPermission", "Camera permission not granted", null);
                            return;
                    }
                    open(result);
                }
            };
            mRequestingPermission = false;
            if (hasCameraPermission()) {
                mCameraPermissionContinuation.run();
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    mRequestingPermission = true;
                    mRegistrar.activity().requestPermissions(
                            new String[] {Manifest.permission.CAMERA}, CAMERA_REQUEST_ID);
                }
            }

            mCameraCallbacks = new Callbacks();
            mCameraController = instantiateCameraController(mCameraCallbacks);

            mOrientationHelper = new OrientationHelper(mActivity, mCameraCallbacks);
        }

        private void registerEventChannel() {
            new EventChannel(
                    mRegistrar.messenger(), "flutter.io/flutterCameraKitPlugin/cameraEvents"
                    + mTextureEntry.id())
                    .setStreamHandler(
                            new EventChannel.StreamHandler() {
                                @Override
                                public void onListen(Object arguments, EventChannel.EventSink eventSink) {
                                    Camera.this.mEventSink = eventSink;
                                }

                                @Override
                                public void onCancel(Object arguments) {
                                    Camera.this.mEventSink = null;
                                }
                            });
        }

        private boolean hasCameraPermission() {
            return Build.VERSION.SDK_INT < Build.VERSION_CODES.M
                    || (mActivity.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
        }

        private CameraController instantiateCameraController(CameraCallbacks callbacks) {
            if(mHasCamera2Support){
                return new CameraController2(callbacks);
            }

            return new CameraController1(callbacks);
        }

        private void open (@Nullable Result result){
            if(!hasCameraPermission()){
                if(result != null) result.error("CameraPermission", "Camera permission not granted", null);
            }

            mOrientationHelper.enable(mActivity);
            mCameraController.setDisplayOffset(mOrientationHelper.getDisplayOffset());
            mCameraController.start();
        }

        private void close() {
            mCameraController.stop();
        }

        private void dispose(){
            close();
            mTextureEntry.release();
        }
    }

    interface CameraCallbacks extends OrientationHelper.Callback {
        void dispatchOnCameraOpened(CameraOptions options);

    }

    private class Callbacks implements CameraCallbacks {
        Callbacks(){}

        @Override
        public void dispatchOnCameraOpened(final CameraOptions options) {

        }

        @Override
        public void onDeviceOrientationChanged(int deviceOrientation) {

        }
    }
}
