package com.anosym.elexar.controller;

import com.anosym.elexar.ElectionTurnout;
import com.anosym.elexar.ElectionTurnoutReport;
import com.anosym.elexar.ElectiveRegion;
import com.anosym.elexar.PoliticalAgent;
import com.anosym.elexar.converter.NumberConverter;
import com.anosym.elexar.facade.ElectionTurnoutFacade;
import com.anosym.elexar.facade.ElectionTurnoutReportFacade;
import com.anosym.elexar.facade.ElectiveRegionFacade;
import com.anosym.elexar.facade.PoliticalAgentFacade;
import com.anosym.elexar.util.ElectiveRegionType;
import com.anosym.elexar.util.VotingTurnout;
import com.anosym.jflemax.validation.RequestStatus;
import com.anosym.jflemax.validation.annotation.LoginStatus;
import com.anosym.jflemax.validation.annotation.OnRequest;
import com.anosym.jflemax.validation.annotation.OnRequests;
import com.anosym.utilities.FormattedCalendar;
import com.anosym.utilities.Utility;
import com.anosym.vjax.VXMLBindingException;
import com.anosym.vjax.VXMLMemberNotFoundException;
import com.anosym.vjax.v3.VObjectMarshaller;
import com.anosym.vjax.xml.VDocument;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.ChartSeries;

/**
 *
 * @author marembo
 */
@Named(value = "electionTurnoutController")
@SessionScoped
@OnRequests(onRequests = {
    @OnRequest(logInStatus = LoginStatus.EITHER, requestStatus = RequestStatus.ANY_REQUEST,
               toPages = "*", onRequestMethod = "handleElectionTurnout")
})
public class ElectionTurnoutController implements Serializable {

    private static final String TURNOUT_PARAMETER = "turnout";
    private int SERIES_INTERVAL_MINUTES = 400;
    private Calendar start = new GregorianCalendar(2013, 2, 29, 18, 0, 0);
    @EJB
    private PoliticalAgentFacade politicalAgentFacade;
    @EJB
    private ElectiveRegionFacade electiveRegionFacade;
    @EJB
    private ElectionTurnoutFacade electionTurnoutFacade;
    @EJB
    private ElectionTurnoutReportFacade electionTurnoutReportFacade;
    @Inject
    private ElectiveRegionController electiveRegionController;
    private ElectionTurnout electionTurnout;
    private List<ElectionTurnoutReport> electionTurnoutReports;
    private CartesianChartModel turnoutModel;
    private ElectiveRegion electiveRegion;
    private ElectiveRegion countryElectiveRegion;
    private ElectiveRegion countyElectiveRegion;
    private ElectiveRegion constituencyRegion;
    private ElectiveRegion wardElectiveRegion;
    private ElectiveRegion pollingStationElectiveRegion;
    private PoliticalAgent pollingStationPoliticalAgent;

    /**
     * Creates a new instance of ElectionTurnoutController
     */
    public ElectionTurnoutController() {
    }

    @PostConstruct
    public void onStart() {
        electiveRegion = countryElectiveRegion = electiveRegionController.getElectiveRegion();
        turnoutModel = null;
    }

    public String prepareElectionTurnoutView() {
        onStart();
        return "/election-turnout.xhtml?faces-redirect=true";
    }

    public void handleElectionTurnout() {
        String turnout = ElexarController.getParameter(TURNOUT_PARAMETER);
        if (turnout != null) {
            System.err.println("Election Turnout: " + turnout);
            handleElectionTurnout(turnout);
        }
    }

