package io.github.hramaroson.fluttercamerakit;

import android.content.Context;
import android.view.View;

import com.otaliastudios.cameraview.CameraView;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import static io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.platform.PlatformView;

public class FlutterCamerakitPlugin implements PlatformView, MethodCallHandler {
    private final CameraView mCameraView;
    private final MethodChannel mMethodChanel;

    FlutterCamerakitPlugin (Context context, BinaryMessenger messenger, int id){
        mCameraView = new CameraView(context);
        mMethodChanel = new MethodChannel(messenger, "plugins.hramaroson.github.io/camerakit_" + id);
        mMethodChanel.setMethodCallHandler(this);
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
    public void dispose(){

    }
}
