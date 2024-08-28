package com.adobe.aem.guides.project2.core.models;

import java.util.List;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class Sidebarsling {
    @ValueMapValue
    private String logoPath;
    @ValueMapValue
    private String logolink;
    @ValueMapValue
    private String logomobileimage;
    @ValueMapValue
    private boolean enableswitch;
    @ValueMapValue
    private String country;

    @ChildResource
    public List<Sidebarlinksmulti> multifield;
    @ChildResource
    public List<Sidebarnavigationmulti> navigationmultifield;

    public List<Sidebarnavigationmulti> getNavigationmultifield() {
        return navigationmultifield;
    }

    public List<Sidebarlinksmulti> getMultifield() {
        return multifield;
    }

    public String getLogolink() {
        return logolink;
    }

    public boolean isEnableswitch() {
        return enableswitch;
    }

    public String getLogomobileimage() {
        return logomobileimage;
    }

    public String getLogoPath() {
        return logoPath;
    }

    public String getCountry() {
        return country;
    }

}
