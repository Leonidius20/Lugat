# Crimean Tatar-Russian dictionary with TTS
This is a Crimean Tatar-russian dictionary with text-to-speech capabilities.

## Features
- Words lookup in a digital version of the Crimean Tatar-russian dictionary by Seyran Memet oğlu Üseinov;
- Text-to-speech pronunciation of Crimean Tatar words and user-provided phrases. The model runs on-device and does not require an internet connection;
- A transliteration tool to convert between Cyrillic and Latin writing.

## Technologies
This project uses ViewBinding, RecyclerView, Room, Dagger/Hilt, Android Architecture Components (ViewModel, Navigation), [Material Components](https://github.com/material-components/material-components-android), a [TTS model from Facebook's Massively Multilingual Speech project](https://huggingface.co/facebook/mms-tts-crh), and [sherpa-onnx](https://github.com/k2-fsa/sherpa-onnx) to run the model on-device.

## Screenshots
![Combined screenshots](/docs/screenshots/combined.png)