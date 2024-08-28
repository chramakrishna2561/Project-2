package com.adobe.aem.guides.project2.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class Sidebarlinksmulti {

    @ValueMapValue
    private String name;
    @ValueMapValue
    private String image;

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

}