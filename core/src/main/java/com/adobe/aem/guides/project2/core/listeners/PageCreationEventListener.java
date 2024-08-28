// package com.example;

// import org.apache.sling.api.resource.Resource;
// import org.apache.sling.api.resource.ResourceResolver;
// import org.apache.sling.api.resource.ResourceUtil;
// import org.apache.sling.event.jobs.Job;
// import org.apache.sling.event.jobs.JobManager;
// import org.apache.sling.event.jobs.JobManagerFactory;
// import org.apache.sling.event.jobs.consumer.JobConsumer;
// import org.apache.sling.event.jobs.consumer.JobConsumerContext;
// import org.apache.sling.event.jobs.consumer.JobConsumerFactory;
// import org.apache.sling.event.jobs.impl.JobConsumerContextImpl;
// import org.apache.sling.event.jobs.impl.JobConsumerFactoryImpl;
// import org.apache.sling.event.jobs.impl.JobManagerFactoryImpl;
// import org.apache.sling.event.jobs.impl.JobManagerImpl;
// import org.apache.sling.event.jobs.impl.JobProcessorFactoryImpl;
// import org.apache.sling.event.jobs.impl.JobProcessorImpl;
// import org.apache.sling.jcr.api.SlingRepository;
// import org.osgi.service.component.annotations.Component;
// import org.osgi.service.component.annotations.Reference;

// import javax.jcr.RepositoryException;
// import javax.jcr.Session;
// import javax.jcr.observation.Event;
// import javax.jcr.observation.EventIterator;
// import javax.jcr.observation.EventListener;
// import java.io.IOException;
// import java.util.HashMap;
// import java.util.Map;

// @Component(service = EventListener.class, immediate = true, property = {
// "eventTypes=PAGE_CREATED", "path=/content" })
// public class PageCreationEventListener implements EventListener {

// @Reference
// private SlingRepository repository;

// @Reference
// private JobManager jobManager;

// @Override
// public void onEvent(final EventIterator eventIterator) {
// while (eventIterator.hasNext()) {
// Event event = eventIterator.nextEvent();
// try {
// String path = event.getPath();
// // Start the workflow for the created page
// startWorkflow(path);
// } catch (RepositoryException e) {
// e.printStackTrace();
// }
// }
// }

// private void startWorkflow(String pagePath) {
// // Construct workflow payload
// Map<String, Object> workflowPayload = new HashMap<>();
// workflowPayload.put("pagePath", pagePath);
// // Start workflow job
// Job job = jobManager.createJob("com/example/workflow/expiryDateWorkflow",
// workflowPayload);
// jobManager.addJob(job);
// }
// }
