package com.adobe.aem.guides.project2.core.servlets;

import java.io.IOException;
import java.util.Iterator;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = { Servlet.class }, property = {
        "sling.servlet.paths=/bin/checkUserAccess",
        "sling.servlet.methods=GET"
})
public class UserAccessServlet extends SlingAllMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(UserAccessServlet.class);

    @Reference
    private ResourceResolverFactory resolverFactory;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {

        ResourceResolver resolver = null;
        try {
            resolver = request.getResourceResolver();
            String userId = "ramakrishna"; // Hardcoded user ID

            UserManager userManager = resolver.adaptTo(UserManager.class);
            Authorizable user = userManager.getAuthorizable(userId);

            if (user != null) {
                Iterator<Group> groups = user.memberOf();
                boolean hasAccess = false;

                while (groups.hasNext()) {
                    Group group = groups.next();
                    if ("Access".equals(group.getID())) {
                        hasAccess = true;
                        break;
                    }
                }

                if (hasAccess) {
                    response.getWriter().write("The user " + userId + " has access.");
                } else {
                    response.getWriter().write("The user " + userId + " doesn't have access.");
                }
            } else {
                response.getWriter().write("User not found.");
            }

        } catch (Exception e) {
            LOG.error("Error in checking user access", e);
            response.getWriter().write("Error in checking user access");
        } finally {
            if (resolver != null && resolver.isLive()) {
                resolver.close();
            }
        }
    }
}
