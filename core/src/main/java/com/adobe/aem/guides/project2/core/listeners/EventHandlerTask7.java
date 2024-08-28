package com.adobe.aem.guides.project2.core.listeners;

import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.PersistenceException;

@Component(service = EventHandler.class, immediate = true, property = {
        EventConstants.EVENT_TOPIC + "=com/day/cq/replication"
})
public class EventHandlerTask7 implements EventHandler {

    private static final Logger LOG = LoggerFactory.getLogger(EventHandlerTask7.class);

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    public void handleEvent(Event event) {
        String[] paths = (String[]) event.getProperty("paths");
        LOG.info("Handling event for paths: {}", (Object[]) paths);
        if (paths != null && paths.length > 0) {
            for (String path : paths) {
                logPublishedPagePath(path);
                updatePageProperty(path);
            }
        } else {
            LOG.warn("Page paths not found in event: {}", event);
        }
    }

    private void logPublishedPagePath(String pagePath) {
        LOG.info("Page published: {}", pagePath);
    }

    private void updatePageProperty(String pagePath) {
        ResourceResolver resolver = null;
        try {
            resolver = getResourceResolver();
            if (resolver != null) {
                LOG.info("ResourceResolver obtained successfully");
                Resource pageResource = resolver.getResource(pagePath + "/jcr:content");
                if (pageResource != null) {
                    LOG.info("Resource found for path: {}", pagePath);
                    ModifiableValueMap properties = pageResource.adaptTo(ModifiableValueMap.class);
                    if (properties != null) {
                        properties.put("changed", true);
                        resolver.commit();
                        LOG.info("Property 'changed' set to true for page: {}", pagePath);
                    } else {
                        LOG.warn("Failed to adapt resource to ModifiableValueMap for path: {}", pagePath);
                    }
                } else {
                    LOG.warn("Resource not found for path: {}", pagePath);
                }
            }
        } catch (PersistenceException e) {
            LOG.error("Error updating page property for path {}: {}", pagePath, e.getMessage(), e);
        } finally {
            if (resolver != null && resolver.isLive()) {
                resolver.close();
            }
        }
    }

    private ResourceResolver getResourceResolver() {
        ResourceResolver resolver = null;
        try {
            Map<String, Object> param = new HashMap<>();
            param.put(ResourceResolverFactory.SUBSERVICE, "krish");
            resolver = resourceResolverFactory.getServiceResourceResolver(param);
            LOG.info("ResourceResolver obtained for service user 'krish'");
        } catch (Exception e) {
            LOG.error("Failed to obtain ResourceResolver", e);
        }
        return resolver;
    }
}