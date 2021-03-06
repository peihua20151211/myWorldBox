package com.tencent.stat.common;

public class RC4 {
    static final byte[] key = "03a976511e2cbe3a7f26808fb7af3c05".getBytes();

    public static byte[] decrypt(byte[] bArr) {
        return decrypt(bArr, key);
    }

    static byte[] decrypt(byte[] bArr, byte[] bArr2) {
        return encrypt(bArr, bArr2);
    }

    public static byte[] encrypt(byte[] bArr) {
        return encrypt(bArr, key);
    }

    static byte[] encrypt(byte[] bArr, byte[] bArr2) {
        int i = 0;
        int[] iArr = new int[256];
        int[] iArr2 = new int[256];
        int length = bArr2.length;
        if (length < 1 || length > 256) {
            throw new IllegalArgumentException("key must be between 1 and 256 bytes");
        }
        int i2;
        for (i2 = 0; i2 < 256; i2++) {
            iArr[i2] = i2;
            iArr2[i2] = bArr2[i2 % length];
        }
        length = 0;
        for (i2 = 0; i2 < 256; i2++) {
            length = ((length + iArr[i2]) + iArr2[i2]) & 255;
            int i3 = iArr[i2];
            iArr[i2] = iArr[length];
            iArr[length] = i3;
        }
        byte[] bArr3 = new byte[bArr.length];
        i2 = 0;
        length = 0;
        while (i < bArr.length) {
            length = (length + 1) & 255;
            i2 = (i2 + iArr[length]) & 255;
            i3 = iArr[length];
            iArr[length] = iArr[i2];
            iArr[i2] = i3;
            bArr3[i] = (byte) (iArr[(iArr[length] + iArr[i2]) & 255] ^ bArr[i]);
            i++;
        }
        return bArr3;
    }
}
