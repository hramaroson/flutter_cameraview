package io.github.hramaroson.fluttercamerakit;

import android.content.Context;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.StandardMessageCodec;
import io.flutter.plugin.platform.PlatformView;
import io.flutter.plugin.platform.PlatformViewFactory;

public class FlutterCameraViewFactory extends PlatformViewFactory{
    private final BinaryMessenger mMessenger;

    public FlutterCameraViewFactory(BinaryMessenger messenger) {
        super(StandardMessageCodec.INSTANCE);
        this.mMessenger = messenger;
    }
    @Override
    public PlatformView create(Context context, int id, Object o) {
        return new FlutterCameraView(context, mMessenger, id);
    }
}
