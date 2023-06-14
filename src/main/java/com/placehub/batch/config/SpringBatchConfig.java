package com.placehub.batch.config;

import com.placehub.base.util.PlaceProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
@Profile("prod")
public class SpringBatchConfig {
    private final PlaceProcessor placeProcessor;
    private final JobLauncher jobLauncher;
    private final Job job1;

//    @Scheduled(fixedDelay = 5000)
//    public void initBatch() {
//        List<String> categoryCode = new ArrayList<>() {{
//            add("AT4"); // 1111
//            add("CT1"); // 2522
//            add("CE7"); // 33501
//            add("FD6"); // 135651
//        }};
//
//        double yDist = placeProcessor.getYdist();
//        double criteria = 0.005;
//
//        categoryCode.stream()
//                .forEach(code -> {
//                    IntStream.range(0, (int) (yDist / criteria) + 1)
//                            .boxed()
//                            .forEach(i -> {
//                                JobParameters jobParameters = new JobParametersBuilder()
//                                        .addString("jobID", String.valueOf(System.currentTimeMillis()))
//                                        .addString("categoryCode", code)
//                                        .addDouble("criteria", criteria)
//                                        .addDouble("i", (double) i)
//                                        .toJobParameters();
//                                try {
//                                    jobLauncher.run(job1, jobParameters);
//                                } catch (JobExecutionAlreadyRunningException e) {
//                                    throw new RuntimeException(e);
//                                } catch (JobRestartException e) {
//                                    throw new RuntimeException(e);
//                                } catch (JobInstanceAlreadyCompleteException e) {
//                                    throw new RuntimeException(e);
//                                } catch (JobParametersInvalidException e) {
//                                    throw new RuntimeException(e);
//                                }
//                            });
//                });
//    }

    @Scheduled(cron = "0 0 3 * * MON-FRI")
    public void placeDataSaveBatch() {
        List<String> categoryCode = new ArrayList<>() {{
            add("AT4"); // 1111
            add("CT1"); // 2522
            add("CE7"); // 33501
            add("FD6"); // 135651
        }};

        double yDist = placeProcessor.getYdist();
        double criteria = 0.005;

        categoryCode.stream()
                .forEach(code -> {
                    IntStream.range(0, (int) (yDist / criteria) + 1)
                            .boxed()
                            .forEach(i -> {
                                JobParameters jobParameters = new JobParametersBuilder()
                                        .addString("jobID", String.valueOf(System.currentTimeMillis()))
                                        .addString("categoryCode", code)
                                        .addDouble("criteria", criteria)
                                        .addDouble("i", (double) i)
                                        .toJobParameters();
                                try {
                                    jobLauncher.run(job1, jobParameters);
                                } catch (JobExecutionAlreadyRunningException e) {
                                    throw new RuntimeException(e);
                                } catch (JobRestartException e) {
                                    throw new RuntimeException(e);
                                } catch (JobInstanceAlreadyCompleteException e) {
                                    throw new RuntimeException(e);
                                } catch (JobParametersInvalidException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                });
    }
}
