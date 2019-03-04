import 'package:flutter/material.dart';
import 'dart:async';
import 'dart:io';

import 'package:flutter_cameraview/flutter_cameraview.dart';
import 'package:fluttertoast/fluttertoast.dart';

import 'settings_page.dart';

Future<Null> main() async {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return new MaterialApp(
        title: "flutter_cameraview example",
        theme: new ThemeData(
            primarySwatch: Colors.blue,
        ),
        home: MyHomePage()
    );
  }
}

class MyHomePage extends StatefulWidget  {
  MyHomePage({Key key}) : super(key: key);

  @override
  _MyHomePageState createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  CameraViewController _cameraViewController; 
  Icon _flashButtonIcon = Icon(Icons.flash_off);
  File _thumbnailImage;

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
              onCreated: _onCameraViewCreated
            ),
            
            //Flash button
            Positioned(
              top: 8.0,
              right: 8.0,
              width: 40.0,
              height: 40.0,
              child: IconButton(
                  color: Colors.white,
                  iconSize: 25.0,
                  icon: _flashButtonIcon,
                  onPressed:  () => _onFlashButtonPressed()
                  ) 
            ),

            //Picture capture button
            Positioned(
              bottom: 20.0,
              width: 60.0,
              height: 60.0,
              left: (MediaQuery.of(context).size.width/2 - 30),
              child: new RaisedButton(
                shape: new CircleBorder(),
                child: new Icon(Icons.camera_alt, size: 30.0, color: Colors.blue),
                onPressed: () => _onTakePictureButtonPressed(),
              ),
            ),


            //Camera facing button
            Positioned(
              bottom:  (MediaQuery.of(context).size.height/2 - 100),
              width: 40.0,
              height: 40.0,
              right: 15.0,
              child: new IconButton(
                color: Colors.white,
                icon: Icon(Icons.switch_camera, size: 25),
                onPressed: () => _onCameraFacingButtonPressed()
              ),
            ),

            //Settings button
            Positioned(
              bottom: 100.0,
              width: 40.0,
              height: 40.0,
              right: 15.0, 
              child: new IconButton(
                color: Colors.white,
                icon: new Icon(Icons.settings, size: 25.0),
                onPressed: () => _onSettingsButtonPressed(context),
              ),
            ),

            //thumbnail & gallery button
            Positioned(
              bottom: 20.0,
              width: 40.0,
              height: 40.0,
              right: 15.0,
              child: new RaisedButton(
                color: Colors.black,
                shape: new CircleBorder(),
                padding: EdgeInsets.all(0),
                child: CircleAvatar( 
                  minRadius: 20.0,
                  maxRadius: 20.0,
                  backgroundColor: Colors.black,
                  backgroundImage: _thumbnailImage == null ? null : FileImage(_thumbnailImage)
                ),
                onPressed: (){} 
              )
            )
          ],
        ),
      ),
    );
  }
 
  void _onCameraViewCreated(CameraViewController controller){
      _cameraViewController = controller;
      _cameraViewController.onPictureFileCreated = _onPictureFileCreated;
  }

  void _onFlashButtonPressed() async {
      if(! await _cameraViewController.isOpened()) {
          showToast("Error: Camera not opened!");
          return;
      }
      
      Flash flash = await _cameraViewController.getFlash();
      Icon icon;
      String msg;
      switch(flash) {
        case Flash.Off:
          flash = Flash.On;
          msg = "Flash On";
          icon = Icon(Icons.flash_on);
          break;

        case Flash.On:
          flash = Flash.Auto;
          msg = "Flash Auto";
          icon = Icon(Icons.flash_auto);
          break;

        case Flash.Auto:
          flash = Flash.Torch;
          msg = "Torch Mode";
          icon = Icon(Icons.highlight);
          break;

        case Flash.Torch:
          flash = Flash.Off;
          msg = "Flash Off";
          icon = Icon(Icons.flash_off);
          break;
      }
      
      await _cameraViewController.setFlash(flash);

      setState(() {
        _flashButtonIcon = icon;
      });

      showToast(msg);
  }

  void _onCameraFacingButtonPressed() async {
    if(! await _cameraViewController.isOpened()) {
        showToast("Error: Camera not opened!");
        return;
    }

    Facing facing = await _cameraViewController.getFacing();
    String msg;
    if( facing == Facing.Back) {
      facing = Facing.Front;
      msg = "Front camera";
    }
    else {
      facing = Facing.Back;
      msg = "Back camera";
    }
    await _cameraViewController.setFacing(facing);

    showToast(msg);
  }

  void _onTakePictureButtonPressed() async {
    if(! await _cameraViewController.isOpened()) {
        showToast("Error: Camera not opened!");
        return;
    }
    _cameraViewController.takePicture();
  }

  void _onPictureFileCreated(String filePath){
      if( filePath == null  || filePath.isEmpty) {
          return;
      }
      showToast("Picture saved to " + filePath);
      setState(() {
        _thumbnailImage = new File(filePath);
        //  _thumbnailImage = new Image.file(new File(filePath) ,width: 200, height: 200,
        //     fit: BoxFit.cover ,filterQuality: FilterQuality.low); 
      });
  }

  void _onSettingsButtonPressed(BuildContext context) async {
     Navigator.push(context, new MaterialPageRoute(builder: (context) => new SettingsPage()));
  }

  void showToast(String msg){
    Fluttertoast.cancel(); //Hides previous toast message
    Fluttertoast.showToast(msg: msg, toastLength: Toast.LENGTH_SHORT , gravity: ToastGravity.CENTER);
  }
}
