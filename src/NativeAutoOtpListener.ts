// src/NativeAutoOtpListener.ts
import { NativeModules, Platform } from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-auto-otp-listener' doesn't seem to be linked properly. Make sure:\n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const AutoOtpListener = NativeModules.AutoOtpListener
  ? NativeModules.AutoOtpListener
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

export default AutoOtpListener;
