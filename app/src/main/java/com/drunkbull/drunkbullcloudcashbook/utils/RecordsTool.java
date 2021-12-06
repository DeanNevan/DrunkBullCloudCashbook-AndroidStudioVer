package com.drunkbull.drunkbullcloudcashbook.utils;

import com.drunkbull.drunkbullcloudcashbook.pojo.CBRecord;
import com.drunkbull.drunkbullcloudcashbook.singleton.Auth;

public class RecordsTool {
    public static double getTotalMoney(){
        double result = 0;
        for (CBRecord record : Auth.getSingleton().cbGroup.records){
            result += record.money;
        }
        return result;
    }
}
