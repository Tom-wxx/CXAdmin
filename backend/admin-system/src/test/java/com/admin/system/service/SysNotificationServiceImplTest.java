package com.admin.system.service;

import com.admin.system.mapper.SysNotificationMapper;
import com.admin.system.service.impl.SysNotificationServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("SysNotificationServiceImpl 站内通知服务测试")
@ExtendWith(MockitoExtension.class)
class SysNotificationServiceImplTest {

    @InjectMocks
    private SysNotificationServiceImpl notificationService;

    @Mock
    private SysNotificationMapper notificationMapper;

    @Mock
    private ISysNotificationTemplateService templateService;

    @Test
    @DisplayName("统计待办任务 - 使用 Mapper 的未读待办/审批口径")
    void countPendingTasks_shouldUseMapperPendingTaskCount() {
        when(notificationMapper.countPendingTasks()).thenReturn(6L);

        Long result = notificationService.countPendingTasks();

        assertEquals(6L, result);
        verify(notificationMapper).countPendingTasks();
    }
}
