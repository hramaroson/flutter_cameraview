import 'dart:async';

import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:flutter/widgets.dart';

typedef void CameraViewCreatedCallback(CameraViewController controller);

class CameraView extends StatefulWidget {
  const CameraView({
    Key key,
    this.onCameraViewCreated,
  }) : super(key: key);

  final CameraViewCreatedCallback onCameraViewCreated;

  @override
  State<StatefulWidget> createState() => _CameraViewState();
}

class _CameraViewState extends State<CameraView>{
  @override
  Widget build(BuildContext context) {
    if (defaultTargetPlatform == TargetPlatform.android) {
      return AndroidView(
        viewType: 'plugins.hramaroson.github.io/camerakit',
        onPlatformViewCreated: _onPlatformViewCreated,
      );
    }
  }
  void _onPlatformViewCreated(int id) {
    if (widget.onCameraViewCreated == null) {
      return;
    }
    widget.onCameraViewCreated(new CameraViewController._(id));
  }
}
class CameraViewController {
  CameraViewController._(int id)
      : _channel = new MethodChannel('plugins.hramaroson.github.io/camerakit_$id');

  final MethodChannel _channel;

//  Future<void> setText(String text) async {
//    assert(text != null);
//    return _channel.invokeMethod('setText', text);
//  }
}