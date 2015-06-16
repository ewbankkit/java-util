/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.log;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;

import com.netsol.adagent.util.beans.BaseData;

import registrar.base.log.Log;

/**
 * log4j appender that logs to the Registrar-Utils logger.
 */
public  class RegistrarUtilsAppender extends AppenderSkeleton {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:08 RegistrarUtilsAppender.java NSI";
    
    private String logComponent;
    
    /**
     * Constructor.
     */
    public RegistrarUtilsAppender() {
        super();
        
        return;
    }

    public void setLogComponent(String logComponent) {
        this.logComponent = logComponent;
    }

    public String getLogComponent() {
        return this.logComponent;
    }

    /**
     * Close the appender.
     */
    public void close() {
        this.closed = true;
        
        return;
    }

    /**
     * Does this appender require a layout?
     */
    public boolean requiresLayout() {
        return false;
    }    

    /**
     * Perform the actual logging.
     */
    @Override
    protected void append(LoggingEvent event) {
        Object messageObject = event.getMessage();
        String message = (messageObject == null) ? null : messageObject.toString();
        ThrowableInformation throwableInformation = event.getThrowableInformation();
        Throwable throwable = (throwableInformation == null) ? null : throwableInformation.getThrowable();
        int logLevel;
        switch (event.getLevel().toInt()) {
        case Priority.DEBUG_INT: logLevel = Log.DEBUG;   break;
        case Priority.ERROR_INT: logLevel = Log.ERROR;   break;
        case Priority.FATAL_INT: logLevel = Log.FATAL;   break;
        case Priority.INFO_INT:  logLevel = Log.INFO;    break;
        case Priority.WARN_INT:  logLevel = Log.WARNING; break;
        default:                 logLevel = Log.UNKNOWN; break;
        }
        String logTag = BaseLoggable.getCurrentLogTag();
        // Any current log component overrides any global log component.
        String logComponent = BaseData.coalesce(BaseLoggable.getCurrentLogComponent(), this.logComponent);

        if (message != null) {
            if (BaseData.stringIsNotBlank(logTag)) {
                Log.log(this, logComponent, logLevel, logTag, message);                
            }
            else {
                Log.log(this, logComponent, logLevel, message);
            }
        }
        if (throwable != null) {
            if (BaseData.stringIsNotBlank(logTag)) {
                Log.log(this, logComponent, logLevel, logTag, throwable);
            }
            else {
                Log.log(this, logComponent, logLevel, throwable);
            }
        }
        
        return;
    }
}
