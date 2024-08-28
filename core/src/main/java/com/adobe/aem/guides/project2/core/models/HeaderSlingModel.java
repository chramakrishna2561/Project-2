package com.adobe.aem.guides.project2.core.models;

import java.util.List;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class HeaderSlingModel {
    @ValueMapValue
    private String pathField;
    @ValueMapValue
    private String textField;
    @ValueMapValue
    private boolean checkbox;
    @ChildResource
    private List<Headerchild> multifield;

    public List<Headerchild> getMultifield() {
        return multifield;
    }

    public String getPathField() {
        return pathField;
    }

    public String getTextField() {
        return textField;
    }

    public boolean getCheckbox() {
        return checkbox;
    }

}