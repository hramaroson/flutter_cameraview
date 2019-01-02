package io.github.hramaroson.fluttercameraview;

import android.app.Activity;
import android.content.Context;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.StandardMessageCodec;
import io.flutter.plugin.platform.PlatformView;
import io.flutter.plugin.platform.PlatformViewFactory;

public class FlutterCameraViewFactory extends PlatformViewFactory{
    private final BinaryMessenger mMessenger;
    private Activity mActivity;

    public FlutterCameraViewFactory(BinaryMessenger messenger, Activity activity) {
        super(StandardMessageCodec.INSTANCE);
        this.mMessenger = messenger;
        this.mActivity = activity;
    }

    @Override
    public PlatformView create(Context context, int id, Object o) {
        return new FlutterCameraView(context, mMessenger, id, mActivity);
    }
}
