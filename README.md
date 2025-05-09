# react-native-auto-otp-listener

With **react-native-auto-otp-listener**, you can perform SMS-based user verification in your Android app automatically —  
without requiring the user to manually type verification codes and without needing any extra app permissions.

---

## Installation

```sh
npm install react-native-auto-otp-listener

import { useEffect, useState } from 'react';
import { Text, View, StyleSheet, Button, Alert } from 'react-native';
import {
  startListeningForOTP,
  stopListeningForOTP,
  addOTPListener,
  addOTPErrorListener,
} from 'react-native-auto-otp-listener';

export default function App() {
  const [otp, setOtp] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    startListeningForOTP();

    const otpSub = addOTPListener((receivedOtp) => {
      console.log('Received OTP:', receivedOtp);
      setOtp(receivedOtp);
      Alert.alert('OTP Received', receivedOtp);
    });

    const errorSub = addOTPErrorListener((err) => {
      console.warn('OTP error:', err);
      setError(err);
    });

    return () => {
      stopListeningForOTP();
      otpSub.remove();
      errorSub.remove();
    };
  }, []);

  return (
    <View style={styles.container}>
      <Text style={styles.title}>React Native Auto OTP Listener Demo</Text>
      <Text style={styles.text}>Received OTP: {otp ?? 'Waiting...'}</Text>
      {error && <Text style={styles.error}>Error: {error}</Text>}
      <Button title="Clear OTP" onPress={() => setOtp(null)} />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f9f9f9',
    alignItems: 'center',
    justifyContent: 'center',
    padding: 20,
  },
  title: {
    fontSize: 22,
    fontWeight: 'bold',
    marginBottom: 20,
  },
  text: {
    fontSize: 16,
    marginVertical: 10,
  },
  error: {
    fontSize: 14,
    color: 'red',
    marginTop: 10,
  },
});
```
