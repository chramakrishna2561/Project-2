package com.adobe.aem.guides.project2.core.schedulers;

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
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

import java.util.Iterator;
import java.util.Map;
import javax.jcr.Session;
import java.util.HashMap;

@Component(service = Runnable.class, property = {
        "scheduler.concurrent=true"
}, immediate = true)
@Designate(ocd = PagePublishSchedulerConfig.class)
public class PagePublishScheduler implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(PagePublishScheduler.class);

    private String pagePath;
    private String schedulerExpression;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Reference
    private Replicator replicator;

    @Reference
    private Scheduler scheduler;

    @Activate
    @Modified
    protected void activate(final PagePublishSchedulerConfig config) {
        pagePath = config.pagePath();
        schedulerExpression = config.schedulerExpression();
        LOG.info("Scheduler activated/modified: pagePath = {}, cronExpression = {}", pagePath, schedulerExpression);

        ScheduleOptions scheduleOptions = scheduler.EXPR(schedulerExpression);
        scheduleOptions.name("Page Publish Scheduler - " + pagePath);
        scheduleOptions.canRunConcurrently(false);

        scheduler.schedule(this, scheduleOptions);
        LOG.info("Scheduler registered with cron expression: {}", schedulerExpression);
    }

    @Override
    public void run() {
        LOG.info("Scheduler triggered for path: {}", pagePath);

        Map<String, Object> param = new HashMap<>();
        param.put(ResourceResolverFactory.SUBSERVICE, "krish");

        try (ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(param)) {
            PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            if (pageManager != null) {
                Page page = pageManager.getPage(pagePath);
                if (page != null) {
                    Iterator<Page> pageIterator = page.listChildren();
                    while (pageIterator.hasNext()) {
                        Page childPage = pageIterator.next();
                        publishPage(childPage.getPath(), resourceResolver);
                    }
                } else {
                    LOG.warn("No page found at path: {}", pagePath);
                }
            } else {
                LOG.warn("PageManager could not be adapted from ResourceResolver.");
            }
        } catch (Exception e) {
            LOG.error("Error during page publishing: {}", e.getMessage(), e);
        }
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
}