/*
 * Copyright (c) 2022 Airbyte, Inc., all rights reserved.
 */

package io.airbyte.workers.config;

import io.airbyte.workers.exception.WorkerException;
import io.airbyte.workers.temporal.TemporalUtils;
import io.airbyte.workers.temporal.check.connection.CheckConnectionActivity;
import io.airbyte.workers.temporal.discover.catalog.DiscoverCatalogActivity;
import io.airbyte.workers.temporal.scheduling.activities.AutoDisableConnectionActivity;
import io.airbyte.workers.temporal.scheduling.activities.ConfigFetchActivity;
import io.airbyte.workers.temporal.scheduling.activities.ConnectionDeletionActivity;
import io.airbyte.workers.temporal.scheduling.activities.GenerateInputActivity;
import io.airbyte.workers.temporal.scheduling.activities.JobCreationAndStatusUpdateActivity;
import io.airbyte.workers.temporal.scheduling.activities.RecordMetricActivity;
import io.airbyte.workers.temporal.scheduling.activities.RouteToSyncTaskQueueActivity;
import io.airbyte.workers.temporal.scheduling.activities.StreamResetActivity;
import io.airbyte.workers.temporal.scheduling.activities.WorkflowConfigActivity;
import io.airbyte.workers.temporal.spec.SpecActivity;
import io.airbyte.workers.temporal.sync.DbtTransformationActivity;
import io.airbyte.workers.temporal.sync.NormalizationActivity;
import io.airbyte.workers.temporal.sync.NormalizationSummaryCheckActivity;
import io.airbyte.workers.temporal.sync.PersistStateActivity;
import io.airbyte.workers.temporal.sync.ReplicationActivity;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Property;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.annotation.Value;
import io.temporal.activity.ActivityCancellationType;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import java.time.Duration;
import java.util.List;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Micronaut bean factory for activity-related singletons.
 */
