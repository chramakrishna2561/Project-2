package com.adobe.aem.guides.project2.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class Rkslingmodel {

    @ValueMapValue
    private String fname;

    @ValueMapValue
    private String lname;

    public String getFname() {
        return fname;
    }

    public String getLname() {
        return lname;
    }

}
