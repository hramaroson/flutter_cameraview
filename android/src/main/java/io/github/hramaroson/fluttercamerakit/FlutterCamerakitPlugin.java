package io.github.hramaroson.fluttercamerakit;

import io.flutter.plugin.common.PluginRegistry;

public class FlutterCamerakitPlugin {
    public static void registerWith(PluginRegistry.Registrar registrar) {
        registrar
                .platformViewRegistry()
                .registerViewFactory(
                        "plugins.hramaroson.github.io/camerakit",
                        new FlutterCameraViewFactory(registrar.messenger(), registrar.activity()));
    }
}