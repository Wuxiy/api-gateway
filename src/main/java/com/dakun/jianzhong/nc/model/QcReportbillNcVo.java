package com.dakun.jianzhong.nc.model;


import javax.validation.constraints.NotNull;

public class QcReportbillNcVo {


    @NotNull(message = "产品规格不能为空")
    private Integer specId;


    private String code;

    /**
     * 生产车间：
     */
    @NotNull(message = "生产车间主键不能为空")
    private Integer workshop;

    /**
     * 生产车间对应主键
     */
    private String workshopPK;
    /**
     * 生产班次
     */
    @NotNull(message = "生产班次不能为空")
    private Byte workclasses;

    /**
     * 生产班次对应字符串
     */
    private String times;
    /**
     * 生产日期
     */
    @NotNull(message = "生产日期不能为空")
    private String createTime;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getWorkshop() {
        return workshop;
    }

    public void setWorkshop(Integer workshop) {
        this.workshop = workshop;
    }

    public String getWorkshopPK() {
        return workshopPK;
    }

    public void setWorkshopPK(String workshopPK) {
        this.workshopPK = workshopPK;
    }

    public Byte getWorkclasses() {
        return workclasses;
    }

    public void setWorkclasses(Byte workclasses) {
        this.workclasses = workclasses;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Integer getSpecId() {
        return specId;
    }

    public void setSpecId(Integer specId) {
        this.specId = specId;
    }

    public String getTimes() {
        return times;
    }

    public void setTimes(String times) {
        this.times = times;
    }
}
