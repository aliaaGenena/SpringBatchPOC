package org.example.springbatchpoc.config;

import org.example.springbatchpoc.entity.Customer;
import org.example.springbatchpoc.repo.CustomerRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.JobExplorerFactoryBean;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.dao.DefaultExecutionContextSerializer;
import org.springframework.batch.core.repository.dao.Jackson2ExecutionContextStringSerializer;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import lombok.AllArgsConstructor;

import javax.sql.DataSource;

@Configuration
@AllArgsConstructor
@EnableBatchProcessing

public class SpringBatchConfig {

	private final CustomerRepository customerRepository;

	@Bean("customerFlatFileReader")
	@StepScope
	public FlatFileItemReader<Customer> customerReader() {
		FlatFileItemReader<Customer> reader = new FlatFileItemReader<>();
		reader.setResource(new ClassPathResource("customers.csv")); // Update if path differs
		reader.setLinesToSkip(1); // Skip CSV header
		reader.setSaveState(true);
		reader.setName("customerFlatFileReader");

		DefaultLineMapper<Customer> lineMapper = new DefaultLineMapper<>();

		DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
		tokenizer.setNames("customerId", "firstName", "lastName", "email", "gender", "contactNo", "country", "dob");

		BeanWrapperFieldSetMapper<Customer> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
		fieldSetMapper.setTargetType(Customer.class);

		lineMapper.setLineTokenizer(tokenizer);
		lineMapper.setFieldSetMapper(fieldSetMapper);
		reader.setLineMapper(lineMapper);

		return reader;
	}

	@Bean
	@StepScope
	public CustomerProcessor customerProcessor() {
		return new CustomerProcessor();
	}

	@Bean
	@StepScope
	public RepositoryItemWriter<Customer> customerWriter() {
		RepositoryItemWriter<Customer> writer = new RepositoryItemWriter<>();
		writer.setRepository(customerRepository);
		writer.setMethodName("save");
		return writer;
	}

	@Bean
	public Step step1(JobRepository jobRepository,
					  PlatformTransactionManager transactionManager,
					  @Qualifier("customerFlatFileReader") FlatFileItemReader<Customer> customerReader) {
		return new StepBuilder("step1", jobRepository)
				.<Customer, Customer>chunk(1, transactionManager)
				.reader(customerReader) // ✅ use wrapper with state tracking
				.processor(customerProcessor())
				.writer(customerWriter())
				.build();
	}

	@Bean
	public Job importUserJob(JobRepository jobRepository, Step step1) {
		return new JobBuilder("customerJob", jobRepository)
				.start(step1)
				.build();
	}







}