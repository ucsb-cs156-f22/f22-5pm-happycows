package edu.ucsb.cs156.happiercows.jobs;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import edu.ucsb.cs156.happiercows.entities.jobs.Job;
import edu.ucsb.cs156.happiercows.services.jobs.JobContext;

public class MilkTheCowsJobTests {
    @Test
    void test_log_output() throws Exception {

        // Arrange

        Job jobStarted = Job.builder().build();
        JobContext ctx = new JobContext(null, jobStarted);

        // Act
        MilkTheCowsJob milkTheCowsJob = MilkTheCowsJob.builder()
                .build();
        milkTheCowsJob.accept(ctx);

        // Assert

        String expected = null;

        assertEquals(expected, jobStarted.getLog());

    }
}