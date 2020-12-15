package com.lawmobile.presentation.security;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;

public class IsolatedService extends Service {

    private final String[] blackListedMountPaths = {"/sbin/.magisk/", "/sbin/.core/mirror", "/sbin/.core/img", "/sbin/.core/db-0/magisk.db"};

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private final IIsolatedService.Stub mBinder = new IIsolatedService.Stub() {
        public boolean isMagiskPresent() {
            int pid = android.os.Process.myPid();
            String cwd = String.format(Locale.US, "/proc/%d/mounts", pid);
            File file = new File(cwd);
            boolean isMagiskPresent = false;
            try (FileInputStream fis = new FileInputStream(file);
                 InputStreamReader inputStreamReader = new InputStreamReader(fis);
                 BufferedReader reader = new BufferedReader(inputStreamReader)) {
                String str;
                int countToKnowIsRoot = 0;
                while ((str = reader.readLine()) != null) {
                    for (String path : blackListedMountPaths) {
                        if (str.contains(path)) {
                            countToKnowIsRoot++;
                        }
                    }
                }
                if (countToKnowIsRoot > 0) {
                    isMagiskPresent = true;
                } else {
                    isMagiskPresent = Native.isMagiskPresentNative();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return isMagiskPresent;
        }
    };
}
