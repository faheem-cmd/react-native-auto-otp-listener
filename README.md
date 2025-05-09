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
  getAppHash,
} from 'react-native-auto-otp-listener';

export default function App() {
  const [otp, setOtp] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [appHash, setAppHash] = useState<string | null>(null); // Store the app hash here

  useEffect(() => {
    const fetchAppHash = async () => {
      try {
        const hash = await getAppHash();
        console.log('✅ App hash for SMS:', hash);
        setAppHash(hash); // Set app hash in state
      } catch (err) {
        console.error('❌ Error getting app hash:', err);
      }
    };

    fetchAppHash(); // Call the async function

    startListeningForOTP();

    const otpSub = addOTPListener((receivedOtp) => {
      console.log('Received OTP:', receivedOtp);

      // Extract the 6-digit OTP from the SMS
      const otpMatch = receivedOtp.match(/\d{6}/); // Regex to find 6 digits
      if (otpMatch) {
        setOtp(otpMatch[0]);
        Alert.alert('OTP Received', otpMatch[0]);
      } else {
        setError('No 6-digit OTP found');
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
      {appHash && <Text style={styles.text}>App Hash: {appHash}</Text>}{' '}
      {/* Display App Hash */}
      <Text style={styles.text}>
        Received OTP: {otp ?? 'Waiting for OTP...'}
      </Text>
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
    padding: 10,
  },
  title: {
    fontSize: 22,
    fontWeight: 'bold',
    marginBottom: 20,
  },
  text: {
    fontSize: 16,
    marginVertical: 40,
    fontWeight: '500',
    color: 'red',
  },
  error: {
    fontSize: 14,
    color: 'red',
    marginVertical: 20,
  },
});
```
