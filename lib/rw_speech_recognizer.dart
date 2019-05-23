import 'dart:async';

import 'package:flutter/services.dart';

class RwSpeechRecognizer {
  static Function(String) _handler;

  static MethodChannel _channel =
      MethodChannel('com.rockwellits.plugins/rw_speech_recognizer')
        ..setMethodCallHandler((call) async {
          if (call.method == 'onSpeechEvent' && _handler != null) {
            _handler(call.arguments['command']);
          }
        });

  static Future<void> setCommands(
      List<String> commands, Function(String) handler) async {
    _handler = handler;
    await _channel.invokeMethod('setCommands', {'commands': commands});
  }

  static Future<void> restoreCommands() async {
    await _channel.invokeMethod('restoreCommands');
  }
}
