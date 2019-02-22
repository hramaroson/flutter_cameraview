package io.github.hramaroson.fluttercameraview;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.Audio;
import com.otaliastudios.cameraview.Flash;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import static io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.platform.PlatformView;

public class FlutterCameraView implements PlatformView, MethodCallHandler, Application.ActivityLifecycleCallbacks {
    private final CameraView mCameraView;
    private final MethodChannel mMethodChanel;
    private final Context mContext;

    FlutterCameraView(Context context, BinaryMessenger messenger, int id, Activity activity){
        mContext = context;
        mCameraView = new CameraView(context);
        mCameraView.setAudio(Audio.OFF);
        mMethodChanel = new MethodChannel(messenger, "plugins.hramaroson.github.io/cameraview_" + id);
        mMethodChanel.setMethodCallHandler(this);

        activity.getApplication().registerActivityLifecycleCallbacks(this);
        mCameraView.open();
    }

    @Override
    public View getView() {
        return mCameraView;
    }

    @Override
    public void onMethodCall(MethodCall methodCall, MethodChannel.Result result) {
        switch (methodCall.method) {
            case "isOpened":
                isOpened(methodCall, result);
                break;
            case "setFlash": 
                setFlash(methodCall, result);
                break;
            case "getFlash":
                getFlash(methodCall, result);
                break;
            case "takePicture":
                break;
            default:
                result.notImplemented();
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {}

    @Override
    public void onActivityStarted(Activity activity) {
    }

    @Override
    public void onActivityResumed (Activity activity) { 
        mCameraView.open();
    }

    @Override
    public void onActivityPaused(Activity activity) {
        mCameraView.close();
    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}

    @Override
    public void onActivityDestroyed(Activity activity) {
        mCameraView.destroy();
    }

    @Override
    public void dispose(){
        mCameraView.destroy();
    }

    private void isOpened(MethodCall methodCall, MethodChannel.Result result) {
        result.success(mCameraView.isOpened());
    }

    private static Flash __flashValueFromIndex(int index){
        switch (index){
            case 0:
              return Flash.OFF;
            case 1:
              return Flash.ON;
            case 2:
              return Flash.AUTO;
            case 3:
              return Flash.TORCH;
            default:
              break;
        }
        return Flash.OFF;
    }

    private int __flashIndexFromValue(Flash flash){
        switch (flash){
            case OFF:
                return 0;
            case ON:
                return 1;
            case AUTO:
                return 2;
            case TORCH:
                return 3;
            default:
                break;
        }
        return 0;
    }
    private void setFlash(MethodCall methodCall, MethodChannel.Result result){
        mCameraView.setFlash(__flashValueFromIndex((int) methodCall.arguments));
        result.success (null);
    }
    
    private void getFlash(MethodCall methodCall, MethodChannel.Result result){
        result.success(__flashIndexFromValue(mCameraView.getFlash()));
    }
}
