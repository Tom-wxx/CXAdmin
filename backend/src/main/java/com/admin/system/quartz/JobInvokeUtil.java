package com.admin.system.quartz;

import com.admin.system.entity.SysJob;
import com.admin.system.utils.SpringUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

/**
 * 任务执行工具
 *
 * @author Admin
 */
public class JobInvokeUtil {

    /**
     * 执行方法
     *
     * @param sysJob 系统任务
     */
    public static void invokeMethod(SysJob sysJob) throws Exception {
        String invokeTarget = sysJob.getInvokeTarget();
        String beanName = getBeanName(invokeTarget);
        String methodName = getMethodName(invokeTarget);

        Object bean = SpringUtils.getBean(beanName);
        Method method = bean.getClass().getMethod(methodName);
        ReflectionUtils.invokeMethod(method, bean);
    }

    /**
     * 获取bean名称
     */
    public static String getBeanName(String invokeTarget) {
        int dotIndex = invokeTarget.lastIndexOf(".");
        if (dotIndex > 0) {
            return invokeTarget.substring(0, dotIndex);
        }
        return invokeTarget;
    }

    /**
     * 获取方法名称
     */
    public static String getMethodName(String invokeTarget) {
        int dotIndex = invokeTarget.lastIndexOf(".");
        if (dotIndex > 0 && dotIndex < invokeTarget.length() - 1) {
            return invokeTarget.substring(dotIndex + 1);
        }
        return null;
    }
}
