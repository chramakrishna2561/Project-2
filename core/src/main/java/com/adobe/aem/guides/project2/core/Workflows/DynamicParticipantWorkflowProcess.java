package com.adobe.aem.guides.project2.core.Workflows;

import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.Session;

@Component(service = WorkflowProcess.class, immediate = true, property = {
        "process.label=Dynamic Participant Workflow Process",
        "service.vendor=AEM Geeks",
        "service.description=Custom workflow step for dynamic participant assignment."
})
public class DynamicParticipantWorkflowProcess implements WorkflowProcess {
    private static final Logger log = LoggerFactory.getLogger(DynamicParticipantWorkflowProcess.class);

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap processArguments) {
        ResourceResolver resourceResolver = null;
        Session jcrSession = null;
        try {
            WorkflowData workflowData = workItem.getWorkflowData();
            if ("JCR_PATH".equals(workflowData.getPayloadType())) {
                resourceResolver = getResourceResolver();
                jcrSession = resourceResolver.adaptTo(Session.class);

                String path = workflowData.getPayload().toString() + "/jcr:content";
                @SuppressWarnings("unused")
                Node node = (Node) jcrSession.getItem(path);

                String reviewer = (String) workItem.getWorkflowData().getMetaDataMap().get("reviewer");
                if (reviewer != null && !reviewer.isEmpty()) {
                    workItem.getMetaDataMap().put("ASSIGNED_USER", reviewer);
                } else {
                    workItem.getMetaDataMap().put("ASSIGNED_USER", "defaultUser");
                }
            } else {
                log.error("Invalid payload type: {}", workflowData.getPayloadType());
            }
        } catch (Exception e) {
            log.error("Error executing workflow step", e);
        } finally {
            if (resourceResolver != null && resourceResolver.isLive()) {
                resourceResolver.close();
            }
        }
    }

    private ResourceResolver getResourceResolver() {
        ResourceResolver resourceResolver = null;
        try {
            resourceResolver = resourceResolverFactory.getServiceResourceResolver(null);
        } catch (Exception e) {
            log.error("Error obtaining resource resolver", e);
        }
        return resourceResolver;
    }
}
