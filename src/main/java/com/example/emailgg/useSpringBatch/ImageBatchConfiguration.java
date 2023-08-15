package com.example.emailgg.useSpringBatch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class ImageBatchConfiguration {

    @Autowired
    DataSource dataSource;

    @Bean
    public FlatFileItemReader<Record> reader() {
        return new FlatFileItemReaderBuilder<Record>()
                .name("csvItemReader")
                .resource(new ClassPathResource("file.csv"))
                .delimited()
                .names(new String[]{"oldUrl"/*, ...*/})
                .fieldSetMapper(new BeanWrapperFieldSetMapper<Record>() {{
                    setTargetType(Record.class);
                }})
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<Record> writer() {
        return new JdbcBatchItemWriterBuilder<Record>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO images (old_url, new_url) VALUES (:oldUrl, :newUrl)")
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public Job importImageJob(JobCompletionNotificationListener listener, Step step1) {
        return jobBuilderFactory.get("importImageJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step1)
                .end()
                .build();
    }

    @Bean
    public Step step1(JdbcBatchItemWriter<Record> writer) {
        return stepBuilderFactory.get("step1")
                .<Record, Record>chunk(10)
                .reader(reader())
                .processor(new ImageProcessor())
                .writer(writer)
                .build();
    }



}

