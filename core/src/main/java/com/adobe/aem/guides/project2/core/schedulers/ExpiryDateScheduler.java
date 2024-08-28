package com.adobe.aem.guides.project2.core.schedulers;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.replication.Replicator;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.ReplicationActionType;

import javax.jcr.Session;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component(service = Runnable.class, property = {
        "scheduler.concurrent=true"
}, immediate = true)
@Designate(ocd = ExpiryDateSchedulerConfig.class)
public class ExpiryDateScheduler implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(ExpiryDateScheduler.class);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    private String rootPath;
    private String schedulerExpression;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Reference
    private Replicator replicator;

    @Reference
    private Scheduler scheduler;

    @Activate
    @Modified
    protected void activate(final ExpiryDateSchedulerConfig config) {
        rootPath = config.rootPath();
        schedulerExpression = config.schedulerExpression();
        LOG.info("Scheduler activated/modified: rootPath = {}, cronExpression = {}", rootPath, schedulerExpression);

        ScheduleOptions scheduleOptions = scheduler.EXPR(schedulerExpression);
        scheduleOptions.name("Expiry Date Scheduler - " + rootPath);
        scheduleOptions.canRunConcurrently(false);

        scheduler.schedule(this, scheduleOptions);
        LOG.info("Scheduler registered with cron expression: {}", schedulerExpression);
    }

    @Override
    public void run() {
        LOG.info("Scheduler triggered for root path: {}", rootPath);

        Map<String, Object> param = new HashMap<>();
        param.put(ResourceResolverFactory.SUBSERVICE, "krish");

        try (ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(param)) {
            Resource rootResource = resourceResolver.getResource(rootPath);
            if (rootResource != null) {
                processPages(rootResource, resourceResolver);
            } else {
                LOG.warn("Root path resource not found: {}", rootPath);
            }
        } catch (Exception e) {
            LOG.error("Error during page processing: {}", e.getMessage(), e);
        }
    }

    private void processPages(Resource resource, ResourceResolver resourceResolver) {
        Calendar now = Calendar.getInstance();
        Calendar previousDay = (Calendar) now.clone();
        previousDay.add(Calendar.DAY_OF_MONTH, -1);

        for (Resource child : resource.getChildren()) {
            if (child.getName().equals("jcr:content")) {
                continue;
            }

            Resource jcrContentResource = child.getChild("jcr:content");
            if (jcrContentResource != null) {
                LOG.info("Found jcr:content node for page: {}", child.getPath());
                if (jcrContentResource.getValueMap().containsKey("expiryDate")) {
                    String expiryDateStr = jcrContentResource.getValueMap().get("expiryDate", String.class);
                    if (expiryDateStr != null) {
                        try {
                            Date expiryDate = DATE_FORMAT.parse(expiryDateStr);
                            Calendar expiryCalendar = Calendar.getInstance();
                            expiryCalendar.setTime(expiryDate);

                            LOG.info("Found expiryDate for page: {} - Expiry Date: {}", child.getPath(), expiryDate);
                            if (isSameDay(expiryCalendar, now)) {
                                publishPage(child.getPath(), resourceResolver);
                            } else if (isSameDay(expiryCalendar, previousDay)) {
                                unpublishPage(child.getPath(), resourceResolver);
                            }
                        } catch (ParseException e) {
                            LOG.error("Failed to parse expiryDate for page: {} - Error: {}", child.getPath(),
                                    e.getMessage(), e);
                        }
                    } else {
                        LOG.warn("ExpiryDate is null for page: {}", child.getPath());
                    }
                } else {
                    LOG.warn("No expiryDate property found for page: {}", child.getPath());
                }
            } else {
                LOG.warn("No jcr:content node found for page: {}", child.getPath());
            }

            // Recursively process child pages
            processPages(child, resourceResolver);
        }
    }

    private boolean isSameDay(Calendar date1, Calendar date2) {
        return date1.get(Calendar.YEAR) == date2.get(Calendar.YEAR) &&
                date1.get(Calendar.DAY_OF_YEAR) == date2.get(Calendar.DAY_OF_YEAR);
    }

    private void publishPage(String path, ResourceResolver resourceResolver) {
        try {
            Session session = resourceResolver.adaptTo(Session.class);
            if (session != null) {
                replicator.replicate(session, ReplicationActionType.ACTIVATE, path);
                LOG.info("Page published: {}", path);
            } else {
                LOG.error("Session could not be adapted from ResourceResolver.");
            }
        } catch (ReplicationException e) {
            LOG.error("Failed to publish page: {}", path, e);
        }
    }

    private void unpublishPage(String path, ResourceResolver resourceResolver) {
        try {
            Session session = resourceResolver.adaptTo(Session.class);
            if (session != null) {
                replicator.replicate(session, ReplicationActionType.DEACTIVATE, path);
                LOG.info("Page unpublished: {}", path);
            } else {
                LOG.error("Session could not be adapted from ResourceResolver.");
            }
        } catch (ReplicationException e) {
            LOG.error("Failed to unpublish page: {}", path, e);
        }
    }
}