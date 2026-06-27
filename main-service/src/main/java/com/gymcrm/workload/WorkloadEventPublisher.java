package com.gymcrm.workload;

public interface WorkloadEventPublisher {
    void publish(TrainerWorkloadRequest request);
}
