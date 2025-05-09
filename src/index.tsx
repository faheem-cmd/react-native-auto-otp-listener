// src/index.tsx
import { NativeEventEmitter, NativeModules } from 'react-native';
import AutoOtpListener from './NativeAutoOtpListener';

const emitter = new NativeEventEmitter(NativeModules.AutoOtpListener);

export function startListeningForOTP() {
  AutoOtpListener.startListeningForOTP();
}

export function stopListeningForOTP() {
  AutoOtpListener.stopListeningForOTP();
}

export function addOTPListener(callback: (otp: string) => void) {
  return emitter.addListener('onOTPReceived', callback);
}

export function addOTPErrorListener(callback: (err: string) => void) {
  return emitter.addListener('onOTPError', callback);
}

export default {
  startListeningForOTP,
  stopListeningForOTP,
  addOTPListener,
  addOTPErrorListener,
};
