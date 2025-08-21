package com.chat.config;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import jakarta.annotation.PostConstruct;

@Configuration
@Profile("prod")
public class ProdLog4j2Config extends Log4jConfig {

    @PostConstruct
    public void init() {
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();
        builder.setStatusLevel(Level.INFO);
        builder.setConfigurationName("ProdFileConfig");

        builder.add(builder.newAppender("RollingFileAppender", "RollingFile")
                .addAttribute("fileName", "logs/" + APP_NAME + ".log")
                .addAttribute("filePattern", "logs/" + APP_NAME + "-%d{yyyy-MM-dd}-%i.log.gz")
                .add(builder.newLayout("PatternLayout").addAttribute("pattern", LOG_PATTERN))
                .addComponent(builder.newComponent("Policies")
                        .addComponent(builder.newComponent("SizeBasedTriggeringPolicy").addAttribute("size", "10MB"))
                        .addComponent(builder.newComponent("TimeBasedTriggeringPolicy")))
                .addComponent(builder.newComponent("DefaultRolloverStrategy").addAttribute("max", "7")));

        builder.newRootLogger(Level.INFO)
               .add(builder.newAppenderRef("RollingFileAppender"));

        builder.newLogger("com.beas", Level.DEBUG)
               .addAttribute("additivity", false)
               .add(builder.newAppenderRef("RollingFileAppender"));
        builder.newLogger("org.springframework", Level.INFO)
               .addAttribute("additivity", false)
               .add(builder.newAppenderRef("RollingFileAppender"));
        builder.newLogger("org.hibernate.SQL", Level.DEBUG)
               .addAttribute("additivity", false)
               .add(builder.newAppenderRef("RollingFileAppender"));
        builder.newLogger("org.hibernate.type.descriptor.sql.BasicBinder", Level.TRACE)
               .addAttribute("additivity", false)
               .add(builder.newAppenderRef("RollingFileAppender"));

        LogManager.shutdown(); 
        ctx.start(builder.build());
    }
}
