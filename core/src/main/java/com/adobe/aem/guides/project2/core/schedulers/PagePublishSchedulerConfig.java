package com.adobe.aem.guides.project2.core.schedulers;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Page Publish Scheduler Configuration")
public @interface PagePublishSchedulerConfig {

    @AttributeDefinition(name = "Cron Expression", description = "Cron expression for scheduler")
    String schedulerExpression() default "0 0/1 * 1/1 * ? *";

    @AttributeDefinition(name = "Page Path", description = "Path of the pages to be published")
    String pagePath() default "/content/project2/us/en/sidebarpage";
}