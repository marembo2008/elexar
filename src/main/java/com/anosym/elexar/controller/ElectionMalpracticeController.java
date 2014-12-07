package com.anosym.elexar.controller;

import com.anosym.elexar.ElectionMalpractice;
import com.anosym.elexar.PoliticalAgent;
import com.anosym.elexar.facade.ElectionMalpracticeFacade;
import com.anosym.elexar.facade.PoliticalAgentFacade;
import com.anosym.elexar.facade.PollingStationFacade;
import com.anosym.elexar.util.VotingMalpractice;
import com.anosym.jflemax.validation.RequestStatus;
import com.anosym.jflemax.validation.annotation.LoginStatus;
import com.anosym.jflemax.validation.annotation.OnRequest;
import com.anosym.jflemax.validation.annotation.OnRequests;
import com.anosym.vjax.VXMLMemberNotFoundException;
import com.anosym.vjax.v3.VObjectMarshaller;
import com.anosym.vjax.xml.VDocument;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author marembo
 */
@Named(value = "electionMalpracticeController")
@SessionScoped
@OnRequests(onRequests = {
    @OnRequest(logInStatus = LoginStatus.EITHER, requestStatus = RequestStatus.ANY_REQUEST,
               toPages = "*", onRequestMethod = "handleElectionMalpractice")
})
public class ElectionMalpracticeController implements Serializable {

    private static final String MALPRACTICE_PARAMETER = "malpractice";
    @EJB
    private PollingStationFacade pollingStationFacade;
    @EJB
    private PoliticalAgentFacade politicalAgentFacade;
    @EJB
    private ElectionMalpracticeFacade electionMalpracticeFacade;

    /**
     * Creates a new instance of ElectionMalpracticeController
     */
    public ElectionMalpracticeController() {
    }

    public void handleElectionMalpractice() {
        String malpractice = ElexarController.getParameter(MALPRACTICE_PARAMETER);
        System.err.println("handleElectionMalpractice: " + malpractice);
        if (malpractice != null) {
            try {
                VObjectMarshaller<VotingMalpractice> m = new VObjectMarshaller<VotingMalpractice>(VotingMalpractice.class);
                VotingMalpractice vm = m.unmarshall(VDocument.parseDocumentFromString(malpractice));
                System.out.println(vm);
                if (vm != null) {
                    //get the agent.
                    PoliticalAgent agent = politicalAgentFacade.findAgentFromTelephoneNumber(vm.getAgent());
                    if (agent != null && vm.getCode() != null && !electionMalpracticeFacade.isMalpracticeAdded(vm.getCode(), agent)) {
                        ElectionMalpractice em = new ElectionMalpractice(agent, vm.getCode());
                        electionMalpracticeFacade.create(em);
                    }
                }
            } catch (VXMLMemberNotFoundException ex) {
                Logger.getLogger(ElectionMalpracticeController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(ElectionMalpracticeController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public List<ElectionMalpractice> getElectionMalpractices() {
        return electionMalpracticeFacade.findAll();
    }
}
