package com.chat.config;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.LayoutComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.RootLoggerComponentBuilder;

public class Log4jConfig {

	protected static final String LOG_PATTERN = "%d{yyyy-MM-dd HH:mm:ss} %-5level %c{1.} - %msg%n";
    protected static final String APP_NAME = "ChatApp";

    protected void setupLogger(ConfigurationBuilder<BuiltConfiguration> builder) {
        LayoutComponentBuilder layoutBuilder = builder.newLayout("PatternLayout").addAttribute("pattern", LOG_PATTERN);

        RootLoggerComponentBuilder rootLogger = builder.newRootLogger(Level.INFO);

        builder.newLogger("com.beas", Level.DEBUG)
                .addAttribute("additivity", false);

        builder.newLogger("org.springframework", Level.INFO)
               .addAttribute("additivity", false);
        builder.newLogger("org.hibernate.SQL", Level.DEBUG)
               .addAttribute("additivity", false);
        builder.newLogger("org.hibernate.type.descriptor.sql.BasicBinder", Level.TRACE)
               .addAttribute("additivity", false);

        builder.add(rootLogger);
    }
}
