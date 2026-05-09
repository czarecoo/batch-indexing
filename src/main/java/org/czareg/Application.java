package org.czareg;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.parameters.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@Slf4j
@SpringBootApplication
public class Application {

    public static final String JOB_NAME = "indexingJob";

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public Job indexingJob(JobRepository jobRepository, Step indexingStep) {
        return new JobBuilder(JOB_NAME, jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(indexingStep)
                .build();
    }

    @Bean
    public Step indexingStep(JobRepository jobRepository) {
        return new StepBuilder("indexingStep", jobRepository)
                .<DbEvent, IndexEvent>chunk(100)
                .reader(new IndexingReader(100))
                .processor(new IndexingProcessor())
                .writer(new IndexingWriter())
                .build();
    }
}
