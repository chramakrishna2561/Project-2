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

@Component(service = EventHandler.class, immediate = true, property = {
        EventConstants.EVENT_TOPIC + "=com/day/cq/replication"
})
public class Expirypagelistner implements EventHandler {

    private static final Logger LOG = LoggerFactory.getLogger(Expirypagelistner.class);

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    public void handleEvent(Event event) {
        String[] paths = (String[]) event.getProperty("paths");
        if (paths != null && paths.length > 0) {
            for (String path : paths) {
                logPublishedPagePath(path);
                updatePageProperty(path);
            }
        } else {
            LOG.warn("No page paths found in the event: {}", event);
        }
    }

    private void logPublishedPagePath(String pagePath) {
        LOG.info("Page published or unpublished: {}", pagePath);
    }

    private void updatePageProperty(String pagePath) {
        ResourceResolver resolver = null;
        try {
            resolver = getResourceResolver();
            if (resolver != null) {
                Resource pageResource = resolver.getResource(pagePath + "/jcr:content");
                if (pageResource != null) {
                    ModifiableValueMap properties = pageResource.adaptTo(ModifiableValueMap.class);
                    if (properties != null) {
                        properties.put("changed", true);
                        resolver.commit();
                        LOG.info("Property 'changed' set to true for page: {}", pagePath);
                    } else {
                        LOG.warn("Unable to adapt resource to ModifiableValueMap at path: {}", pagePath);
                    }
                } else {
                    LOG.warn("Resource not found at path: {}", pagePath);
                }
            }
        } catch (Exception e) {
            LOG.error("Error updating page property at path {}: {}", pagePath, e.getMessage(), e);
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
