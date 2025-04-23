import { Injectable } from '@angular/core';
import * as CryptoJS from 'crypto-js';

@Injectable({
  providedIn: 'root',
})
export class EncryptionService {
  private readonly SECRET_KEY = 'x2B7eTf93mQ9cGzYdFk7pLm8XsRjHtNv';
  private readonly IV = '7fH1d9Lm3cQ5x7Vz';

  encrypt(data: string): string {
    try {
      const key = CryptoJS.enc.Utf8.parse(this.SECRET_KEY);
      const iv = CryptoJS.enc.Utf8.parse(this.IV);

      const encrypted = CryptoJS.AES.encrypt(data, key, {
        iv: iv,
        mode: CryptoJS.mode.CBC,
        padding: CryptoJS.pad.Pkcs7,
      });

      // Return encrypted data as a base64-encoded string
      return encrypted.toString(); // This returns a base64 string
    } catch (error) {
      console.error('Encryption error:', error);
      throw error;
    }
  }

  decrypt(encryptedData: string): string {
    try {
      const key = CryptoJS.enc.Utf8.parse(this.SECRET_KEY);
      const iv = CryptoJS.enc.Utf8.parse(this.IV);

      const decrypted = CryptoJS.AES.decrypt(encryptedData, key, {
        iv: iv,
        mode: CryptoJS.mode.CBC,
        padding: CryptoJS.pad.Pkcs7,
      });

      return decrypted.toString(CryptoJS.enc.Utf8);
    } catch (error) {
      console.error('Decryption error:', error);
      throw error;
    }
  }
}
