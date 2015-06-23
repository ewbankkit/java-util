//
// Kit's Java Utils.
//

package com.github.ewbankkit;

import com.github.ewbankkit.base.ReflectiveRepresentation;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.MDC;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents an execution context.
 * An execution context starts when an HTTP request is received.
 * Manages the SLF4J Mapped Diagnostic Context.
 * Manages the Hystrix request context.
 */
public final class ExecutionContext {
    private static final ThreadLocal<ExecutionContext> TLS                      = new ThreadLocal<>();
    private static final String                        APP_VERSION_KEY          = "AppVersion";
    private static final String                        DEVICE_TYPE_KEY          = "DeviceType";
    private static final String                        EXECUTION_CONTEXT_ID_KEY = "ExecutionContextId";
    private static final String                        SESSION_ID_KEY           = "SessionId";
    private static final String                        USER_ID_KEY              = "UserId";
    private static final String                        CO_RELATION_ID_KEY           ="Co-Relation-ID";
    private static final String                        ENTERPRISE_SSO_ID_KEY           ="EnterpriseSSOId";

    private final AppVersion            appVersion;
    private final String                deviceType;
    private final UUID                  executionContextId;
    private final UUID                  sessionId;
    private final UUID                  userId;

    private final UUID                  clientCorelationID;


    private String                enterpriseSSOId;

    private final HystrixRequestContext hystrixRequestContext;

    private ExecutionContext(
        @Nullable
        AppVersion appVersion,
        @Nullable
        String     deviceType,
        @Nullable
        UUID       sessionId,
        @Nullable
        UUID       userId,
        @Nullable
        UUID clientCorelationID,
        @Nullable
        String enterpriseSSOId
    ) {
        this(UUID.randomUUID(), appVersion, deviceType, sessionId, userId,clientCorelationID,enterpriseSSOId);
    }

    private ExecutionContext(
        UUID       executionContextId,
        @Nullable
        AppVersion appVersion,
        @Nullable
        String     deviceType,
        @Nullable
        UUID       sessionId,
        @Nullable
        UUID       userId,
        @Nullable
        UUID clientCorelationID,
        @Nullable
        String enterpriseSSOId
    ) {
        this.appVersion = ObjectUtils.firstNonNull(appVersion, AppVersion.UNKNOWN);
        this.deviceType = ObjectUtils.firstNonNull(deviceType, "UNKNOWN");
        this.executionContextId = executionContextId;
        this.sessionId = sessionId;
        this.userId = userId;
        this.clientCorelationID=clientCorelationID;
        this.enterpriseSSOId = enterpriseSSOId;
        hystrixRequestContext = HystrixRequestContext.initializeContext();
    }

    @Nullable
    public static ExecutionContext getContextForCurrentThread() {
        return TLS.get();
    }

    public static ExecutionContext initializeContext() {
        return initializeContext(null, null, null, null,null,null);
    }

    public static ExecutionContext initializeContext(
        @Nullable
        AppVersion appVersion,
        @Nullable
        String     deviceType,
        @Nullable
        UUID       sessionId,
        @Nullable
        UUID       userId,
        @Nullable
        UUID clientCorelationID,
        @Nullable
        String enterpriseSSOId
    ) {
        ExecutionContext context = new ExecutionContext(appVersion, deviceType, sessionId, userId,clientCorelationID,enterpriseSSOId);
        setContextOnCurrentThread(context);
        return context;
    }

    public static ExecutionContext reinitializeContext(@Nullable UUID sessionId, @Nullable UUID userId,@Nullable UUID clientCorelationID, @Nullable String enterpriseSSOId) {
        ExecutionContext currentExecutionContext = ExecutionContext.getContextForCurrentThread();
        if (currentExecutionContext == null) {
            return null;
        }
        ExecutionContext newExecutionContext = new ExecutionContext(
            currentExecutionContext.executionContextId,
            currentExecutionContext.appVersion,
            currentExecutionContext.deviceType,
            ObjectUtils.firstNonNull(sessionId, currentExecutionContext.sessionId),
            ObjectUtils.firstNonNull(userId, currentExecutionContext.userId),
            ObjectUtils.firstNonNull(clientCorelationID,currentExecutionContext.clientCorelationID),
                ObjectUtils.firstNonNull(enterpriseSSOId,currentExecutionContext.enterpriseSSOId)
        );
        removeContextFromCurrentThread();
        setContextOnCurrentThread(newExecutionContext);
        return newExecutionContext;
    }

    public static void removeContextFromCurrentThread() {
        MDC.clear();

        HystrixRequestContext.setContextOnCurrentThread(null);

        TLS.remove();
    }

    public static void setContextOnCurrentThread(@Nullable ExecutionContext context) {
        if (context != null) {
            TLS.set(context);

            HystrixRequestContext.setContextOnCurrentThread(context.hystrixRequestContext);

            MDC.put(APP_VERSION_KEY, context.appVersion.toString());
            MDC.put(DEVICE_TYPE_KEY, context.deviceType);
            MDC.put(EXECUTION_CONTEXT_ID_KEY, context.executionContextId.toString());
            MDC.put(CO_RELATION_ID_KEY,context.executionContextId.toString());


            UUID sessionId = context.sessionId;
            if (sessionId == null) {
                MDC.remove(SESSION_ID_KEY);
            }
            else {
                MDC.put(SESSION_ID_KEY, sessionId.toString());
            }
            UUID userId = context.userId;
            if (userId == null) {
                MDC.remove(USER_ID_KEY);
            }
            else {
                MDC.put(USER_ID_KEY, userId.toString());
            }
            String enterpriseSSOId= context.enterpriseSSOId;
            if(enterpriseSSOId == null)
            {
                MDC.remove(ENTERPRISE_SSO_ID_KEY);
            }
            else
            {
                MDC.put(ENTERPRISE_SSO_ID_KEY,context.enterpriseSSOId.toString());
            }
        }
    }

    public AppVersion getAppVersion() {
        return appVersion;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public UUID getExecutionContextId() {
        return executionContextId;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void shutdown() {
        hystrixRequestContext.shutdown();
        removeContextFromCurrentThread();
    }

    public UUID getClientCorelationID() {
        return clientCorelationID;
    }

    public String getEnterpriseSSOId() {
        return enterpriseSSOId;
    }

    public void setEnterpriseSSOId(String enterpriseSSOId)
    {
        this.enterpriseSSOId=enterpriseSSOId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        ExecutionContext that = (ExecutionContext)obj;
        return Objects.equals(appVersion, that.appVersion) &&
               Objects.equals(deviceType, that.deviceType) &&
               Objects.equals(executionContextId, that.executionContextId) &&
               Objects.equals(sessionId, that.sessionId) &&
               Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(appVersion, deviceType, executionContextId, sessionId, userId);
    }

    @Override
    public String toString() {
        return ReflectiveRepresentation.toString(this);
    }
}
