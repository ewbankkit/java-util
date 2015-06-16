package com.github.ewbankkit.util.codes;

public enum Role {
    SUPERVISOR(1),
    ANALYST(2),
    SALES_AGENT(3),
    CUSTOMER(4),
    ACCOUNT_REP(5),
    SALES_MANAGER(6),
    DNI_PARTNER(7),
    ACCOUNT_MANAGER(8),
    SEO_ANALYST(9),
    OLM_ANALYST(10),
    SEO_COPYWRITER(11),
    AGENCY_USER(12);


    private int roleId;
    private Role(int roleId){
        this.roleId = roleId;
    }


    public int getRoleId() {
        return roleId;
    }

}
