#include <jni.h>
#include <unistd.h>
#include <stdio.h>
#include <malloc.h>
#include <android/log.h>
#include <stdbool.h>
#include <fcntl.h>
#include <string.h>

static inline bool is_mountpaths_detected();
static inline bool is_supath_detected();

static const char *TAG = "DetectMagiskNative";
static char *blacklistedMountPaths[] = {
        "/sbin/.magisk/",
        "/sbin/.core/mirror",
        "/sbin/.core/img",
        "/sbin/.core/db-0/magisk.db"
};

static const char *suPaths[] = {
        "/data/local/su",
        "/data/local/bin/su",
        "/data/local/xbin/su",
        "/sbin/su",
        "/su/bin/su",
        "/system/bin/su",
        "/system/bin/.ext/su",
        "/system/bin/failsafe/su",
        "/system/sd/xbin/su",
        "/system/usr/we-need-root/su",
        "/system/xbin/su",
        "/cache/su",
        "/data/su",
        "/dev/su"
};


JNIEXPORT jboolean
Java_com_lawmobile_presentation_security_Native_isMagiskPresentNative(__unused JNIEnv *env,
                                                                      __unused jclass clazz) {
    bool bRet = false;
    do {
        bRet = is_supath_detected();
        if (bRet)
            break;
        bRet = is_mountpaths_detected();
        if (bRet)
            break;
    } while (false);

    if(bRet)
        return JNI_TRUE;
    else
        return JNI_FALSE;
}

__attribute__((always_inline))
static inline bool is_mountpaths_detected() {
    int len = sizeof(blacklistedMountPaths) / sizeof(blacklistedMountPaths[0]);

    bool bRet = false;
    int pid = getpid();
    char ch[100];
    memset(ch, '\0', 100 * sizeof(char));

    sprintf(ch, "/proc/%d/mounts", pid);

    FILE *fp = fopen(ch, "r");

    fseek(fp, 0L, SEEK_END);
    long size = ftell(fp);
    __android_log_print(ANDROID_LOG_INFO, TAG, "Opening Mount file :%s: size: %ld", ch, size);
    /* For some reason size comes as zero */
    if (size == 0)
        size = 3000;  /*This will differ for different devices */
    char *buffer = malloc(size * sizeof(char));
    if (buffer == NULL)
        goto exit;

    fread(buffer, 1, size, fp);
    int count = 0;
    for (int i = 0; i < len; i++) {
        __android_log_print(ANDROID_LOG_INFO, TAG, "Path  :%s", blacklistedMountPaths[i]);
        char *rem = strstr(buffer, blacklistedMountPaths[i]);
        if (rem != NULL) {
            count++;
            __android_log_print(ANDROID_LOG_INFO, TAG, "Found Path");
            break;
        }
    }
    if (count > 0)
        bRet = true;

    exit:

    if (NULL != buffer)
        free(buffer);
    if (fp != NULL)
        fclose(fp);

    return bRet;
}


__attribute__((always_inline))
static inline bool is_supath_detected() {
    int len = sizeof(suPaths) / sizeof(suPaths[0]);

    bool bRet = false;
    for (int i = 0; i < len; i++) {
        if (0 <= open(suPaths[i], O_RDONLY)) {
            __android_log_print(ANDROID_LOG_INFO, TAG, "Found Path :%s", suPaths[i]);
            bRet = true;
            break;
        }
        if (0 == access(suPaths[i], R_OK)) {
            __android_log_print(ANDROID_LOG_INFO, TAG, "Found Path :%s", suPaths[i]);
            bRet = true;
            break;
        }
    }

    return bRet;
}