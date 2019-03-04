package io.github.hramaroson.fluttercameraview;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.PictureResult;
import com.otaliastudios.cameraview.FileCallback;
import com.otaliastudios.cameraview.Audio;
import com.otaliastudios.cameraview.Flash;
import com.otaliastudios.cameraview.Facing;
import com.otaliastudios.cameraview.Mode;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import static io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.platform.PlatformView;

import java.io.File;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;

public class FlutterCameraView implements PlatformView, MethodCallHandler, 
        Application.ActivityLifecycleCallbacks {
    private final CameraView mCameraView;
    private final MethodChannel mMethodChanel;
    private final Context mContext;

    private String mCapturedPictureFilePath;

    FlutterCameraView(Context context, BinaryMessenger messenger, int id, Activity activity){
        mContext = context;
        mCameraView = new CameraView(context);
        mMethodChanel = new MethodChannel(messenger, "plugins.hramaroson.github.io/cameraview_" + id);
        mMethodChanel.setMethodCallHandler(this);

        activity.getApplication().registerActivityLifecycleCallbacks(this);
        
        mCameraView.open();
        mCameraView.setMode(Mode.PICTURE);
        mCameraView.addCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(PictureResult result) { onPicture(result);}
        });
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
            case "getFacing":
                getFacing(methodCall, result);
                break;
            case "setFacing":
                setFacing(methodCall, result);
                break;
            case "setFlash": 
                setFlash(methodCall, result);
                break;
            case "getFlash":
                getFlash(methodCall, result);
                break;
            case "takePicture":
                takePicture(methodCall, result);
                break;
            default:
                result.notImplemented();
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {}

    @Override
    public void onActivityStarted(Activity activity) {
        mCameraView.open();
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

    private static Facing __facingValueFromIndex(int index){
        switch (index){
            case 0:
              return Facing.BACK;
            case 1:
              return Facing.FRONT;
            default:
              break;
        }
        return Facing.BACK;
    }

    private static int __facingIndexFromValue (Facing facing){
        switch (facing){
            case BACK:
                return 0;
            case FRONT:
                return 1;
            default:
                break;
        }
        return 0;
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

    private static int __flashIndexFromValue(Flash flash){
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

    private void isOpened(MethodCall methodCall, MethodChannel.Result result) {
        result.success(mCameraView.isOpened());
    }

    private void setFacing(MethodCall methodCall, MethodChannel.Result result){
        mCameraView.setFacing(__facingValueFromIndex((int) methodCall.arguments));
        result.success (null);
    }

    private void getFacing(MethodCall methodCall, MethodChannel.Result result){
        result.success(__facingIndexFromValue(mCameraView.getFacing()));
    }

    private void setFlash(MethodCall methodCall, MethodChannel.Result result){
        if (!mCameraView.isTakingPicture() && !mCameraView.isTakingVideo()){
            mCameraView.setFlash(__flashValueFromIndex((int) methodCall.arguments));
        }
        result.success (null);
    }
    
    private void getFlash(MethodCall methodCall, MethodChannel.Result result){
        result.success(__flashIndexFromValue(mCameraView.getFlash()));
    }

    private void takePicture(MethodCall methodCall, MethodChannel.Result result) {
        mCapturedPictureFilePath = (String) methodCall.arguments;

        mCameraView.takePicture();
        result.success (mCapturedPictureFilePath);
    }

    private void onPicture(PictureResult result){
        result.toFile(new File(mCapturedPictureFilePath), new FileCallback() {
            @UiThread
            public void onFileReady(@Nullable File file){
                if(file != null){
                    mMethodChanel.invokeMethod("pictureFileCreated", mCapturedPictureFilePath);
                    mCapturedPictureFilePath = ""; 
                }
            }
        });
    }
}
