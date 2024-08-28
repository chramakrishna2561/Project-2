package com.adobe.aem.guides.project2.core.Config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Client Configuration")
public @interface ClientConfig {

    @AttributeDefinition(name = "Client ID")
    String clientId() default "";

    @AttributeDefinition(name = "API Token")
    String apiToken() default "";

    @AttributeDefinition(name = "Page Path")
    String pagePath() default "";
}
