package com.adobe.aem.guides.project2.core.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.framework.Constants;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = Servlet.class, property = {
        Constants.SERVICE_DESCRIPTION + "=Path Validation Servlet",
        "sling.servlet.methods=" + "GET",
        "sling.servlet.paths=" + "/content/project2/us/en/wf"
})
public class ValidatePathServlet extends SlingAllMethodsServlet {

    private static final Logger log = LoggerFactory.getLogger(ValidatePathServlet.class);

    @Reference
    private ResourceResolverFactory resolverFactory;

    @Reference
    private ClientService clientService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        log.debug("Servlet called for path validation.");
        String pagePath = clientService.getPagePath();
        log.debug("Page Path from configuration: {}", pagePath);

        try (ResourceResolver resolver = resolverFactory.getServiceResourceResolver(
                Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, "krish"))) {

            Resource resource = resolver.getResource(pagePath);

            if (resource != null) {
                log.debug("Resource found: {}", resource.getPath());
                Resource contentResource = resource.getChild("jcr:content");

                if (contentResource != null) {
                    log.debug("jcr:content Resource found: {}", contentResource.getPath());

                    ModifiableValueMap properties = contentResource.adaptTo(ModifiableValueMap.class);
                    properties.put("clientId", clientService.getClientId());
                    properties.put("apiToken", clientService.getApiToken());

                    resolver.commit();
                    log.debug("Properties updated successfully.");
                    response.getWriter().write("Properties updated successfully.");
                } else {
                    log.debug("jcr:content Resource not found.");
                    response.getWriter().write("Invalid page path: jcr:content node not found.");
                }
            } else {
                log.debug("Resource not found.");
                response.getWriter().write("Invalid page path: Resource not found.");
            }
        } catch (Exception e) {
            log.error("Error updating properties", e);
            response.getWriter().write("Error: " + e.getMessage());
        }
    }

}
