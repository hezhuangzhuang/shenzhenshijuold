package com.zxwl.szga.bean;

import java.util.Objects;

public class pliceOrg {
    /**
     * id : abcdefghijklmnopqrstuvwxyz0123456789
     * deptCode : 00000001
     * name : 深圳市局
     * category : 0
     * parentId : abcdefghijklmnopqrstuvwxyz0123456780
     * parentCode : 00000000
     * isCancel : 0
     * isDisabled : 0
     * disabledDate :
     * activeDate : 2010-01-01 00:00:00
     * oldCodeDisabledDate :
     * oldDeptCode :
     * isLeaf : 0
     * path :
     * pathtree :
     * pathLevel : 1
     * listOrder : 1
     * remark :
     * createName :
     * lastUpdateName :
     * zgrsId :
     * description :
     */

    private String id;
    private String deptCode;
    private String name;
    private int category;
    private String parentId;
    private String parentCode;
    private int isCancel;
    private int isDisabled;
    private String disabledDate;
    private String activeDate;
    private String oldCodeDisabledDate;
    private String oldDeptCode;
    private int isLeaf;
    private String path;
    private String pathtree;
    private int pathLevel;
    private int listOrder;
    private String remark;
    private String createName;
    private String lastUpdateName;
    private String zgrsId;
    private String description;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeptCode() {
        return deptCode;
    }

    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }

    public int getIsCancel() {
        return isCancel;
    }

    public void setIsCancel(int isCancel) {
        this.isCancel = isCancel;
    }

    public int getIsDisabled() {
        return isDisabled;
    }

    public void setIsDisabled(int isDisabled) {
        this.isDisabled = isDisabled;
    }

    public String getDisabledDate() {
        return disabledDate;
    }

    public void setDisabledDate(String disabledDate) {
        this.disabledDate = disabledDate;
    }

    public String getActiveDate() {
        return activeDate;
    }

    public void setActiveDate(String activeDate) {
        this.activeDate = activeDate;
    }

    public String getOldCodeDisabledDate() {
        return oldCodeDisabledDate;
    }

    public void setOldCodeDisabledDate(String oldCodeDisabledDate) {
        this.oldCodeDisabledDate = oldCodeDisabledDate;
    }

    public String getOldDeptCode() {
        return oldDeptCode;
    }

    public void setOldDeptCode(String oldDeptCode) {
        this.oldDeptCode = oldDeptCode;
    }

    public int getIsLeaf() {
        return isLeaf;
    }

    public void setIsLeaf(int isLeaf) {
        this.isLeaf = isLeaf;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPathtree() {
        return pathtree;
    }

    public void setPathtree(String pathtree) {
        this.pathtree = pathtree;
    }

    public int getPathLevel() {
        return pathLevel;
    }

    public void setPathLevel(int pathLevel) {
        this.pathLevel = pathLevel;
    }

    public int getListOrder() {
        return listOrder;
    }

    public void setListOrder(int listOrder) {
        this.listOrder = listOrder;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCreateName() {
        return createName;
    }

    public void setCreateName(String createName) {
        this.createName = createName;
    }

    public String getLastUpdateName() {
        return lastUpdateName;
    }

    public void setLastUpdateName(String lastUpdateName) {
        this.lastUpdateName = lastUpdateName;
    }

    public String getZgrsId() {
        return zgrsId;
    }

    public void setZgrsId(String zgrsId) {
        this.zgrsId = zgrsId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        pliceOrg pliceOrg = (pliceOrg) o;
        return category == pliceOrg.category &&
                isCancel == pliceOrg.isCancel &&
                isDisabled == pliceOrg.isDisabled &&
                isLeaf == pliceOrg.isLeaf &&
                pathLevel == pliceOrg.pathLevel &&
                listOrder == pliceOrg.listOrder &&
                Objects.equals(id, pliceOrg.id) &&
                Objects.equals(deptCode, pliceOrg.deptCode) &&
                Objects.equals(name, pliceOrg.name) &&
                Objects.equals(parentId, pliceOrg.parentId) &&
                Objects.equals(parentCode, pliceOrg.parentCode) &&
                Objects.equals(disabledDate, pliceOrg.disabledDate) &&
                Objects.equals(activeDate, pliceOrg.activeDate) &&
                Objects.equals(oldCodeDisabledDate, pliceOrg.oldCodeDisabledDate) &&
                Objects.equals(oldDeptCode, pliceOrg.oldDeptCode) &&
                Objects.equals(path, pliceOrg.path) &&
                Objects.equals(pathtree, pliceOrg.pathtree) &&
                Objects.equals(remark, pliceOrg.remark) &&
                Objects.equals(createName, pliceOrg.createName) &&
                Objects.equals(lastUpdateName, pliceOrg.lastUpdateName) &&
                Objects.equals(zgrsId, pliceOrg.zgrsId) &&
                Objects.equals(description, pliceOrg.description);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, deptCode, name, category, parentId, parentCode, isCancel, isDisabled, disabledDate, activeDate, oldCodeDisabledDate, oldDeptCode, isLeaf, path, pathtree, pathLevel, listOrder, remark, createName, lastUpdateName, zgrsId, description);
    }

    //    /**
//     * id : 3
//     * name : B分局
//     * parentId : 1
//     */
//
//    private int id;
//    private String name;
//    private int parentId;
//
//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public int getParentId() {
//        return parentId;
//    }
//
//    public void setParentId(int parentId) {
//        this.parentId = parentId;
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        pliceOrg pliceOrg = (pliceOrg) o;
//        return id == pliceOrg.id &&
//                parentId == pliceOrg.parentId &&
//                Objects.equals(name, pliceOrg.name);
//    }
//
//    @Override
//    public int hashCode() {
//
//        return Objects.hash(id, name, parentId);
//    }
}
