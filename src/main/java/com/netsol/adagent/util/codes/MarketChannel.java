package com.netsol.adagent.util.codes;

/**
 * Static Definition for the market channels, stored in GDB 
 * 
 * @author pmitchel
 */
public enum MarketChannel {
    Leads(1),
    SmartClicks(2),
    Verio(3),
    Register(4),
    Yahoo(5);
    
    
    private int marketChannelId;
    private MarketChannel(int marketChannelId){
        this.marketChannelId = marketChannelId;
    }
    
  
    public int getMarketChannelId() {
        return marketChannelId;
    } 
}
