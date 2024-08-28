package com.adobe.aem.guides.project2.core.Workflows;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import javax.jcr.Session;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Component(service = WorkflowProcess.class, immediate = true, property = {
        "process.label=Set Expiry Date Process",
        "service.vendor=AEM Geeks",
        "service.description=Custom workflow step for setting expiry date."
})
public class SetExpiryDateProcess implements WorkflowProcess {

    private static final Logger log = LoggerFactory.getLogger(SetExpiryDateProcess.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap)
            throws WorkflowException {
        Session jcrSession = null;
        ResourceResolver resolver = null;
        try {
            String path = workItem.getWorkflowData().getPayload().toString();
            jcrSession = workflowSession.adaptTo(Session.class);
            if (jcrSession == null) {
                log.error("Unable to adapt to JCR session.");
                return;
            }

            resolver = workflowSession.adaptTo(ResourceResolver.class);
            if (resolver == null) {
                log.error("Unable to adapt to ResourceResolver.");
                return;
            }

            Resource pageResource = resolver.getResource(path);
            if (pageResource != null) {
                String templatePath = pageResource.getValueMap().get("cq:template", String.class);
                Calendar expiryDate = Calendar.getInstance();

                if (templatePath != null && templatePath.startsWith("/conf/project2")) {
                    // Set expiry date to current date for project2 templates
                    expiryDate.add(Calendar.DAY_OF_MONTH, 0);
                    log.info("Setting expiry date to current date for template: {}", templatePath);
                } else {
                    // Set expiry date to previous date for other templates
                    expiryDate.add(Calendar.DAY_OF_MONTH, -1);
                    log.info("Setting expiry date to previous date for template: {}", templatePath);
                }

                Date expiryDateValue = expiryDate.getTime();
                String formattedDate = dateFormat.format(expiryDateValue);

                ModifiableValueMap properties = pageResource.adaptTo(ModifiableValueMap.class);
                if (properties != null) {
                    properties.put("expiryDate", formattedDate);
                    resolver.commit();
                    log.info("Expiry date set and changes committed for resource: {}", path);
                } else {
                    log.error("Unable to adapt to ModifiableValueMap for resource: {}", path);
                }
            } else {
                log.error("Resource not found at path: {}", path);
            }
        } catch (Exception e) {
            log.error("Error setting expiry date", e);
            throw new WorkflowException("Error setting expiry date", e);
        } finally {
            if (jcrSession != null) {
                jcrSession.logout();
            }
            if (resolver != null && resolver.isLive()) {
                resolver.close();
            }
        }
    }
}