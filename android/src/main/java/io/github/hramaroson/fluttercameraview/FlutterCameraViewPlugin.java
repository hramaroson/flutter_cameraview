package io.github.hramaroson.fluttercameraview;

import io.flutter.plugin.common.PluginRegistry;

public class FlutterCameraViewPlugin {
    public static void registerWith(PluginRegistry.Registrar registrar) {
        registrar
                .platformViewRegistry()
                .registerViewFactory(
                        "plugins.hramaroson.github.io/cameraview",
                        new FlutterCameraViewFactory(registrar.messenger(), registrar.activity()));
    }
}
