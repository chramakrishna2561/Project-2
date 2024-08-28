package com.adobe.aem.guides.project2.core.models;

import java.util.List;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class Sidebarnavigationmulti {
    @ValueMapValue
    private String desktopicon;
    @ValueMapValue
    private String mobileicon;
    @ChildResource
    public List<Sidebarnestedmulti> nestedmultifield;

    public String getDesktopicon() {
        return desktopicon;
    }

    public String getMobileicon() {
        return mobileicon;
    }

    public List<Sidebarnestedmulti> getNestedmultifield() {
        return nestedmultifield;
    }

}
