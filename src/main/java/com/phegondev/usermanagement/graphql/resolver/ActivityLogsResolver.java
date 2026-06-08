package com.phegondev.usermanagement.graphql.resolver;

import com.phegondev.usermanagement.dto.PageResponse;
import com.phegondev.usermanagement.entity.ActivityLogs;
import com.phegondev.usermanagement.entity.ActivityTypeEnum;
import com.phegondev.usermanagement.service.ActivityLogsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ActivityLogsResolver {

    private final ActivityLogsService activityLogsService;

    @QueryMapping
    public PageResponse<ActivityLogs> getAllActivities(
            @Argument(name = "offset") Integer offset,
            @Argument(name = "limit") Integer limit) {
        return activityLogsService.getAllActivities(
                offset != null ? offset : 0,
                limit != null ? limit : 10);
    }

    @QueryMapping
    public PageResponse<ActivityLogs> getActivitiesByUser(
            @Argument UUID userId,
            @Argument(name = "offset") Integer offset,
            @Argument(name = "limit") Integer limit) {
        return activityLogsService.getActivitiesByUser(
                userId,
                offset != null ? offset : 0,
                limit != null ? limit : 10);
    }

    @QueryMapping
    public PageResponse<ActivityLogs> getActivitiesByType(
            @Argument ActivityTypeEnum activityType,
            @Argument(name = "offset") Integer offset,
            @Argument(name = "limit") Integer limit) {
        return activityLogsService.getActivitiesByType(
                activityType,
                offset != null ? offset : 0,
                limit != null ? limit : 10);
    }

    @QueryMapping
    public PageResponse<ActivityLogs> getActivitiesByEntityType(
            @Argument String entityType,
            @Argument(name = "offset") Integer offset,
            @Argument(name = "limit") Integer limit) {
        return activityLogsService.getActivitiesByEntityType(
                entityType,
                offset != null ? offset : 0,
                limit != null ? limit : 10);
    }

    @QueryMapping
    public PageResponse<ActivityLogs> getActivitiesByEntity(
            @Argument String entityType,
            @Argument UUID entityId,
            @Argument(name = "offset") Integer offset,
            @Argument(name = "limit") Integer limit) {
        return activityLogsService.getActivitiesByEntity(
                entityType, entityId,
                offset != null ? offset : 0,
                limit != null ? limit : 10);
    }

    @QueryMapping
    public PageResponse<ActivityLogs> getActivitiesByDateRange(
            @Argument String startDate,
            @Argument String endDate,
            @Argument(name = "offset") Integer offset,
            @Argument(name = "limit") Integer limit) {
        return activityLogsService.getActivitiesByDateRange(
                LocalDateTime.parse(startDate),
                LocalDateTime.parse(endDate),
                offset != null ? offset : 0,
                limit != null ? limit : 10);
    }

    @QueryMapping
    public PageResponse<ActivityLogs> searchActivities(
            @Argument String searchTerm,
            @Argument(name = "offset") Integer offset,
            @Argument(name = "limit") Integer limit) {
        return activityLogsService.searchActivities(
                searchTerm,
                offset != null ? offset : 0,
                limit != null ? limit : 10);
    }

    @QueryMapping
    public ActivityLogs getActivityById(@Argument UUID id) {
        return activityLogsService.getActivityById(id).orElse(null);
    }
}
