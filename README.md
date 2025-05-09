# react-native-auto-otp-listener

With **react-native-auto-otp-listener**,
you can perform SMS-based user verification in your Android app automatically —  
without requiring the user to manually type verification codes and without needing any extra app permissions.

---

## Installation

```sh
npm install react-native-auto-otp-listener
```

```sh
yarn add react-native-auto-otp-listener
```

```sh
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

    const otpSub = addOTPListener((message) => {
      console.log('Received message:', message);

      // Use regex to extract a 6-digit number
      const match = message.match(/\b\d{6}\b/);

      if (match) {
        const extractedOtp = match[0];
        console.log('Extracted OTP:', extractedOtp);
        setOtp(extractedOtp);
        Alert.alert('OTP Received', extractedOtp);
      } else {
        console.warn('No 6-digit OTP found in message');
        setError('No 6-digit OTP found in message');
      }
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
