package com.adobe.aem.guides.project2.core.models;

import java.util.List;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class Carouselsling {

    @ChildResource
    public List<Carouselmulti> multifield;

    public List<Carouselmulti> getMultifield() {
        return multifield;
    }

}