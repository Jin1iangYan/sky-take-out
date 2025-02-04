package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 切面，实现公共字段自动填充
 */
@Aspect
@Component
@Slf4j
public class AutoFillAspect {

    /**
     * 切入点，mapper包下的所有加入AutoFill的注解的方法
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointcut() {}

    /**
     * 前置通知，在通知中进行公共字段的赋值
     */
    @Before("autoFillPointcut()")
    public void autoFill(JoinPoint joinPoint) {
        log.info("开始进行公共字段的自动填充...");

        // 获取方法签名及注解中的操作类型
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        AutoFill autoFillAnnotation = signature.getMethod().getAnnotation(AutoFill.class);
        OperationType operationType = autoFillAnnotation.value();

        // 获取方法参数，假定第一个参数为实体对象
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            return;
        }
        Object entity = args[0];
        Class<?> entityClass = entity.getClass();

        // 准备赋值数据
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        try {
            // 根据操作类型调用不同的字段赋值逻辑
            if (operationType == OperationType.INSERT) {
                // 为创建和更新相关字段赋值
                invokeMethod(entity, entityClass, AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class, now);
                invokeMethod(entity, entityClass, AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class, now);
                invokeMethod(entity, entityClass, AutoFillConstant.SET_CREATE_USER, Long.class, currentId);
                invokeMethod(entity, entityClass, AutoFillConstant.SET_UPDATE_USER, Long.class, currentId);
            } else if (operationType == OperationType.UPDATE) {
                // 仅为更新相关字段赋值
                invokeMethod(entity, entityClass, AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class, now);
                invokeMethod(entity, entityClass, AutoFillConstant.SET_UPDATE_USER, Long.class, currentId);
            }
        } catch (Exception e) {
            throw new RuntimeException("自动填充公共字段时发生异常", e);
        }
    }

    /**
     * 通过反射调用实体对象的setter方法为字段赋值
     *
     * @param target 实体对象
     * @param clazz 实体对象的Class
     * @param methodName 方法名
     * @param paramType 方法参数类型
     * @param value 赋予的值
     * @throws NoSuchMethodException 如果未找到方法
     * @throws InvocationTargetException 如果方法调用出现异常
     * @throws IllegalAccessException 如果无法访问方法
     */
    private void invokeMethod(Object target, Class<?> clazz, String methodName, Class<?> paramType, Object value)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = clazz.getDeclaredMethod(methodName, paramType);
        method.setAccessible(true); // 如果方法不可见，设置为可访问
        method.invoke(target, value);
    }
}
