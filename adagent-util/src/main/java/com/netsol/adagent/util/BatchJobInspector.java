// ---------------Copyright (c) 2008 Network Solutions----------------------------------------------
package com.netsol.adagent.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.netsol.adagent.util.beans.BatchJobStatusData;

/**
 * Create an instance of this class by passing a list of BatchJobStatus and this class will inspect
 * the status list for you and help you answer a few questions related to the batch job run. For
 * example, it can tell you how many runs a batch job did, etc.
 * 
 * @author Hatim Khan
 * @since Jun 16, 2009
 */
public class BatchJobInspector
{
    /**
     * SCCS control ID
     */
    public static final String sccsID = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:53:49 BatchJobInspector.java NSI";

    private List<BatchJobStatusData> statusList;
    private Map<String, List<BatchJobStatusData>> dayToListMap;

    public BatchJobInspector(List<BatchJobStatusData> statusList)
    {
        this.statusList = new ArrayList<BatchJobStatusData>(statusList);
        dayToListMap = new HashMap<String, List<BatchJobStatusData>>();
        processList();
    }

    private void processList()
    {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        for (BatchJobStatusData status : statusList) {
            String dayStr = format.format(status.getProcessingDate());
            List<BatchJobStatusData> innerList = dayToListMap.get(dayStr);
            if (innerList == null) innerList = new ArrayList<BatchJobStatusData>();
            innerList.add(status);
            dayToListMap.put(dayStr, innerList);
        }
    }

    /**
     * Given a day string with the following format "2009-06-16" this method returns the number of
     * runs a batch job did for the given day.
     */
    public int getRunCount(String batchJobId, String dayStr)
    {
        List<BatchJobStatusData> innerList = dayToListMap.get(dayStr);
        int count = 0;

        if (innerList != null) {
            for (BatchJobStatusData status : innerList) {
                if ((status.getBatchJobId().equals(batchJobId))
                        && ("FINISHED, SUCCESSFUL".equalsIgnoreCase(status.getStatus()))
                        && (status.getProdInstId() == null || status.getProdInstId().length() == 0)) {
                    count++;
                }
            }
        }

        return count;
    }

    /**
     * Returns null if there were no errors. This method will check for errors at the batch job
     * level as a whole and at the product level
     */
    public List<String> getErrors(String prodInstId, String batchJobId, String dayStr)
    {
        List<String> result = new ArrayList<String>();
        List<BatchJobStatusData> innerList = dayToListMap.get(dayStr);

        if (innerList != null) {
            for (BatchJobStatusData status : innerList) {
                // YES, YES, I know, I can combine all these if statements into one, but guess what?
                // it is much easier to read this way
                if (status.getBatchJobId().equals(batchJobId)) {
                    if ((status.getProdInstId() == null || status.getProdInstId().length() == 0)) {
                        if ("FINISHED, FAILED".equalsIgnoreCase(status.getStatus())) {
                            String message = status.getMessage();
                            if ("Please contact Engineering".equalsIgnoreCase(message))
                                message = message + " [engineering message: " + status.getError() + " ]";
                            result.add(message);
                        }
                    } else if (status.getProdInstId().equalsIgnoreCase(prodInstId)) {
                        if ("FAILED".equalsIgnoreCase(status.getStatus())) {
                            String message = status.getMessage();
                            String err = status.getError();
                            if ((err != null) && (err.length() != 0))
                                message = message + " [engineering message: " + err + " ]";
                            result.add(message);
                        }
                    }
                }
            }
        }

        return result.size() == 0 ? null : result;
    }
}
