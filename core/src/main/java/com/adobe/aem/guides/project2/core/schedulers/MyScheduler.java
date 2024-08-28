package com.adobe.aem.guides.project2.core.schedulers;

import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;

import com.adobe.aem.guides.project2.core.servlets.UpdatePagePropertiesServlet;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolverFactory;

@Component(service = Runnable.class, immediate = true, property = {
        "scheduler.expression=0 0/5 * * * ?",
        "scheduler.concurrent=false"
})
@ServiceDescription("A scheduler that triggers a servlet")
public class MyScheduler implements Runnable {

    @Reference
    private UpdatePagePropertiesServlet updatePagePropertiesServlet;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    public void run() {
        // Simulating a Servlet request and response for demonstration purposes
        SlingHttpServletRequest request = null;
        SlingHttpServletResponse response = null;

        try {
            updatePagePropertiesServlet.doGet(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}