@Factory
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class ActivityBeanFactory {

  @Singleton
  @Requires(property = "airbyte.worker.plane",
            pattern = "(?i)^(?!data_plane).*")
  @Named("checkConnectionActivities")
  public List<Object> checkConnectionActivities(
                                                final CheckConnectionActivity checkConnectionActivity) {
    return List.of(checkConnectionActivity);
  }

  @Singleton
  @Requires(property = "airbyte.worker.plane",
            pattern = "(?i)^(?!data_plane).*")
  @Named("connectionManagerActivities")
  public List<Object> connectionManagerActivities(
                                                  final GenerateInputActivity generateInputActivity,
                                                  final JobCreationAndStatusUpdateActivity jobCreationAndStatusUpdateActivity,
                                                  final ConfigFetchActivity configFetchActivity,
                                                  final ConnectionDeletionActivity connectionDeletionActivity,
                                                  final CheckConnectionActivity checkConnectionActivity,
                                                  final AutoDisableConnectionActivity autoDisableConnectionActivity,
                                                  final StreamResetActivity streamResetActivity,
                                                  final RecordMetricActivity recordMetricActivity,
                                                  final WorkflowConfigActivity workflowConfigActivity,
                                                  final RouteToSyncTaskQueueActivity routeToSyncTaskQueueActivity) {
    return List.of(generateInputActivity,
        jobCreationAndStatusUpdateActivity,
        configFetchActivity,
        connectionDeletionActivity,
        checkConnectionActivity,
        autoDisableConnectionActivity,
        streamResetActivity,
        recordMetricActivity,
        workflowConfigActivity,
        routeToSyncTaskQueueActivity);
  }

  @Singleton
  @Requires(property = "airbyte.worker.plane",
            pattern = "(?i)^(?!data_plane).*")
  @Named("discoverActivities")
  public List<Object> discoverActivities(
                                         final DiscoverCatalogActivity discoverCatalogActivity) {
    return List.of(discoverCatalogActivity);
  }

  @Singleton
  @Requires(property = "airbyte.worker.plane",
            pattern = "(?i)^(?!data_plane).*")
  @Named("specActivities")
  public List<Object> specActivities(
                                     final SpecActivity specActivity) {
    return List.of(specActivity);
  }

  @Singleton
  @Named("syncActivities")
  public List<Object> syncActivities(
                                     final ReplicationActivity replicationActivity,
                                     final NormalizationActivity normalizationActivity,
                                     final DbtTransformationActivity dbtTransformationActivity,
                                     final PersistStateActivity persistStateActivity,
                                     final NormalizationSummaryCheckActivity normalizationSummaryCheckActivity) {
    return List.of(replicationActivity, normalizationActivity, dbtTransformationActivity, persistStateActivity, normalizationSummaryCheckActivity);
  }

  @Singleton
  @Named("checkActivityOptions")
  public ActivityOptions checkActivityOptions() {
    return ActivityOptions.newBuilder()
        .setScheduleToCloseTimeout(Duration.ofMinutes(5))
        .setRetryOptions(TemporalUtils.NO_RETRY)
        .build();
  }

  @Singleton
  @Named("discoveryActivityOptions")
  public ActivityOptions discoveryActivityOptions() {
    return ActivityOptions.newBuilder()
        .setScheduleToCloseTimeout(Duration.ofHours(2))
        .setRetryOptions(TemporalUtils.NO_RETRY)
        .build();
  }

  @Singleton
  @Named("longRunActivityOptions")
  public ActivityOptions longRunActivityOptions(
                                                @Value("${airbyte.worker.sync.max-timeout}") final Long maxTimeout,
                                                @Named("longRunActivityRetryOptions") final RetryOptions retryOptions) {
    return ActivityOptions.newBuilder()
        .setScheduleToCloseTimeout(Duration.ofDays(maxTimeout))
        .setStartToCloseTimeout(Duration.ofDays(maxTimeout))
        .setScheduleToStartTimeout(Duration.ofDays(maxTimeout))
        .setCancellationType(ActivityCancellationType.WAIT_CANCELLATION_COMPLETED)
        .setRetryOptions(retryOptions)
        .setHeartbeatTimeout(TemporalUtils.HEARTBEAT_TIMEOUT)
        .build();
  }

  @Singleton
  @Named("shortActivityOptions")
  public ActivityOptions shortActivityOptions(@Property(name = "airbyte.activity.max-timeout",
                                                        defaultValue = "120") final Long maxTimeout,
                                              @Named("shortRetryOptions") final RetryOptions shortRetryOptions) {
    return ActivityOptions.newBuilder()
        .setStartToCloseTimeout(Duration.ofSeconds(maxTimeout))
        .setCancellationType(ActivityCancellationType.WAIT_CANCELLATION_COMPLETED)
        .setRetryOptions(shortRetryOptions)
        .setHeartbeatTimeout(TemporalUtils.HEARTBEAT_TIMEOUT)
        .build();
  }

  @Singleton
  @Requires(property = "airbyte.worker.plane",
            pattern = "(?i)^(?!data_plane).*")
  @Named("specActivityOptions")
  public ActivityOptions specActivityOptions() {
    return ActivityOptions.newBuilder()
        .setScheduleToCloseTimeout(Duration.ofHours(1))
        .setRetryOptions(TemporalUtils.NO_RETRY)
        .build();
  }

  @Singleton
  @Requires(property = "airbyte.container.orchestrator.enabled",
            value = "true")
  @Named("longRunActivityRetryOptions")
  public RetryOptions containerOrchestratorRetryOptions() {
    return RetryOptions.newBuilder()
        .setDoNotRetry(RuntimeException.class.getName(), WorkerException.class.getName())
        .build();
  }

  @Singleton
  @Requires(property = "airbyte.container.orchestrator.enabled",
            notEquals = "true")
  @Named("longRunActivityRetryOptions")
  public RetryOptions noRetryOptions() {
    return TemporalUtils.NO_RETRY;
  }

  @Singleton
  @Named("shortRetryOptions")
  public RetryOptions shortRetryOptions(@Property(name = "airbyte.activity.max-attempts",
                                                  defaultValue = "5") final Integer activityNumberOfAttempts,
                                        @Property(name = "airbyte.activity.initial-delay",
                                                  defaultValue = "30") final Integer initialDelayBetweenActivityAttemptsSeconds,
                                        @Property(name = "airbyte.activity.max-delay",
                                                  defaultValue = "600") final Integer maxDelayBetweenActivityAttemptsSeconds) {
    return RetryOptions.newBuilder()
        .setMaximumAttempts(activityNumberOfAttempts)
        .setInitialInterval(Duration.ofSeconds(initialDelayBetweenActivityAttemptsSeconds))
        .setMaximumInterval(Duration.ofSeconds(maxDelayBetweenActivityAttemptsSeconds))
        .build();
  }

}
