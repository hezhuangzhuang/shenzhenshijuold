package com.zxwl.szga.util;

import com.zxwl.szga.bean.pliceOrg;

import java.util.ArrayList;
import java.util.List;

public class OrgUtils {

    /**
     * 递归删除
     *
     * @param data
     * @return
     */
    public static List<pliceOrg> delete(List<pliceOrg> data, pliceOrg item) {
        data.remove(item);
        List<pliceOrg> deleteList = new ArrayList<>();
        for (pliceOrg temp : data) {
            if (temp.getParentId().equals(item.getId())) {
                deleteList.add(temp);
            }
        }
        for (pliceOrg temp : deleteList) {
            delete(data, temp);
        }
        return data;
    }

    /**
     * 使用获取层级递归前需要先重置此变量
     */
    private static int level = 0;

    /**
     * 获取层级深度，私有内部工具方法
     * @param data
     * @param item
     * @return
     */
    public static int prgetLevel(List<pliceOrg> data, pliceOrg item) {
        for (pliceOrg temp : data) {
            if (temp.getId().equals(item.getParentId())) {
                level = level + 1;
                prgetLevel(data, temp);
            }
        }
        return level;
    }

    /**
     * 获取层级深度
     *
     * @param data
     * @param item
     * @return
     */
    public static int getLevel(List<pliceOrg> data, pliceOrg item) {
        level = 0;
        return prgetLevel(data, item);
    }
}
