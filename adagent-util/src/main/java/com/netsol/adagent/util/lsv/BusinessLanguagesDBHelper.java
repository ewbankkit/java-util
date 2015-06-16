package com.netsol.adagent.util.lsv;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;

import com.netsol.adagent.util.dbhelpers.BaseHelper;
import com.netsol.adagent.util.log.BaseLoggable;

public class BusinessLanguagesDBHelper extends BaseHelper{

    public static final String sccsID = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:10 BusinessLanguagesDBHelper.java NSI";
    public static final BusinessLanguageFactory Factory = new BusinessLanguageFactory();

    String logTag = null;
    public BusinessLanguagesDBHelper(String logComponent, String logTag) {
        super(logComponent);
        this.logTag = logTag;
    }

    public BusinessLanguagesDBHelper(Log log, String logTag) {
        super(log);
        this.logTag = logTag;
    }
  
    public BusinessLanguagesDBHelper(BaseLoggable log, String logTag) {
        super(log);
        this.logTag = logTag;
    }

    /**
     * Query for the languages.
     * @param conn - gdb
     * @param languages - the languages' names
     * @return List of all languages
     */
    public List<BusinessLanguage> queryLanguages(Connection conn, String[] languagesNames) throws SQLException{    
        PreparedStatement stat = null;
        ResultSet rs = null;

        final String queryLanguagesNeedle = StringUtils.join(this.arrayOfString("?", languagesNames.length), ", ");
        final String queryLanguages = 
            "SELECT " +
            " name," + 
            " vendor_code," +
            " vendor_id " +
            "from language " +
            "where name IN (" + queryLanguagesNeedle + ")";
            
       List<BusinessLanguage> allLanguages = new LinkedList<BusinessLanguage>();
        try{
            stat = conn.prepareStatement(queryLanguages);

            for (int i = 0; i < languagesNames.length; i++) {
                stat.setString(i + 1, languagesNames[i].toUpperCase().trim());
            }
            
            logDebug(logTag, "Execute Query: "+ stat);
            
            rs = stat.executeQuery();
            
            while (rs.next()){
                allLanguages.add(Factory.newInstance(rs));
            } 

            logDebug(logTag, "Complete, nResults: "+ allLanguages.size());

        }finally{
            close( stat, rs);
        }

        return allLanguages;
    }

    private String[] arrayOfString(String string, int size) {
        String[] array = new String[size];
        for (int i = 0; i < size; i++) {
            array[i] = string;
        }
        return (array);
    }
    
    /**
     * Factory class used to create SeoProductDetail objects from a result set. 
     */
    private static class BusinessLanguageFactory implements Factory<BusinessLanguage> {

        private BusinessLanguageFactory(){}        
        public BusinessLanguage newInstance(ResultSet resultSet) throws SQLException {

            BusinessLanguage language = new BusinessLanguage();
            language.setName(resultSet.getString("name"));
            language.setVendorCode(resultSet.getString("vendor_code"));
            language.setVendorId(resultSet.getInt("vendor_id"));

            return language;
        }
    }




}
