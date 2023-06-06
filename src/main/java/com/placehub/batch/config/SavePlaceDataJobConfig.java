package com.placehub.batch.config;

import com.placehub.base.util.LocalApi;
import com.placehub.base.util.PlaceData;
import com.placehub.boundedContext.place.repository.PlaceRepository;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.boot.autoconfigure.batch.JobLauncherApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;
import java.util.stream.Collectors;

@EnableBatchProcessing
@Configuration
@EnableScheduling
public class SavePlaceDataJobConfig {
//    @Autowired
//    private final PlaceData placeData;
//    @Autowired
//    private final PlaceRepository placeRepository;
//    @Autowired
//    private final PlatformTransactionManager transactionManager;
//
//    @Bean
//    public Job savePlaceDataJob(JobRepository jobRepository) {
//        return new JobBuilder("savePlaceDataJob", jobRepository)
//                .start(step1(jobRepository))
//                .build();
//    }
//
//    @Bean
//    public Step step1(JobRepository jobRepository) {
//        return new StepBuilder("savePlaceDataReadStep", jobRepository)
//                .tasklet(new MyTasklet(), transactionManager)
//                .build();
//    }
//
//    class MyTasklet implements Tasklet {
//        @Override
//        public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
//            System.out.println("tasklet test");
////            int page = 1;
////            int size = 15;
////            double[] coords = placeData.getNextCoord(13, 12, 0.005);
////            String rect = placeData.convertRectString(coords);
////            JSONObject result = LocalApi.Category.getAllRect(rect, "CE7", page, size);
////            List<JSONObject> data = (List<JSONObject>) ((JSONArray) result.get("documents"))
////                    .stream()
////                    .collect(Collectors.toList());
////
////            data.stream()
////                    .forEach(element -> placeRepository.save(placeData.convertPlace(element)));
//
//            return RepeatStatus.FINISHED;
//        }
//    }
//
//    @Bean
//    public Tasklet taskletTest() {
//        return (contribution, chunkContext) -> {
//            System.out.println("tasklet test");
//            int page = 1;
//            int size = 15;
//            double[] coords = placeData.getNextCoord(13, 12, 0.005);
//            String rect = placeData.convertRectString(coords);
//            JSONObject result = LocalApi.Category.getAllRect(rect, "CE7", page, size);
//            List<JSONObject> data = (List<JSONObject>) ((JSONArray) result.get("documents"))
//                    .stream()
//                    .collect(Collectors.toList());
//
//            data.stream()
//                    .forEach(element -> placeRepository.save(placeData.convertPlace(element)));
//
//            return RepeatStatus.FINISHED;
//        };
//    }
//
//
//    @Bean
//    public ListItemReader<JSONObject> savePlaceDataReader() {
//        int page = 1;
//        int size = 15;
//        double[] coords = placeData.getNextCoord(13, 12, 0.005);
//        String rect = placeData.convertRectString(coords);
//        JSONObject result = LocalApi.Category.getAllRect(rect, "CE7", page, size);
//        List<JSONObject> data = (List<JSONObject>) ((JSONArray) result.get("documents"))
//                .stream()
//                .collect(Collectors.toList());
//        return new ListItemReader<>(data);
//    }
//
//    @Bean
//    public ItemProcessor<JSONObject, String> savePlaceDataProcessor() {
//        return element -> {
//            return "place_name";
//            //return placeData.convertPlace(element);
//        };
//    }
//
//    @Bean
//    public ItemWriter<String> savePlaceDataWriter() {
//        return items -> {
//            System.out.println(items.getItems().get(0));
//        };
////        return chunk -> {
////            for (Place item : chunk.getItems()) {
////                System.out.println(item.getPlaceName());
////            }
////        };
////        return chunk -> places -> {
////            List<Place> list = (List<Place>) places.getItems();
////            placeRepository.saveAll(list);
////        };
//    }

    @Autowired
    JobRepository jobRepository;
    @Autowired
    PlatformTransactionManager transactionManager;

    @Bean
    public Job job123(JobRepository jobRepository) {return new JobBuilder("job1", jobRepository)
                .start(step123(jobRepository))
                .build();
    }

    @Bean
    public Step step123(JobRepository jobRepository) {
        return new StepBuilder("step1", jobRepository)
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("this is example");

                        return RepeatStatus.FINISHED;
                    }
                }, transactionManager)
                .build();
    }
}
