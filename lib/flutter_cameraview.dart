import 'dart:async';

import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:flutter/widgets.dart';

typedef void CameraViewCreatedCallback(CameraViewController controller);

enum Facing {
  Back,
  Front
}

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

class CameraException implements Exception {
  CameraException(this.code, this.description);

  String code;
  String description;

  @override
  String toString() => '$runtimeType($code, $description)';
}

class CameraViewController {
  CameraViewController._(int id)
      : _channel = new MethodChannel('plugins.hramaroson.github.io/cameraview_$id');

  final MethodChannel _channel;

  Future<bool> isOpened() async {
    try {
       return _channel.invokeMethod('isOpened');
    } on PlatformException catch (e){
      throw CameraException(e.code, e.message);
    } 
  }

  Future<void> setFacing(Facing facing) async {
    try {
       return _channel.invokeMethod('setFacing', facing.index);
    } on PlatformException catch (e){
      throw CameraException(e.code, e.message);
    } 
  }

  Future<Facing> getFacing() async {
    try {
      int _facingIndex = await _channel.invokeMethod('getFacing');
      return Facing.values[_facingIndex];
    } on PlatformException catch (e){
      throw CameraException(e.code, e.message);
    } 
  }

  Future<void> setFlash(Flash flash) async {
    try {
      return _channel.invokeMethod('setFlash', flash.index);
    } on PlatformException catch (e){
      throw CameraException(e.code, e.message);
    } 
  }

  Future<Flash> getFlash() async {
    try {
      int _flashIndex = await _channel.invokeMethod('getFlash');
      return Flash.values[_flashIndex];
    } on PlatformException catch (e){
      throw CameraException(e.code, e.message);
    } 
  }

  Future<void> takePicture(String filepath) async {
      try {
        return _channel.invokeMethod('takePicture', filepath);
      } on PlatformException catch (e) {
        throw CameraException(e.code, e.message);
      }
  }
}
