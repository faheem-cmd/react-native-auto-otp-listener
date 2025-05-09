// src/index.d.ts
declare module 'react-native-auto-otp-listener' {
  export function startListeningForOTP(): void;
  export function stopListeningForOTP(): void;
  export function addOTPListener(callback: (otp: string) => void): void;
  export function addOTPErrorListener(callback: (error: string) => void): void;
}
