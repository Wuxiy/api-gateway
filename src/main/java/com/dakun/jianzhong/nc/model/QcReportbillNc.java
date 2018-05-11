package com.dakun.jianzhong.nc.model;

import javax.persistence.Column;
import java.math.BigDecimal;

public class QcReportbillNc {
    /**
     * 产品规格
     */
    private Integer specId;

    private String specName;

    private String code;

    /**
     * 生产车间：
     */
    private Integer workshop;
    private String workshopPk;
    /**
     * 生产班组
     */
    private String workgroup;

    /**
     * 生产班次
     */
    private Byte workclasses;

    private String times;
    /**
     * 生产日期
     */
    private String createTime;

    /**
     * 合格品数量
     */
    private BigDecimal neligiastnum;

    /**
     * 不合格品数量
     */
    private BigDecimal nuneligiastnum;

    /**
     * 托盘与包装号
     */

    private String trayNum;

    /**
     * 托盘号
     */
    private Integer tuopanNum;

    /**
     * 包装号
     */
    private Integer packNum;

    /**
     * 标准值
     */
    private Double standardValue;

    /**
     * 检验值
     */
    private Double testValue;

    /**
     * 质量等级
     */
    private String pkQualitylvB;

    /**
     * 改判：Y：是；N：否
     */
    private String bchanged;

    /**
     * 改判物料
     */
    @Column(name = "pk_chgmrl")
    private String pkChgmrl;

    /**
     * 含量区间
     */
    private String contentRange;

    /**
     * 混合物
     */
    private String mixture;

    /**
     * 注释
     */
    private String note;

    /**
     * 质检分数
     *
     * @return
     */
    private String bscore;
    private String qualityScore;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


    /**
     * 获取标准值
     *
     * @return standard_value - 标准值
     */
    public Double getStandardValue() {
        return standardValue;
    }

    /**
     * 设置标准值
     *
     * @param standardValue 标准值
     */
    public void setStandardValue(Double standardValue) {
        this.standardValue = standardValue;
    }

    /**
     * 获取检验值
     *
     * @return test_value - 检验值
     */
    public Double getTestValue() {
        return testValue;
    }

    /**
     * 设置检验值
     *
     * @param testValue 检验值
     */
    public void setTestValue(Double testValue) {
        this.testValue = testValue;
    }

    /**
     * 获取质量等级
     *
     * @return pk_qualitylv_b - 质量等级
     */
    public String getPkQualitylvB() {
        return pkQualitylvB;
    }

    /**
     * 设置质量等级
     *
     * @param pkQualitylvB 质量等级
     */
    public void setPkQualitylvB(String pkQualitylvB) {
        this.pkQualitylvB = pkQualitylvB;
    }

    /**
     * 获取改判：Y：是；N：否
     *
     * @return bchanged - 改判：Y：是；N：否
     */
    public String getBchanged() {
        return bchanged;
    }

    /**
     * 设置改判：Y：是；N：否
     *
     * @param bchanged 改判：Y：是；N：否
     */
    public void setBchanged(String bchanged) {
        this.bchanged = bchanged;
    }

    /**
     * 获取改判物料
     *
     * @return pk_chgmrl - 改判物料
     */
    public String getPkChgmrl() {
        return pkChgmrl;
    }

    /**
     * 设置改判物料
     *
     * @param pkChgmrl 改判物料
     */
    public void setPkChgmrl(String pkChgmrl) {
        this.pkChgmrl = pkChgmrl;
    }

    /**
     * 获取含量区间
     *
     * @return range - 含量区间
     */
    public String getContentRange() {
        return contentRange;
    }

    public void setContentRange(String contentRange) {
        this.contentRange = contentRange;
    }

    /**
     * 获取混合物
     *
     * @return mixture - 混合物
     */
    public String getMixture() {
        return mixture;
    }

    /**
     * 设置混合物
     *
     * @param mixture 混合物
     */
    public void setMixture(String mixture) {
        this.mixture = mixture;
    }

    /**
     * 获取注释
     *
     * @return note - 注释
     */
    public String getNote() {
        return note;
    }

    /**
     * 设置注释
     *
     * @param note 注释
     */
    public void setNote(String note) {
        this.note = note;
    }

    public Integer getWorkshop() {
        return workshop;
    }

    public void setWorkshop(Integer workshop) {
        this.workshop = workshop;
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

    /**
     * 获取合格品数量
     *
     * @return neligiastnum - 合格品数量
     */
    public BigDecimal getNeligiastnum() {
        return neligiastnum;
    }

    /**
     * 设置合格品数量
     *
     * @param neligiastnum 合格品数量
     */
    public void setNeligiastnum(BigDecimal neligiastnum) {
        this.neligiastnum = neligiastnum;
    }

    /**
     * 获取不合格品数量
     *
     * @return nuneligiastnum - 不合格品数量
     */
    public BigDecimal getNuneligiastnum() {
        return nuneligiastnum;
    }

    /**
     * 设置不合格品数量
     *
     * @param nuneligiastnum 不合格品数量
     */
    public void setNuneligiastnum(BigDecimal nuneligiastnum) {
        this.nuneligiastnum = nuneligiastnum;
    }


    public String getWorkgroup() {
        return workgroup;
    }

    public void setWorkgroup(String workgroup) {
        this.workgroup = workgroup;
    }

    public String getTrayNum() {
        return trayNum;
    }

    public void setTrayNum(String trayNum) {
        this.trayNum = trayNum;
    }

    public String getSpecName() {
        return specName;
    }

    public void setSpecName(String specName) {
        this.specName = specName;
    }

    public Integer getTuopanNum() {
        return tuopanNum;
    }

    public void setTuopanNum(Integer tuopanNum) {
        this.tuopanNum = tuopanNum;
    }

    public Integer getPackNum() {
        return packNum;
    }

    public void setPackNum(Integer packNum) {
        this.packNum = packNum;
    }



    public String getQualityScore() {
        return qualityScore;
    }

    public void setQualityScore(String qualityScore) {
        this.qualityScore = qualityScore;
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

    public String getWorkshopPk() {
        return workshopPk;
    }

    public void setWorkshopPk(String workshopPk) {
        this.workshopPk = workshopPk;
    }

    public String getBscore() {
        return bscore;
    }

    public void setBscore(String bscore) {
        this.bscore = bscore;
    }
}
