package com.netsol.adagent.util.lsv;

import java.io.Serializable;


public class BusinessLanguage implements Serializable
{

    public static final String sccsID = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:09 BusinessLanguage.java NSI";

    private String name;

    private String vendorCode;

    private int vendorId;


    public String getName()
    {
        return (this.name);
    }


    public void setName(String name)
    {
        this.name = name;
    }


	public String getVendorCode() {
		return vendorCode;
	}


	public void setVendorCode(String vendorCode) {
		this.vendorCode = vendorCode;
	}


	public int getVendorId() {
		return vendorId;
	}


	public void setVendorId(int vendorId) {
		this.vendorId = vendorId;
	}




}
