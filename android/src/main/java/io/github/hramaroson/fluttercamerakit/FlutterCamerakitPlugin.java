package io.github.hramaroson.fluttercamerakit;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/** FlutterCamerakitPlugin */
public class FlutterCamerakitPlugin implements MethodCallHandler {
  private static final String TAG="FlutterCamerakitPlugin" 
  
  /** Plugin registration. */
  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "flutter_camerakit");
  
    channel.setMethodCallHandler( 
        new FlutterCamerakitPlugin(registrar, registrar.view(), registrar.activity()));
  }

  @Override
  public void onMethodCall(MethodCall call, Result result) {
       switch(call.method){
         default:
            result.notImplemented();
            break;
       }
}
