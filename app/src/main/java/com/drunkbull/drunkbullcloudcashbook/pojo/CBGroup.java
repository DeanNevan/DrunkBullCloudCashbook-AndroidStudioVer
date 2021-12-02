package com.drunkbull.drunkbullcloudcashbook.pojo;

import java.util.ArrayList;
import java.util.List;

public class CBGroup {
    public static class CBGroupMember{
        public boolean admin = false;
        public boolean read = false;
        public boolean write = false;

        public String username = "";
        public String groupName = "";
        public String nickname = "";
    }

    public String groupName = "";
    public CBGroupMember admin;

    public List<CBGroupMember> members = new ArrayList<>();
    public List<CBRecord> records = new ArrayList<>();
    public int recordsTotalCount = 0;


}
