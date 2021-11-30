package com.drunkbull.drunkbullcloudcashbook.utils.data;

public class ByteUtil {
    public static byte[] formatBytes(byte[] target){
        for (int i = 0; i < target.length; i ++){
            if (target[i] < 0){
                target[i] += 256;
            }

        }
        return target;
    }
}
