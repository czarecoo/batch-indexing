package org.czareg;

import jakarta.jms.ConnectionFactory;
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
import org.springframework.core.retry.RetryPolicy;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.util.backoff.FixedBackOff;

@Slf4j
@SpringBootApplication
public class Application {

    public static final String INDEX_QUEUE = "indexing.queue";
    public static final String JOB_NAME = "indexingJob";
    public static final String STEP_NAME = "indexingStep";
    public static final int CHUNK_SIZE = 100;

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
    public Step indexingStep(JobRepository jobRepository, JmsTemplate jmsTemplate) {
        return new StepBuilder(STEP_NAME, jobRepository)
                .<DbEvent, IndexEvent>chunk(CHUNK_SIZE)
                .reader(new IndexingReader(CHUNK_SIZE))
                .processor(new IndexingProcessor())
                .writer(new IndexingJmsWriter(jmsTemplate))
                .faultTolerant()
                .retryPolicy(RetryPolicy.builder().backOff(new FixedBackOff(1000L)).build())
                .retry(Exception.class)
                .build();
    }

    @Bean
    public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory) {
        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setDefaultDestinationName(INDEX_QUEUE);
        jmsTemplate.setExplicitQosEnabled(true);
        jmsTemplate.setDeliveryPersistent(true);
        return jmsTemplate;
    }
}
