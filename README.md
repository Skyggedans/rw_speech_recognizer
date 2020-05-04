# rw_speech_recognizer

Speech recognizer plugin for RealWear HMT-1(Z1).

## Usage
```dart
    RwSpeechRecognizer.setCommands(<String>[
      'Full Boar',
      'California Sunshine',
      'Deadicated',
    ], (command) {
      // Define your callback here
    });
```