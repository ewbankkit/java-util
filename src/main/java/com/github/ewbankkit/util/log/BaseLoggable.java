/**
 * Kit's Java Utils.
 */

package com.github.ewbankkit.util.log;

import java.lang.reflect.Method;
import java.util.Properties;

import com.github.ewbankkit.util.beans.NotInToString;

/**
 * Base class for objects that can log.
 */
public abstract class BaseLoggable {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:08 BaseLoggable.java NSI";

    /**
     * Decouple from registrar.base.log.Log.
     */
    protected static final int DEBUG_HIGH = 1;
    protected static final int DEBUG      = 2;
    protected static final int DEBUG_LOW  = 3;
    protected static final int INFO       = 4;
    protected static final int WARNING    = 5;
    protected static final int ERROR      = 6;
    protected static final int CRITICAL   = 7;
    protected static final int FATAL      = 8;

    protected static final RegistrarUtilsLog REGISTRAR_UTILS_LOG = createRegistrarUtilsLog();

    private static final LogAtLevel LOG_DEBUG = new LogAtLevel() {
        public void log(org.apache.commons.logging.Log logger, String message) {
            logger.debug(message);
        }

        public void log(org.apache.commons.logging.Log logger, Throwable t) {
            logger.debug(null, t);
        }
    };
    private static final LogAtLevel LOG_ERROR = new LogAtLevel() {
        public void log(org.apache.commons.logging.Log logger, String message) {
            logger.error(message);
        }

        public void log(org.apache.commons.logging.Log logger, Throwable t) {
            logger.error(null, t);
        }
    };
    private static final LogAtLevel LOG_FATAL = new LogAtLevel() {
        public void log(org.apache.commons.logging.Log logger, String message) {
            logger.fatal(message);
        }

        public void log(org.apache.commons.logging.Log logger, Throwable t) {
            logger.fatal(null, t);
        }
    };
    private static final LogAtLevel LOG_INFO = new LogAtLevel() {
        public void log(org.apache.commons.logging.Log logger, String message) {
            logger.info(message);
        }

        public void log(org.apache.commons.logging.Log logger, Throwable t) {
            logger.info(null, t);
        }
    };
    private static final LogAtLevel LOG_TRACE = new LogAtLevel() {
        public void log(org.apache.commons.logging.Log logger, String message) {
            logger.trace(message);
        }

        public void log(org.apache.commons.logging.Log logger, Throwable t) {
            logger.trace(null, t);
        }
    };
    private static final LogAtLevel LOG_WARNING = new LogAtLevel() {
        public void log(org.apache.commons.logging.Log logger, String message) {
            logger.warn(message);
        }

        public void log(org.apache.commons.logging.Log logger, Throwable t) {
            logger.warn(null, t);
        }
    };

    private static final ThreadLocal<String> CURRENT_LOG_COMPONENT = new ThreadLocal<String>() {
        @Override
        final protected String initialValue() {
            return null;
        }
    };
    private static final ThreadLocal<String> CURRENT_LOG_TAG = new ThreadLocal<String>() {
        @Override
        final protected String initialValue() {
            return null;
        }
    };

    private final String logComponent;
    private final org.apache.commons.logging.Log logger;

    /**
     * Constructor.
     */
    protected BaseLoggable(String logComponent) {
        this(logComponent, null);
    }

    /**
     * Constructor.
     */
    protected BaseLoggable(org.apache.commons.logging.Log logger) {
        this(null, logger);
    }

    /**
     * Constructor.
     */
    protected BaseLoggable(BaseLoggable that) {
        this(that.logComponent, that.logger);
    }

    /**
     * Constructor.
     */
    protected BaseLoggable(String logComponent, org.apache.commons.logging.Log logger) {
        this.logComponent = logComponent;
        this.logger = logger;
    }

    /**
     * Configure log4j.
     */
    public static void configureLog4J(Properties properties) {
        org.apache.log4j.PropertyConfigurator.configure(properties);
    }

    /**
     * Set the log component for the current thread.
     */
    public static void setCurrentLogComponent(String logComponent) {
        CURRENT_LOG_COMPONENT.set(logComponent);
    }

    /**
     * Clear the log component for the current thread.
     */
    public static void clearCurrentLogComponent() {
        setCurrentLogComponent(null);
    }

    /**
     * Return the log component for the current thread.
     */
    public static String getCurrentLogComponent() {
        return CURRENT_LOG_COMPONENT.get();
    }

