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
@Profile("dev")
public class DevLog4j2Config extends Log4jConfig {

    @PostConstruct
    public void init() {
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();
        builder.setStatusLevel(Level.INFO);
        builder.setConfigurationName("DevConsoleConfig");

        builder.add(builder.newAppender("ConsoleAppender", "Console")
                .add(builder.newLayout("PatternLayout").addAttribute("pattern", LOG_PATTERN)));
        
        builder.newRootLogger(Level.INFO)
               .add(builder.newAppenderRef("ConsoleAppender"));

        builder.newLogger("com.beas", Level.DEBUG)
               .addAttribute("additivity", false)
               .add(builder.newAppenderRef("ConsoleAppender"));
        builder.newLogger("org.springframework", Level.INFO)
               .addAttribute("additivity", false)
               .add(builder.newAppenderRef("ConsoleAppender"));
        builder.newLogger("org.hibernate.SQL", Level.DEBUG)
               .addAttribute("additivity", false)
               .add(builder.newAppenderRef("ConsoleAppender"));
        builder.newLogger("org.hibernate.type.descriptor.sql.BasicBinder", Level.TRACE)
               .addAttribute("additivity", false)
               .add(builder.newAppenderRef("ConsoleAppender"));


        LogManager.shutdown();
        ctx.start(builder.build());
        System.out.println("--- Log4j2 'dev' profile configuration applied ---");
    }
}
