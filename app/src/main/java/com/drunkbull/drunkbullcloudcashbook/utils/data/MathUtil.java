package com.drunkbull.drunkbullcloudcashbook.utils.data;

public class MathUtil {
    public static int clamp(int num, int min, int max){
        if (num < min) return min;
        if (num > max) return max;
        return num;
    }

    public static double clamp(double num, double min, double max){
        if (num < min) return min;
        if (num > max) return max;
        return num;
    }

    public static float clamp(float num, float min, float max){
        if (num < min) return min;
        if (num > max) return max;
        return num;
    }
}