    private void handleElectionTurnout(final String turnout) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!Utility.isNullOrEmpty(turnout)) {
                    try {
                        /*
                         * turnout = <VotingTurnout><votesCast>4545</votesCast><agent>0789456123</agent></VotingTurnout>
                         */
                        VObjectMarshaller<VotingTurnout> vom = new VObjectMarshaller<VotingTurnout>(VotingTurnout.class);
                        VDocument doc = VDocument.parseDocumentFromString(turnout);
                        VotingTurnout votingTurnout = vom.unmarshall(doc);
                        if (votingTurnout != null) {
                            //get the candidates.
                            PoliticalAgent agent = politicalAgentFacade.findAgentFromTelephoneNumber(votingTurnout.getAgent());
                            if (agent != null) {
                                electionTurnoutFacade.addElectionTurnoutData(agent, votingTurnout.getVotesCast());
                            }
                        }
                    } catch (VXMLMemberNotFoundException ex) {
                        Logger.getLogger(ElectionResultController.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (VXMLBindingException ex) {
                        Logger.getLogger(ElectionResultController.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (Exception ex) {
                        Logger.getLogger(ElectionResultController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }).start();
    }

    public void setStart(Calendar start) {
        this.start = start;
    }

    public Calendar getStart() {
        return start;
    }

    public void setSERIES_INTERVAL_MINUTES(int SERIES_INTERVAL_MINUTES) {
        this.SERIES_INTERVAL_MINUTES = SERIES_INTERVAL_MINUTES;
    }

    public int getSERIES_INTERVAL_MINUTES() {
        return SERIES_INTERVAL_MINUTES;
    }

    public void setElectionTurnout(ElectionTurnout electionTurnout) {
        this.electionTurnout = electionTurnout;
    }

    public ElectionTurnout getElectionTurnout() {
        return electionTurnout;
    }

    public void countryElectiveRegionSelected() {
        countyElectiveRegion = null;
        constituencyRegion = null;
        wardElectiveRegion = null;
        pollingStationElectiveRegion = null;
        electiveRegion = countryElectiveRegion != null ? countryElectiveRegion : electiveRegion;
        prepareTurnoutModel();
    }

    public void countyElectiveRegionSelected() {
        constituencyRegion = null;
        wardElectiveRegion = null;
        pollingStationElectiveRegion = null;
        electiveRegion = countyElectiveRegion != null ? countyElectiveRegion : countryElectiveRegion;
        prepareTurnoutModel();
    }

    public void constituencyElectiveRegionSelected() {
        wardElectiveRegion = null;
        pollingStationElectiveRegion = null;
        electiveRegion = constituencyRegion != null ? constituencyRegion : countyElectiveRegion;
        prepareTurnoutModel();
    }

    public void wardElectiveRegionSelected() {
        pollingStationElectiveRegion = null;
        electiveRegion = wardElectiveRegion != null ? wardElectiveRegion : constituencyRegion;
        prepareTurnoutModel();
    }

    public void pollingStationElectiveRegionSelected() {
        electiveRegion = pollingStationElectiveRegion != null ? pollingStationElectiveRegion : wardElectiveRegion;
        pollingStationPoliticalAgent = politicalAgentFacade.findPoliticalAgentForElectiveRegion(electiveRegion);
        prepareTurnoutModel();
    }

    public void setPollingStationElectiveRegion(ElectiveRegion pollingStationElectiveRegion) {
        this.pollingStationElectiveRegion = pollingStationElectiveRegion;
    }

    public ElectiveRegion getPollingStationElectiveRegion() {
        return pollingStationElectiveRegion;
    }

    public void setPollingStationPoliticalAgent(PoliticalAgent pollingStationPoliticalAgent) {
        this.pollingStationPoliticalAgent = pollingStationPoliticalAgent;
    }

    public PoliticalAgent getPollingStationPoliticalAgent() {
        return pollingStationPoliticalAgent;
    }

    public void setCountryElectiveRegion(ElectiveRegion countryElectiveRegion) {
        this.countryElectiveRegion = countryElectiveRegion;
    }

    public ElectiveRegion getCountryElectiveRegion() {
        return countryElectiveRegion;
    }

    public void setWardElectiveRegion(ElectiveRegion wardElectiveRegion) {
        this.wardElectiveRegion = wardElectiveRegion;
    }

    public ElectiveRegion getWardElectiveRegion() {
        return wardElectiveRegion;
    }

    public ElectiveRegion getConstituencyRegion() {
        return constituencyRegion;
    }

    public void setConstituencyRegion(ElectiveRegion constituencyRegion) {
        this.constituencyRegion = constituencyRegion;
    }

    public ElectiveRegion getCountyElectiveRegion() {
        return countyElectiveRegion;
    }

    public void setCountyElectiveRegion(ElectiveRegion countyElectiveRegion) {
        this.countyElectiveRegion = countyElectiveRegion;
    }

    public void electiveRegionSelected() {
        this.electiveRegion = electiveRegionController.getElectiveRegion();
        if (electiveRegion != null) {
            switch (electiveRegion.getElectiveRegionType()) {
                case CONSTITUENCY:
                    constituencyRegion = electiveRegion;
                    constituencyElectiveRegionSelected();
                    break;
                case COUNTRY:
                    countryElectiveRegion = electiveRegion;
                    countryElectiveRegionSelected();
                    break;
                case COUNTY:
                    countyElectiveRegion = electiveRegion;
                    countyElectiveRegionSelected();
                    break;
                case COUNTY_WARD:
                    wardElectiveRegion = electiveRegion;
                    wardElectiveRegionSelected();
                    break;
                case POLLING_STATION:
                    pollingStationElectiveRegion = electiveRegion;
                    pollingStationElectiveRegionSelected();
                    break;
            }
        }
    }

    public SelectItem[] getAllCountryElectiveRegions() {
        return JsfUtil.getSelectItems(electiveRegionFacade.getElectiveRegions(ElectiveRegionType.COUNTRY), true, "(Select Country)");
    }

    public SelectItem[] getAllCountyElectiveRegions() {
        return countryElectiveRegion != null
                ? JsfUtil.getSelectItems(electiveRegionFacade.getElectiveRegionsForElectiveRegion(countryElectiveRegion), true, "(Select County)") : null;
    }

    public SelectItem[] getAllConstituencyElectiveRegions() {
        return countyElectiveRegion != null
                ? JsfUtil
                .getSelectItems(electiveRegionFacade.getElectiveRegionsForElectiveRegion(countyElectiveRegion), true, "(Select Constituency)") : null;
    }

    public SelectItem[] getAllWardElectiveRegions() {
        return constituencyRegion != null
                ? JsfUtil.getSelectItems(electiveRegionFacade.getElectiveRegionsForElectiveRegion(constituencyRegion), true, "(Select Ward)") : null;
    }

    public SelectItem[] getAllPollingStationElectiveRegions() {
        return wardElectiveRegion != null
                ? JsfUtil.getSelectItems(electiveRegionFacade.getElectiveRegionsForElectiveRegion(wardElectiveRegion), true, "(Select Ward)") : null;
    }

    public void updateTurnoutModel() {
        if (electionTurnout != null) {
            electionTurnout = electionTurnoutFacade.find(electionTurnout.getTurnoutId());
            prepareTurnoutModel();
        }
    }

    public void prepareTurnoutModel() {
        electionTurnout = electionTurnoutFacade.getElectionTurnout(electiveRegion);
        electiveRegionController.setElectiveRegion(electiveRegion);
        if (electionTurnout == null || (electionTurnoutReports = electionTurnoutReportFacade.getElectionTurnoutReports(electionTurnout, start))
                .isEmpty()) {
            turnoutModel = null;
            electionTurnoutReports = null;
            return;
        }
        turnoutModel = new CartesianChartModel();
        ChartSeries series = new ChartSeries();
        series.setLabel("Election Turnout for " + electionTurnout.getElectiveRegion());
        //sort the turnout reports in ascending order
        Collections.sort(electionTurnoutReports, new Comparator<ElectionTurnoutReport>() {
            @Override
            public int compare(ElectionTurnoutReport o1, ElectionTurnoutReport o2) {
                return o1.getReportDate().compareTo(o2.getReportDate());
            }
        });
        Calendar startDate = (Calendar) start.clone();
        Calendar end = Calendar.getInstance();
        //get the data between start and end inclusive.
        series.set(FormattedCalendar.toISOTimeString(startDate), getTotalVotesCastBefore(startDate));
        System.err.println(FormattedCalendar.toISOTimeString(startDate));
        System.err.println(FormattedCalendar.toISOTimeString(end));
        while (startDate.before(end)) {
            startDate.set(Calendar.MINUTE, startDate.get(Calendar.MINUTE) + SERIES_INTERVAL_MINUTES);
            if (end.before(startDate)) {
                startDate = end;
            }
            System.err.println(FormattedCalendar.toISOTimeString(startDate));
            series.set(FormattedCalendar.toISOTimeString(startDate), getTotalVotesCastBefore(startDate));
        }
        turnoutModel.addSeries(series);
    }

    public long getMaximumVotesCast() {
        return electionTurnout.getElectiveRegion().getRegisteredVoters();
    }

    public String getElectiverRegionTitle() {
        if (electionTurnout == null || electionTurnout.getElectiveRegion() == null) {
            return null;
        }
        NumberConverter nc = new NumberConverter();
        long totalVotes = electionTurnoutReports != null ? ElectionTurnoutReport.getTotalVotesCast(electionTurnoutReports) : 0l;
        String title = electionTurnout.getElectiveRegion().getRegionName() + ": " + nc.getAsString(null, null, totalVotes);
        float percent = ((float) totalVotes * 100) / (electionTurnout.getElectiveRegion().getRegisteredVoters());
        title += "(" + BigDecimal.valueOf(percent).setScale(2, RoundingMode.UP) + "%)";
        return title;
    }

    public long getTotalVotesCastBefore(Calendar time) {
        long votes = 0l;
        if (electionTurnoutReports != null) {
            for (ElectionTurnoutReport e : electionTurnoutReports) {
                Calendar c = e.getReportDate();
                if (c.before(time)) {
                    votes += e.getVotesCast();
                }
            }
        }
        System.err.println("getTotalVotesCastBefore: " + votes);
        return votes;
    }

    public CartesianChartModel getTurnoutModel() {
        if (turnoutModel == null) {
            prepareTurnoutModel();
        }
        return turnoutModel;
    }
}
