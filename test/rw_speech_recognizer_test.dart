import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:rw_speech_recognizer/rw_speech_recognizer.dart';

void main() {
  const MethodChannel channel = MethodChannel('rw_speech_recognizer');

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await RwSpeechRecognizer.platformVersion, '42');
  });
}
