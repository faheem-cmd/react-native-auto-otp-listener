import { NativeEventEmitter, NativeModules } from 'react-native';
const { AutoOtpListener } = NativeModules;

const emitter = new NativeEventEmitter(AutoOtpListener);

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

export async function getAppHash(): Promise<string> {
  try {
    const hash = await AutoOtpListener.getAppHash();
    return hash;
  } catch (error) {
    console.error('Failed to get app hash:', error);
    throw error;
  }
}

export default {
  startListeningForOTP,
  stopListeningForOTP,
  addOTPListener,
  addOTPErrorListener,
  getAppHash,
};
