package com.patres.framework.context;

import com.patres.framework.FrameworkException;
import com.patres.framework.ProxyFrameInvocationHandler;
import com.patres.framework.component.Autowired;
import com.patres.framework.component.Component;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.stream.Collectors;


public class ApplicationContext {

    private final Map<Class<?>, Object> beanRegistry = new HashMap<>();
    private final Set<Class<?>> componentsClasses;

    public ApplicationContext(Class<?> mainClass) {
        final Reflections reflections = new Reflections(mainClass.getPackage().getName());
        this.componentsClasses = reflections.getTypesAnnotatedWith(Component.class).stream()
                .filter(clazz -> !clazz.isInterface())
                .collect(Collectors.toSet());
    }

    public <T> T getBean(final Class<T> clazz) {
        try {
            if (!clazz.isInterface()) {
                throw new FrameworkException("Class " + clazz.getName() + " need to be an interface");
            }
            final Class<T> implementationClass = getImplementationByInterface(clazz);
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

    @SuppressWarnings("unchecked")
    private <T> Class<T> getImplementationByInterface(Class<T> interfaceItem) {
        final Set<Class<?>> classesWithInterface = componentsClasses.stream()
                .filter(componentsClass -> Arrays.asList(componentsClass.getInterfaces()).contains(interfaceItem))
                .collect(Collectors.toSet());
        if (classesWithInterface.size() > 1) {
            throw new FrameworkException("There are more than one (" + classesWithInterface.size() + ")class with interface: " + interfaceItem);
        }
        return (Class<T>) classesWithInterface.stream()
                .findFirst()
                .orElseThrow(() -> new FrameworkException("There is no class with interface: " + interfaceItem));
    }

    @SuppressWarnings("unchecked")
    private <T> Constructor<T> findConstructor(final Class<T> clazz) {
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

    private Object[] calculateParameters(final Constructor<?> constructor) {
        final Class<?>[] parameterTypes = constructor.getParameterTypes();
        return Arrays.stream(parameterTypes)
                .map(parameterClass -> beanRegistry.computeIfAbsent(parameterClass, this::getBean))
                .toArray(Object[]::new);
    }

}
