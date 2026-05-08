package com.internship.tool.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.internship.tool.entity.AuditLog;
import com.internship.tool.repository.AuditLogRepository;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
public class AuditAspect {

    @Autowired
    private AuditLogRepository repository;

    private final ObjectMapper mapper;

    public AuditAspect() {
        this.mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // ✅ Fix for LocalDateTime
    }

    @Around("execution(* com.internship.tool.service.*.*(..))")
    public Object logAudit(ProceedingJoinPoint joinPoint) throws Throwable {

        String methodName = joinPoint.getSignature().getName();

        // ❌ Skip read methods
        if (methodName.toLowerCase().contains("get") ||
            methodName.toLowerCase().contains("search")) {
            return joinPoint.proceed();
        }

        Object result = joinPoint.proceed();

        try {
            AuditLog log = new AuditLog();
            log.setEntityType("RECORD");
            log.setAction(methodName);
            log.setEntityId(0L);
            log.setOldValue("N/A");

            // ✅ SAFE JSON conversion
            try {
                log.setNewValue(mapper.writeValueAsString(result));
            } catch (Exception e) {
                log.setNewValue("SERIALIZATION_FAILED");
            }

            log.setCreatedAt(LocalDateTime.now());

            repository.save(log);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}