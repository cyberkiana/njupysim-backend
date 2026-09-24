package org.njupt.njuptphysim.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用户操作日志注解。
 *
 * <p>标注在 Controller 的业务方法上，由 {@code OperationLogAspect} 切面拦截：
 * 控制台输出"哪个用户什么时间干了什么"，并调用 saveLog 将操作记录
 * 持久化到 sys_log_operation 表。登录/退出等接口不使用本注解，
 * 应在方法内直接持久化并输出控制台日志。</p>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLog {

    /**
     * 操作类型（如：查询、新增、修改、删除、预约等）
     */
    String value() default "";

    /**
     * 具体操作名称（如：添加班级信息）
     */
    String detail() default "";
}
