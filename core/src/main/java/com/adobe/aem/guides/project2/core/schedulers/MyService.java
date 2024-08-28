package com.adobe.aem.guides.project2.core.schedulers;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(service = MyService.class)
public class MyService {

    @Reference
    private MyConfigService configService;

    public String getClientId() {
        return configService.getClientId();
    }

    public String getApiToken() {
        return configService.getApiToken();
    }

    public String getPagePath() {
        return configService.getPagePath();
    }
}