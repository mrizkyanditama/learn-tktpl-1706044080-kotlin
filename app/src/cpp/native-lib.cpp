#include <jni.h>

//
// Created by Asus on 12/6/2020.
//

extern "C" jdouble
Java_id_ac_ui_cs_mobileprogramming_MainActivity_convertToGHzJNI(JNIEnv *env, jobject thiz,
                                                                jdouble input) {
    // TODO: implement convertToGHzJNI()
    double result;
    result = input/1000;
    return result;
}