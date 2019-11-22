package com.baedose.producer.run;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.util.Properties;

import javax.sql.DataSource;
 
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

@SpringBootApplication(exclude=HibernateJpaAutoConfiguration.class,scanBasePackages = "com.baedose")
/*@EnableAutoConfiguration(exclude = {   
        DataSourceAutoConfiguration.class, 
        DataSourceTransactionManagerAutoConfiguration.class, 
        HibernateJpaAutoConfiguration.class })
@EnableDiscoveryClient*/
public class AccountsMicroserviceServerApplication {
	@Autowired
    private Environment env;

	public static void main(String[] args) {
		SpringApplication.run(AccountsMicroserviceServerApplication.class, args);
	}
	@Bean(name = "dataSource")
    public DataSource getDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
 
        // See: application.properties
        dataSource.setDriverClassName(env.getProperty("spring.datasource.driver-class-name"));
        dataSource.setUrl(env.getProperty("spring.datasource.url"));
        dataSource.setUsername(env.getProperty("spring.datasource.username"));
        dataSource.setPassword(env.getProperty("spring.datasource.password"));
 
        System.out.println("## getDataSource: " + dataSource);
 
        return dataSource;
    }
 
    @Autowired
    @Bean(name = "sessionFactory")
    public SessionFactory getSessionFactory(DataSource dataSource) throws Exception {
        Properties properties = new Properties();
        System.out.println("## getSessionFactory: " + dataSource);
        // See: application.properties  
        properties.put("hibernate.dialect", env.getProperty("spring.jpa.properties.hibernate.dialect"));
        System.out.println("## getSessionFactory: " + properties);
        properties.put("hibernate.show_sql", env.getProperty("spring.jpa.show-sql"));
        System.out.println("## getSessionFactory: " + properties);
        properties.put("current_session_context_class", //
                env.getProperty("spring.jpa.properties.hibernate.current_session_context_class"));
        System.out.println("## getSessionFactory: " + properties);
        LocalSessionFactoryBean factoryBean = new LocalSessionFactoryBean();
        System.out.println("## getSessionFactory: " + properties);
        // Package contain entity classes
        factoryBean.setPackagesToScan(new String[] {"com.baedose.config","com.baedose.controller","com.baedose.service",
        		"com.baedose.dao","com.baedose.producer.run"});
        System.out.println("## factorybean: " + factoryBean);
        factoryBean.setDataSource(dataSource);
        System.out.println("## factorybean: " + factoryBean);
        factoryBean.setHibernateProperties(properties);
        System.out.println("## factorybean: " + factoryBean);
        factoryBean.afterPropertiesSet();
        System.out.println("## factorybean: " + factoryBean);
        SessionFactory sf = factoryBean.getObject();
        System.out.println("## getSessionFactory: " + sf);
        return sf;
    }
 
    @Autowired
    @Bean(name = "transactionManager")
    public HibernateTransactionManager getTransactionManager(SessionFactory sessionFactory) {
        HibernateTransactionManager transactionManager = new HibernateTransactionManager(sessionFactory);
 
        return transactionManager;
    }
    
    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        // Load file: validation.properties
        messageSource.setBasename("classpath:validation");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }


}

