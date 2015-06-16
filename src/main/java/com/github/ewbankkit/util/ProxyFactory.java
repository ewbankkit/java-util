/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.github.ewbankkit.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;


public final class ProxyFactory {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:20 ProxyFactory.java NSI";

    @SuppressWarnings("unchecked")
    public static <T> T newProxy(Class<T> classOfT, InvocationHandler invocationHandler) {
        return (T)Proxy.newProxyInstance(classOfT.getClassLoader(), new Class<?>[] {classOfT}, invocationHandler);
    }

    private ProxyFactory() {}
}