    /**
     * Set the log tag for the current thread.
     */
    public static void setCurrentLogTag(String logTag) {
        CURRENT_LOG_TAG.set(logTag);
    }

    /**
     * Clear the log tag for the current thread.
     */
    public static void clearCurrentLogTag() {
        setCurrentLogTag(null);
    }

    /**
     * Return the log tag for the current thread.
     */
    public static String getCurrentLogTag() {
        return CURRENT_LOG_TAG.get();
    }

    /**
     * Is DEBUG logging currently enabled?
     */
    @NotInToString
    public boolean isDebugEnabled() {
        return (((logComponent != null) && REGISTRAR_UTILS_LOG.checkSeverity(DEBUG)) ||
                 ((logger != null) && logger.isDebugEnabled()));
    }

    /**
     * Is ERROR logging currently enabled?
     */
    @NotInToString
    public boolean isErrorEnabled() {
        return (((logComponent != null) && REGISTRAR_UTILS_LOG.checkSeverity(ERROR)) ||
                 ((logger != null) && logger.isErrorEnabled()));
    }

    /**
     * Is FATAL logging currently enabled?
     */
    @NotInToString
    public boolean isFatalEnabled() {
        return (((logComponent != null) && REGISTRAR_UTILS_LOG.checkSeverity(FATAL)) ||
                 ((logger != null) && logger.isFatalEnabled()));
    }

    /**
     * Is INFO logging currently enabled?
     */
    @NotInToString
    public boolean isInfoEnabled() {
        return (((logComponent != null) && REGISTRAR_UTILS_LOG.checkSeverity(INFO)) ||
                 ((logger != null) && logger.isInfoEnabled()));
    }

    /**
     * Is TRACE logging currently enabled?
     */
    @NotInToString
    public boolean isTraceEnabled() {
        return (((logComponent != null) && REGISTRAR_UTILS_LOG.checkSeverity(DEBUG_HIGH)) ||
                 ((logger != null) && logger.isTraceEnabled()));
    }

    /**
     * Is WARNING logging currently enabled?
     */
    @NotInToString
    public boolean isWarningEnabled() {
        return (((logComponent != null) && REGISTRAR_UTILS_LOG.checkSeverity(WARNING)) ||
                 ((logger != null) && logger.isWarnEnabled()));
    }

    /**
     * Log a message at level DEBUG.
     */
    protected void logDebug(String logTag, String message) {
        log(logTag, message, DEBUG, LOG_DEBUG);
    }

    /**
     * Log an exception at level DEBUG.
     */
    protected void logDebug(String logTag, Throwable t) {
        log(logTag, t, DEBUG, LOG_DEBUG);
    }

    /**
     * Log a message at level ERROR.
     */
    protected void logError(String logTag, String message) {
        log(logTag, message, ERROR, LOG_ERROR);
    }

    /**
     * Log an exception at level ERROR.
     */
    protected void logError(String logTag, Throwable t) {
        log(logTag, t, ERROR, LOG_ERROR);
    }

    /**
     * Log a message at level FATAL.
     */
    protected void logFatal(String logTag, String message) {
        log(logTag, message, FATAL, LOG_FATAL);
    }

    /**
     * Log an exception at level FATAL.
     */
    protected void logFatal(String logTag, Throwable t) {
        log(logTag, t, FATAL, LOG_FATAL);
    }

    /**
     * Log a message at level INFO.
     */
    protected void logInfo(String logTag, String message) {
        log(logTag, message, INFO, LOG_INFO);
    }

    /**
     * Log an exception at level INFO.
     */
    protected void logInfo(String logTag, Throwable t) {
        log(logTag, t, INFO, LOG_INFO);
    }

    /**
     * Log a message at level TRACE.
     */
    protected void logTrace(String logTag, String message) {
        log(logTag, message, DEBUG_HIGH, LOG_TRACE);
    }

    /**
     * Log an exception at level TRACE.
     */
    protected void logTrace(String logTag, Throwable t) {
        log(logTag, t, DEBUG_HIGH, LOG_TRACE);
    }

    /**
     * Log a message at level WARNING.
     */
    protected void logWarning(String logTag, String message) {
        log(logTag, message, WARNING, LOG_WARNING);
    }

    /**
     * Log an exception at level WARNING.
     */
    protected void logWarning(String logTag, Throwable t) {
        log(logTag, t, WARNING, LOG_WARNING);
    }

