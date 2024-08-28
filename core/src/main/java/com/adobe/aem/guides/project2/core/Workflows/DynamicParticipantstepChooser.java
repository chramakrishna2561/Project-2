package com.adobe.aem.guides.project2.core.Workflows;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.adobe.granite.workflow.model.WorkflowNode;
import com.adobe.granite.workflow.model.WorkflowTransition;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.adobe.granite.workflow.exec.ParticipantStepChooser;

import java.util.HashMap;
import java.util.Map;

@Component(service = ParticipantStepChooser.class, immediate = true, property = {
        "chooser.label=Custom Dynamic Participant Step",
        "service.vendor=AEM Geeks",
        "service.description=Dynamic participant chooser based on conditions"
})
public class DynamicParticipantstepChooser implements ParticipantStepChooser {

    private static final Logger log = LoggerFactory.getLogger(DynamicParticipantstepChooser.class);

    @Override
    public String getParticipant(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap)
            throws WorkflowException {
        String path = workItem.getWorkflowData().getPayload().toString();

        // Example condition based on the path or metadata
        String participant = "admin"; // Default participant

        if (path.startsWith("/content/project2")) {
            participant = "project2-owner";
        } else if (path.startsWith("/content/other-project")) {
            participant = "other-project-owner";
        }

        log.info("Dynamic participant chosen: {}", participant);
        return participant;
    }
}