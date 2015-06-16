/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.productlifecycle;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

import com.netsol.adagent.util.beans.BaseData;

/**
 * Represents a product life cycle event.
 */
/* package-private */ class ProductLifeCycleEventData extends BaseData {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:15 ProductLifeCycleEventData.java NSI";

    public static final int GENERIC_DECIMAL_SCALE = 2;

    private Timestamp eventDate;
    private String eventType;
    private Boolean genericBoolean1;
    private Boolean genericBoolean2;
    private Boolean genericBoolean3;
    private Boolean genericBoolean4;
    private Date genericDate1;
    private Date genericDate2;
    private Date genericDate3;
    private Date genericDate4;
    private BigDecimal genericDecimal1;
    private BigDecimal genericDecimal2;
    private BigDecimal genericDecimal3;
    private BigDecimal genericDecimal4;
    private Long genericNumber1;
    private Long genericNumber2;
    private Long genericNumber3;
    private Long genericNumber4;
    private String genericString1;
    private String genericString2;
    private String genericString3;
    private String genericString4;
    private String genericString5;
    private String genericString6;
    private String genericString7;
    private String genericString8;
    private String genericString9;
    private String genericString10;
    private String prodInstId;

    public void setEventDate(Timestamp eventDate) {
        this.eventDate = eventDate;
    }

    public Timestamp getEventDate() {
        return eventDate;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventType() {
        return eventType;
    }

    public void setProdInstId(String prodInstId) {
        this.prodInstId = prodInstId;
    }

    public String getProdInstId() {
        return prodInstId;
    }

    public void setGenericBoolean1(Boolean genericBoolean1) {
        this.genericBoolean1 = genericBoolean1;
    }

    public Boolean getGenericBoolean1() {
        return genericBoolean1;
    }

    public void setGenericBoolean2(Boolean genericBoolean2) {
        this.genericBoolean2 = genericBoolean2;
    }

    public Boolean getGenericBoolean2() {
        return genericBoolean2;
    }

    public void setGenericBoolean3(Boolean genericBoolean3) {
        this.genericBoolean3 = genericBoolean3;
    }

    public Boolean getGenericBoolean3() {
        return genericBoolean3;
    }

    public void setGenericBoolean4(Boolean genericBoolean4) {
        this.genericBoolean4 = genericBoolean4;
    }

    public Boolean getGenericBoolean4() {
        return genericBoolean4;
    }

    public void setGenericDate1(Date genericDate1) {
        this.genericDate1 = genericDate1;
    }

    public Date getGenericDate1() {
        return genericDate1;
    }

    public void setGenericDate2(Date genericDate2) {
        this.genericDate2 = genericDate2;
    }

    public Date getGenericDate2() {
        return genericDate2;
    }

    public void setGenericDate3(Date genericDate3) {
        this.genericDate3 = genericDate3;
    }

    public Date getGenericDate3() {
        return genericDate3;
    }

    public void setGenericDate4(Date genericDate4) {
        this.genericDate4 = genericDate4;
    }

    public Date getGenericDate4() {
        return genericDate4;
    }

    public void setGenericDecimal1(BigDecimal genericDecimal1) {
        this.genericDecimal1 = genericDecimal1;
    }

    public BigDecimal getGenericDecimal1() {
        return genericDecimal1;
    }

    public void setGenericDecimal2(BigDecimal genericDecimal2) {
        this.genericDecimal2 = genericDecimal2;
    }

    public BigDecimal getGenericDecimal2() {
        return genericDecimal2;
    }

    public void setGenericDecimal3(BigDecimal genericDecimal3) {
        this.genericDecimal3 = genericDecimal3;
    }

    public BigDecimal getGenericDecimal3() {
        return genericDecimal3;
    }

    public void setGenericDecimal4(BigDecimal genericDecimal4) {
        this.genericDecimal4 = genericDecimal4;
    }

    public BigDecimal getGenericDecimal4() {
        return genericDecimal4;
    }

    public void setGenericNumber1(Long genericNumber1) {
        this.genericNumber1 = genericNumber1;
    }

    public Long getGenericNumber1() {
        return genericNumber1;
    }

    public void setGenericNumber2(Long genericNumber2) {
        this.genericNumber2 = genericNumber2;
    }

    public Long getGenericNumber2() {
        return genericNumber2;
    }

    public void setGenericNumber3(Long genericNumber3) {
        this.genericNumber3 = genericNumber3;
    }

    public Long getGenericNumber3() {
        return genericNumber3;
    }

    public void setGenericNumber4(Long genericNumber4) {
        this.genericNumber4 = genericNumber4;
    }

    public Long getGenericNumber4() {
        return genericNumber4;
    }

    public void setGenericString1(String genericString1) {
        this.genericString1 = genericString1;
    }

    public String getGenericString1() {
        return genericString1;
    }

    public void setGenericString2(String genericString2) {
        this.genericString2 = genericString2;
    }

    public String getGenericString2() {
        return genericString2;
    }

    public void setGenericString3(String genericString3) {
        this.genericString3 = genericString3;
    }

    public String getGenericString3() {
        return genericString3;
    }

    public void setGenericString4(String genericString4) {
        this.genericString4 = genericString4;
    }

    public String getGenericString4() {
        return genericString4;
    }

    public void setGenericString5(String genericString5) {
        this.genericString5 = genericString5;
    }

    public String getGenericString5() {
        return genericString5;
    }

    public void setGenericString6(String genericString6) {
        this.genericString6 = genericString6;
    }

    public String getGenericString6() {
        return genericString6;
    }

    public void setGenericString7(String genericString7) {
        this.genericString7 = genericString7;
    }

    public String getGenericString7() {
        return genericString7;
    }

    public void setGenericString8(String genericString8) {
        this.genericString8 = genericString8;
    }

    public String getGenericString8() {
        return genericString8;
    }

    public void setGenericString9(String genericString9) {
        this.genericString9 = genericString9;
    }

    public String getGenericString9() {
        return genericString9;
    }

    public void setGenericString10(String genericString10) {
        this.genericString10 = genericString10;
    }

    public String getGenericString10() {
        return genericString10;
    }
}
