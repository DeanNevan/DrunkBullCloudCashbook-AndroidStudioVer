package com.drunkbull.drunkbullcloudcashbook.utils;

import com.drunkbull.drunkbullcloudcashbook.R;
import com.drunkbull.drunkbullcloudcashbook.pojo.CBGroup;

public class Para2TextFormatter {
    public static int getCBGroupMemberAuthorityStringRID(CBGroup.CBGroupMember member){
        if (member.admin){
            return R.string.text_authority_admin;
        }
        if (member.read){
            if (member.write){
                return R.string.text_authority_wr;
            }
            else{
                return R.string.text_authority_read;
            }
        }
        if (member.write){
            return R.string.text_authority_write;
        }
        return R.string.text_space;
    }
}
