package com.patres.framework.context;

import com.patres.framework.FrameworkException;
import com.patres.framework.ProxyFrameInvocationHandler;
import com.patres.framework.component.Autowired;
import com.patres.framework.component.Component;

import java.lang.reflect.Constructor;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class ApplicationContext {

    private static final Map<Class<?>, Object> singletonObject = new HashMap<>();

    public static <T> T getBeanDynamicProxy(final Class<T> clazz) {
        try {
            if (!clazz.isInterface()) {
                throw new FrameworkException("Class " + clazz.getName() + " need to be an interface");
            }

            final Class<T> implementationClass = findImplementation(clazz);
            final Constructor<T> constructor = findConstructor(implementationClass);
            final Object[] parameters = calculateParameters(constructor);
            final T bean = constructor.newInstance(parameters);

            final Object proxyObject = Proxy.newProxyInstance(
                    ApplicationContext.class.getClassLoader(),
                    new Class[]{clazz},
                    new ProxyFrameInvocationHandler(bean)
            );
            return clazz.cast(proxyObject);
        } catch (FrameworkException e) {
            throw e;
        } catch (Exception e) {
            throw new FrameworkException("Unknown exception: " + e.getMessage(), e);
        }
    }

    private static <T> Class<T> findImplementation(final Class<T> clazz) throws ClassNotFoundException {
        final Class<T> implementationClass = (Class<T>) Class.forName(clazz.getName() + "Impl");
        if (!implementationClass.isAnnotationPresent(Component.class)) {
            throw new FrameworkException("Class " + implementationClass.getName() + " is not a Component");
        }
        return implementationClass;
    }

    private static <T> Constructor<T> findConstructor(final Class<T> clazz) {
        final Constructor<T>[] constructors = (Constructor<T>[]) clazz.getConstructors();
        if (constructors.length == 1) {
            return constructors[0];
        }

        final List<Constructor<T>> constructorsWithAnnotation = Arrays.stream(constructors)
                .filter(it -> it.isAnnotationPresent(Autowired.class))
                .collect(Collectors.toList());

        if (constructorsWithAnnotation.size() > 1) {
            throw new FrameworkException("Cannot find more than one Autowired constructor for class " + clazz.getName());
        }

        return constructorsWithAnnotation.stream()
                .findFirst()
                .orElseThrow(() -> new FrameworkException("Cannot find Autowired constructor for class " + clazz.getName()));
    }

    private static Object[] calculateParameters(final Constructor<?> constructor) {
        final Class<?>[] parameterTypes = constructor.getParameterTypes();
        return Arrays.stream(parameterTypes)
                .map(parameterClass -> singletonObject.computeIfAbsent(parameterClass, ApplicationContext::getBeanDynamicProxy))
                .toArray(Object[]::new);
    }

}
