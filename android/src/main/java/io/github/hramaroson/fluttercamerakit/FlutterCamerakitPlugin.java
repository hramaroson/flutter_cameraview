package io.github.hramaroson.fluttercamerakit;

import android.app.Activity;

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

    private final FlutterView view;
    private Registrar registrar;
    private Activity activity;
    private Runnable cameraPermissionContinuation;

    private FlutterCamerakitPlugin(Registrar registrar, FlutterView view, Activity activity) {
        this.registrar = registrar;
        this.view = view;
        this.activity = activity;

        registrar.addRequestPermissionsResultListener(new CameraRequestPermissionsListener());

    }
    /**
     * Plugin registration.
     */
    public static void registerWith(Registrar registrar) {
        final MethodChannel channel = new MethodChannel(registrar.messenger(), "flutter_camerakit");

        channel.setMethodCallHandler(
                new FlutterCamerakitPlugin(registrar, registrar.view(), registrar.activity()));
    }

    @Override
    public void onMethodCall(MethodCall call, Result result) {
        switch (call.method) {
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
}
