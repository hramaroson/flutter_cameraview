import 'dart:async';

import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:flutter/widgets.dart';

typedef void CameraViewCreatedCallback(CameraViewController controller);

// Flash value indicates the flash mode to be used.
enum Flash {
  // Flash is always off.
  Off,

  // Flash will be on when capturing.
  On,

  // Flash mode is chosen by the camera.
  Auto,

  // Flash is always on, working as a torch.
  Torch,
}

class CameraView extends StatefulWidget {
  const CameraView({
    Key key,
    this.onCreated,
  }) : super(key: key);

  final CameraViewCreatedCallback onCreated;

  @override
  State<StatefulWidget> createState() => _CameraViewState();
}

class _CameraViewState extends State<CameraView>{
  @override
  Widget build(BuildContext context) {
    if (defaultTargetPlatform == TargetPlatform.android) {
      return AndroidView(
        viewType: 'plugins.hramaroson.github.io/cameraview',
        onPlatformViewCreated: _onPlatformViewCreated,
      );
    }
    return Text('Unsupported Platform');
  }

  void _onPlatformViewCreated(int id) {
    if (widget.onCreated == null) {
      return;
    }
    widget.onCreated(new CameraViewController._(id));
  }
}
class CameraViewController {
  CameraViewController._(int id)
      : _channel = new MethodChannel('plugins.hramaroson.github.io/cameraview_$id');

  final MethodChannel _channel;

  Future<void> setFlash(Flash flash) async {
    return _channel.invokeMethod('setFlash', flash.index);
  }

  Future<Flash> getFlash() async {
    int _flashIndex = await _channel.invokeMethod('getFlash');
    return Flash.values[_flashIndex];
  }
}
