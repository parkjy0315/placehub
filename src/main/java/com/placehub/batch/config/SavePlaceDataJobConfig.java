package com.placehub.batch.config;

import com.placehub.base.util.PlaceProcessor;
import com.placehub.boundedContext.place.service.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.*;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.stream.IntStream;

@Configuration
@RequiredArgsConstructor
public class SavePlaceDataJobConfig {
    private final PlaceProcessor placeProcessor;
    private final PlaceService placeService;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    class MyTasklet implements Tasklet {
        @Override
        public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
            JobParameters jobParameters = chunkContext.getStepContext().getStepExecution().getJobParameters();
            String code = jobParameters.getString("categoryCode");
            double criteria = jobParameters.getDouble("criteria");
            int i = jobParameters.getDouble("i").intValue();
            double xDist = placeProcessor.getXdist();

            IntStream.range(0, (int) (xDist / criteria) + 1)
                    .mapToObj(j -> new int[]{i, j})
                    .forEach(coords -> {
                        placeProcessor.processDataAndSave(code, coords[0], coords[1], criteria);
                    });

            return RepeatStatus.FINISHED;
        }
    }

    @Bean
    public Job job1() {
        return new JobBuilder("job1", jobRepository)
            .start(step1())
            .build();
    }

    @Bean
    public Step step1() {
        return new StepBuilder("step1", jobRepository)
                .tasklet(new MyTasklet(), transactionManager)
                .build();
    }
}
