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
  CameraViewController cameraViewController;
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
        body: CameraView(
          onCameraViewCreated: _onCameraViewCreated,
        ),
        ),
    );
  }
  void _onCameraViewCreated(CameraViewController controller){
      cameraViewController = controller;
  }
}
