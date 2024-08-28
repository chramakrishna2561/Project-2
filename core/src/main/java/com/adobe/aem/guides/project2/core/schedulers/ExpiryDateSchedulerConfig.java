package com.adobe.aem.guides.project2.core.schedulers;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Expiry Date Scheduler Configuration")
public @interface ExpiryDateSchedulerConfig {

    @AttributeDefinition(name = "Cron Expression", description = "Cron expression for scheduler")
    String schedulerExpression() default "0 0/3 * 1/1 * ? *"; // Default to every 3 minutes

    @AttributeDefinition(name = "Root Page Path", description = "Path to start searching for pages under /content")
    String rootPath() default "/content"; // Default to /content to include all projects
}