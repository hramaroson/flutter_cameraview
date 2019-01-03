package io.github.hramaroson.fluttercameraview;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.otaliastudios.cameraview.CameraView;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import static io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.platform.PlatformView;

public class FlutterCameraView implements PlatformView, MethodCallHandler, Application.ActivityLifecycleCallbacks {
    private final CameraView mCameraView;
    private final MethodChannel mMethodChanel;

    FlutterCameraView (Context context, BinaryMessenger messenger, int id, Activity activity){
        mCameraView = new CameraView(context);
        mMethodChanel = new MethodChannel(messenger, "plugins.hramaroson.github.io/camerakit_" + id);
        mMethodChanel.setMethodCallHandler(this);

        activity.getApplication().registerActivityLifecycleCallbacks(this);
    }

    @Override
    public View getView () {
        return mCameraView;
    }

    @Override
    public void onMethodCall(MethodCall methodCall, MethodChannel.Result result) {
        switch (methodCall.method) {
            default:
                result.notImplemented();
        }
    }
    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {}

    @Override
    public void onActivityStarted(Activity activity) {}

    @Override
    public void onActivityResumed(Activity activity) {
        mCameraView.start();
    }

    @Override
    public void onActivityPaused(Activity activity) {
        mCameraView.stop();
    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}

    @Override
    public void onActivityDestroyed(Activity activity) {}

    @Override
    public void dispose(){
        mCameraView.destroy();
    }
}
