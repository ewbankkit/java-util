/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.lsv;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;

import com.netsol.adagent.util.dbhelpers.BaseHelper;
import com.netsol.adagent.util.log.BaseLoggable;

public class BusinessCategoryDBHelper extends BaseHelper {
    public static final String sccsID = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:09 BusinessCategoryDBHelper.java NSI";

    private static final BusinessCategoryFactory Factory = new BusinessCategoryFactory();

    private final String logTag;

    public BusinessCategoryDBHelper(String logComponent, String logTag) {
        super(logComponent);
        this.logTag = logTag;
    }

    public BusinessCategoryDBHelper(Log log, String logTag) {
        super(log);
        this.logTag = logTag;
    }

    public BusinessCategoryDBHelper(BaseLoggable log, String logTag) {
        super(log);
        this.logTag = logTag;
    }

    public List<BusinessCategory> queryCategories(Connection conn, long categoryTypeId) throws SQLException {
        PreparedStatement stat = null;
        ResultSet rs = null;

        final String QueryByPid =
            "Select " +
            " business_category_id, " +
            " business_category_name, " +
            " parent_business_category_id, " +
            " vendor_category_id " +
            "from business_category " +
            "where business_category_type_id = ? " +
            "order by business_category_id asc;";

        List<BusinessCategory> allCats = new LinkedList<BusinessCategory>();
        try {
            stat = conn.prepareStatement(QueryByPid);
            stat.clearParameters();
            stat.setLong(1, categoryTypeId);

            logDebug(logTag, "Execute Query: "+ stat);

            rs = stat.executeQuery();

            while (rs.next()) {
                allCats.add(Factory.newInstance(rs));
            }

            logDebug(logTag, "Complete, nResults: "+ allCats.size());

            return buildCategoryTree(allCats);
        }
        finally {
            close( stat, rs);
        }
    }

    public List<BusinessCategory> queryParentCategories(Connection conn, long categoryTypeId, Long categoryId) throws SQLException{
        LinkedList<BusinessCategory> results = new LinkedList<BusinessCategory>();
        PreparedStatement stat = null;
        ResultSet rs = null;
        BusinessCategory category = null;
        final String QueryById =
            "Select " +
            " business_category_id, " +
            " business_category_name, " +
            " parent_business_category_id, " +
            " vendor_category_id " +
            "from business_category " +
            "where business_category_type_id = ? " +
            "and business_category_id = ?;";

        try {
            stat = conn.prepareStatement(QueryById);
            while (categoryId != null && categoryId > 0) {
                stat.clearParameters();
                stat.setLong(1, categoryTypeId);
                stat.setLong(2, categoryId);
                try {
                    rs = stat.executeQuery();
                    category = singleValue(rs,Factory );
                    if (category != null) {
                        results.addFirst(category);
                        categoryId = category.getParentCategoryId();
                    }
                    else {
                        categoryId = null;
                    }
                }
                finally{
                    close(rs);
                }
            }
        }
        finally{
            close(stat);
        }

        return results;
    }

    private List<BusinessCategory> buildCategoryTree(List<BusinessCategory> allCategories) throws SQLException {
        List<BusinessCategory> topCategories = new ArrayList<BusinessCategory>();
        for (BusinessCategory c: allCategories) {
            long id = c.getCategoryId();
            long pid = c.getParentCategoryId();
            if (pid == 0) {
                topCategories.add(c);
            }
            else {
                BusinessCategory parent = getCategoryById(pid, allCategories);
                if (parent == null) {
                    logError(logTag, "Missing parent category for: "+ c.getName() + "("+id+")");
                }
                else {
                    if (parent.getChildCategories() == null) {
                        parent.setChildCategories(new ArrayList<BusinessCategory>());
                    }
                    parent.getChildCategories().add(c);
                }
            }
        }

        return topCategories;
    }

    private BusinessCategory getCategoryById(long id, List<BusinessCategory> allCategories) {
        BusinessCategory category = null;
        BusinessCategory key = new BusinessCategory();
        key.setCategoryId(id);

        int i = Collections.binarySearch(allCategories, key, new Comparator<BusinessCategory>() {
            public int compare(BusinessCategory a, BusinessCategory b) {
                return new Long(a.getCategoryId()).compareTo(b.getCategoryId());
            }
        });
        if (i >= 0 && i < allCategories.size()) {
            category = allCategories.get(i);
            if (category.getCategoryId() != id) {
                category = null;
            }
        }
        return category;
    }

    private static class BusinessCategoryFactory implements Factory<BusinessCategory> {
        public BusinessCategory newInstance(ResultSet resultSet) throws SQLException {
            BusinessCategory businessCategory = new BusinessCategory();
            businessCategory.setName(resultSet.getString("business_category_name"));
            businessCategory.setCategoryId(getLongValue(resultSet, "business_category_id"));
            businessCategory.setParentCategoryId(getLongValue(resultSet, "parent_business_category_id"));
            businessCategory.setVendorId(getLongValue(resultSet, "vendor_category_id"));
            return businessCategory;
        }
    }
}
