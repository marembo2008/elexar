package com.anosym.elexar.controller;

import com.anosym.jflemax.validation.controller.JFlemaxController;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

import static com.anosym.jflemax.validation.controller.JFlemaxController.getRequestPath;
import static com.anosym.jflemax.validation.controller.JFlemaxController.logError;

/**
 *
 * @author marembo
 */
@Named(value = "elexarController")
@ApplicationScoped
@Singleton
@Startup
public class ElexarController extends JFlemaxController {

    /**
     * Creates a new instance of ElexarController
     */
    public ElexarController() {
    }

    public void preRenderView() {
        try {
            getSessionId(true); //initializes session if no session already exists
        } catch (Exception e) {
            logError(e);
        }
    }

    public String getSessionId(boolean newSession) {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(newSession);
        return session == null ? null : session.getId();
    }

    public String getCurrentMenuBackground(String page) {
        String currentPage = getRequestPath();
        System.out.println("currentPage: " + currentPage);
        if (currentPage.equals(page)) {
            return "#777777 !important";
        }
        return "no-css";
    }
}
