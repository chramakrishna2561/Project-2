package com.adobe.aem.guides.project2.core.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Servlet;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
@Component(service = Servlet.class, immediate = true, property = {
        "sling.servlet.paths=/bin/example/jcrnodes"
})
public class Ajaxcall extends SlingAllMethodsServlet {

    private static final Logger log = LoggerFactory.getLogger(Ajaxcall.class);

    @Override
    protected void doGet(SlingHttpServletRequest req, SlingHttpServletResponse res) {
        try {
            ResourceResolver resolver = req.getResourceResolver();
            Resource usersNode = resolver.getResource("/content/capstone/us/en/jcr:content/users");

            if (usersNode != null) {
                JSONArray usersArray = new JSONArray();

                for (Resource user : usersNode.getChildren()) {
                    JSONObject userJson = new JSONObject();
                    userJson.put("id", user.getName()); // Use node name as ID
                    userJson.put("name", user.getValueMap().get("name", String.class));
                    userJson.put("email", user.getValueMap().get("email", String.class));
                    userJson.put("subject", user.getValueMap().get("subject", String.class));
                    userJson.put("message", user.getValueMap().get("message", String.class));
                    usersArray.put(userJson);
                }

                res.setContentType("application/json");
                res.getWriter().write(usersArray.toString());
            } else {
                res.getWriter().write("[]"); // Return empty array if no users node exists
            }
        } catch (JSONException e) {
            log.error("JSON Exception", e);
            res.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try {
                res.getWriter().write("Error processing JSON: " + e.getMessage());
            } catch (IOException ioException) {
                log.error("IO Exception", ioException);
            }
        } catch (IOException e) {
            log.error("IO Exception", e);
            res.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(SlingHttpServletRequest req, SlingHttpServletResponse res) {
        try {
            String name = req.getParameter("name");
            String email = req.getParameter("email");
            String subject = req.getParameter("subject");
            String message = req.getParameter("message");

            ResourceResolver resolver = req.getResourceResolver();
            Resource parentResource = resolver.getResource("/content/project2/us/en/jcr:content");

            if (parentResource != null) {
                Resource usersNode = parentResource.getChild("users");
                if (usersNode == null) {
                    usersNode = resolver.create(parentResource, "users", null);
                }

                Map<String, Object> map = new HashMap<>();
                map.put("name", name);
                map.put("email", email);
                map.put("subject", subject);
                map.put("message", message);

                String newNodeName = "user_" + System.currentTimeMillis();
                resolver.create(usersNode, newNodeName, map);
                resolver.commit();

                // Retrieve all user nodes and return as JSON
                JSONArray usersArray = new JSONArray();
                for (Resource user : usersNode.getChildren()) {
                    JSONObject userJson = new JSONObject();
                    userJson.put("id", user.getName()); // Use node name as ID
                    userJson.put("name", user.getValueMap().get("name", String.class));
                    userJson.put("email", user.getValueMap().get("email", String.class));
                    userJson.put("subject", user.getValueMap().get("subject", String.class));
                    userJson.put("message", user.getValueMap().get("message", String.class));
                    usersArray.put(userJson);
                }

                res.setContentType("application/json");
                res.getWriter().write(usersArray.toString());
            } else {
                res.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                res.getWriter().write("Parent resource does not exist");
            }
        } catch (PersistenceException e) {
            log.error("Persistence Exception", e);
            res.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try {
                res.getWriter().write("Error creating node: " + e.getMessage());
            } catch (IOException ioException) {
                log.error("IO Exception", ioException);
            }
        } catch (IOException e) {
            log.error("IO Exception", e);
            res.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (JSONException e) {
            log.error("JSON Exception", e);
            res.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try {
                res.getWriter().write("Error processing JSON: " + e.getMessage());
            } catch (IOException ioException) {
                log.error("IO Exception", ioException);
            }
        }
    }

    @Override
    protected void doPut(SlingHttpServletRequest req, SlingHttpServletResponse res) {
        try {
            String userId = req.getParameter("userId");
            String name = req.getParameter("name");
            String email = req.getParameter("email");
            String subject = req.getParameter("subject");
            String message = req.getParameter("message");

            ResourceResolver resolver = req.getResourceResolver();
            Resource userNode = resolver.getResource("/content/project2/us/en/jcr:content/users/" + userId);

            if (userNode != null) {
                ModifiableValueMap properties = userNode.adaptTo(ModifiableValueMap.class);
                if (properties != null) {
                    properties.put("name", name);
                    properties.put("email", email);
                    properties.put("subject", subject);
                    properties.put("message", message);

                    resolver.commit();

                    // Retrieve all user nodes and return as JSON
                    Resource usersNode = userNode.getParent();
                    JSONArray usersArray = new JSONArray();
                    for (Resource user : usersNode.getChildren()) {
                        JSONObject userJson = new JSONObject();
                        userJson.put("id", user.getName());
                        userJson.put("name", user.getValueMap().get("name", String.class));
                        userJson.put("email", user.getValueMap().get("email", String.class));
                        userJson.put("subject", user.getValueMap().get("subject", String.class));
                        userJson.put("message", user.getValueMap().get("message", String.class));
                        usersArray.put(userJson);
                    }

                    res.setContentType("application/json");
                    res.getWriter().write(usersArray.toString());
                } else {
                    res.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    res.getWriter().write("Unable to adapt resource to ModifiableValueMap");
                }
            } else {
                res.setStatus(SlingHttpServletResponse.SC_NOT_FOUND);
                res.getWriter().write("User node not found with ID: " + userId);
            }
        } catch (PersistenceException e) {
            log.error("Persistence Exception", e);
            res.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try {
                res.getWriter().write("Error updating node: " + e.getMessage());
            } catch (IOException ioException) {
                log.error("IO Exception", ioException);
            }
        } catch (IOException e) {
            log.error("IO Exception", e);
            res.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (JSONException e) {
            log.error("JSON Exception", e);
            res.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try {
                res.getWriter().write("Error processing JSON: " + e.getMessage());
            } catch (IOException ioException) {
                log.error("IO Exception", ioException);
            }
        }
    }

    @Override
    protected void doDelete(SlingHttpServletRequest req, SlingHttpServletResponse res) {
        try {
            String userId = req.getParameter("userId");

            ResourceResolver resolver = req.getResourceResolver();
            Resource userNode = resolver.getResource("/content/project2/us/en/jcr:content/users/" + userId);

            if (userNode != null) {
                resolver.delete(userNode);
                resolver.commit();

                // Retrieve all user nodes and return as JSON
                Resource usersNode = userNode.getParent();
                JSONArray usersArray = new JSONArray();
                for (Resource user : usersNode.getChildren()) {
                    JSONObject userJson = new JSONObject();
                    userJson.put("id", user.getName());
                    userJson.put("name", user.getValueMap().get("name", String.class));
                    userJson.put("email", user.getValueMap().get("email", String.class));
                    userJson.put("subject", user.getValueMap().get("subject", String.class));
                    userJson.put("message", user.getValueMap().get("message", String.class));
                    usersArray.put(userJson);
                }

                res.setContentType("application/json");
                res.getWriter().write(usersArray.toString());
            } else {
                res.setStatus(SlingHttpServletResponse.SC_NOT_FOUND);
                res.getWriter().write("User node not found with ID: " + userId);
            }
        } catch (PersistenceException e) {
            log.error("Persistence Exception", e);
            res.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try {
                res.getWriter().write("Error deleting node: " + e.getMessage());
            } catch (IOException ioException) {
                log.error("IO Exception", ioException);
            }
        } catch (IOException e) {
            log.error("IO Exception", e);
            res.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}