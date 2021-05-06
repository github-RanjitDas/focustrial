package com.lawmobile.presentation.security;

public class Native {

    private Native() {
        throw new IllegalStateException("Utility class");
    }

    static {
        System.loadLibrary("native-lib");
    }

    static native boolean isMagiskPresentNative();
}
