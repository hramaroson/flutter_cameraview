import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter_camerakit/flutter_camerakit.dart';

Future<Null> main() async {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  @override
  void initState() {
    super.initState();
  }
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('flutter_camerakit example'),
        ),
        body:CameraView(),
        ),
    );
  }
}