    /**
     * Log a message at the specified level.
     */
    private void log(String logTag, String message, int logLevel, LogAtLevel logAt) {
        if (logComponent != null) {
            REGISTRAR_UTILS_LOG.log(this, logComponent, logLevel, logTag, message);
        }

        if (logger != null) {
            boolean pushLogTag = (NestedDiagnosticContext.peek() == null) && (logTag != null);
            if (pushLogTag) {
                NestedDiagnosticContext.push(logTag);
            }
            boolean setCurrentLogTag = (getCurrentLogTag() == null) && (logTag != null);
            if (setCurrentLogTag) {
                setCurrentLogTag(logTag);
            }
            try {
                logAt.log(logger, message);
            }
            finally {
                if (setCurrentLogTag) {
                    clearCurrentLogTag();
                }
                if (pushLogTag) {
                    NestedDiagnosticContext.pop();
                }
            }
        }
    }

    /**
     * Log an exception at the specified level.
     */
    private void log(String logTag, Throwable t, int logLevel, LogAtLevel logAt) {
        if (logComponent != null) {
            REGISTRAR_UTILS_LOG.log(this, logComponent, logLevel, logTag, t);
        }

        if (logger != null) {
            boolean pushLogTag = (NestedDiagnosticContext.peek() == null) && (logTag != null);
            if (pushLogTag) {
                NestedDiagnosticContext.push(logTag);
            }
            boolean setCurrentLogTag = (getCurrentLogTag() == null) && (logTag != null);
            if (setCurrentLogTag) {
                setCurrentLogTag(logTag);
            }
            try {
                logAt.log(logger, t);
            }
            finally {
                if (setCurrentLogTag) {
                    clearCurrentLogTag();
                }
                if (pushLogTag) {
                    NestedDiagnosticContext.pop();
                }
            }
        }
    }

    // Attempt to dynamically bind to registrar.base.log.Log.
    private static RegistrarUtilsLog createRegistrarUtilsLog() {
        Class<?> registrarBaseLog = null;
        try {
            registrarBaseLog = Class.forName("registrar.base.log.Log");
        }
        catch (Throwable t) {}
        if (registrarBaseLog != null) {
            try {
                final Method checkSeverityMethod =
                    registrarBaseLog.getDeclaredMethod("checkSeverity", int.class);
                final Method logMessageMethod =
                    registrarBaseLog.getDeclaredMethod("log", Object.class, String.class, int.class, String.class, String.class);
                final Method logThrowableMethod =
                    registrarBaseLog.getDeclaredMethod("log", Object.class, String.class, int.class, String.class, Throwable.class);

                return new RegistrarUtilsLog() {
                    public boolean checkSeverity(int severity) {
                        try {
                            return (Boolean)checkSeverityMethod.invoke(null, severity);
                        }
                        catch (Throwable t) {}
                        return false;
                    }

                    public void log(Object callingClass, String component, int severity, String preface, String message) {
                        try {
                            logMessageMethod.invoke(null, callingClass, component, severity, preface, message);
                        }
                        catch (Throwable t) {}
                    }

                    public void log(Object callingClass, String component, int severity, String preface, Throwable throwable) {
                        try {
                            logThrowableMethod.invoke(null, callingClass, component, severity, preface, throwable);
                        }
                        catch (Throwable t) {}
                    }
                };
            }
            catch (Throwable t) {}
        }

        return new RegistrarUtilsLog() {
            public boolean checkSeverity(int severity) {
                return false;
            }

            public void log(Object callingClass, String component, int severity, String preface, String message) {}

            public void log(Object callingClass, String component, int severity, String preface, Throwable throwable) {}
        };
    }

    protected static interface RegistrarUtilsLog {
        /**
         * registrar.base.log.Log.checkSeverity
         */
        public abstract boolean checkSeverity(int severity);

        /**
         * registrar.base.log.Log.log
         */
        public abstract void log(Object callingClass, String component, int severity, String preface, String message);

        /**
         * registrar.base.log.Log.log
         */
        public abstract void log(Object callingClass, String component, int severity, String preface, Throwable throwable);
    }

    private static interface LogAtLevel {
        /**
         * Log a message.
         */
        public abstract void log(org.apache.commons.logging.Log logger, String message);

        /**
         * Log an error or exception.
         */
        public abstract void log(org.apache.commons.logging.Log logger, Throwable t);
    };
}
