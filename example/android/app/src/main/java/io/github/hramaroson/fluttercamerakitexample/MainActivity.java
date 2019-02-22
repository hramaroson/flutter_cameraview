package io.github.hramaroson.fluttercameraviewexample;

import android.os.Bundle;
import io.flutter.app.FlutterActivity;
import io.flutter.plugins.GeneratedPluginRegistrant;
import android.content.pm.ActivityInfo;

public class MainActivity extends FlutterActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    GeneratedPluginRegistrant.registerWith(this);

    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
  }
}
