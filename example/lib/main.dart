import 'package:flutter/material.dart';

import 'package:rw_speech_recognizer/rw_speech_recognizer.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _speechCommand;

  @override
  void initState() {
    super.initState();

    RwSpeechRecognizer.setCommands(<String>[
      'Full Boar',
      'California Sunshine',
      'Deadicated',
    ], (command) {
      setState(() {
        _speechCommand = command;
      });
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('RealWear HMT-1 speech recognition example app'),
        ),
        body: Builder(builder: (BuildContext context) {
          if (_speechCommand != null) {
            WidgetsBinding.instance.addPostFrameCallback((_) {
              showDialog(
                context: context,
                builder: (BuildContext context) {
                  return AlertDialog(
                    title: Text(''),
                    content: Text('You said $_speechCommand'),
                    actions: <Widget>[
                      new FlatButton(
                        child: const Text('OK'),
                        onPressed: () {
                          Navigator.of(context).pop();
                        },
                      ),
                    ],
                  );
                },
              );
            });
          }

          return Center(
              child: const Text(
                  'Say "Full Boar", "California Sunshine" or "Deadicated"'));
        }),
      ),
    );
  }
}
