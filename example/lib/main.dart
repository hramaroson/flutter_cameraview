import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter_cameraview/flutter_cameraview.dart';

Future<Null> main() async {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  CameraViewController _cameraViewController; 
  Icon _flashButtonIcon = Icon(Icons.flash_off);
  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('flutter_cameraview example'),
        ),
        body: Stack(
          children: <Widget>[
            CameraView(
              onCameraViewCreated: _onCameraViewCreated
            ),
            Positioned(
              top: 8.0,
              right: 8.0,
              width: 40.0,
              height: 40.0,
              child: IconButton(
                color: Colors.white,
                icon: _flashButtonIcon,
                onPressed: _onFlashButtonPressed,
              ),
            ),
          ],
        ),
      ),
    );
  }
 
  void _onCameraViewCreated(CameraViewController controller){
      _cameraViewController = controller;
  }

  void _onFlashButtonPressed() async {
      Flash flash = await _cameraViewController.getFlash();
      Icon _icon;
      switch(flash) {
        case Flash.Off:
          flash = Flash.On;
          _icon = Icon(Icons.flash_on);
          break;
        case Flash.On:
          flash = Flash.Auto;
          _icon = Icon(Icons.flash_auto);
          break;
        case Flash.Auto:
          flash = Flash.Torch;
          _icon = Icon(Icons.highlight);
          break;
        case Flash.Torch:
          flash = Flash.Off;
          _icon = Icon(Icons.flash_off);
          break;
      }
      await _cameraViewController.setFlash(flash);

      setState(() {
        _flashButtonIcon = _icon;
      });
  }
}
