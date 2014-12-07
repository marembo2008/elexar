package com.anosym.elexar.controller;

import com.anosym.elexar.Candidate;
import com.anosym.elexar.CandidateElectionResult;
import com.anosym.elexar.ElectionResult;
import com.anosym.elexar.ElectiveRegion;
import com.anosym.elexar.ManualEntryElectionResult;
import com.anosym.elexar.PoliticalAgent;
import com.anosym.elexar.converter.NumberConverter;
import com.anosym.elexar.facade.CandidateFacade;
import com.anosym.elexar.facade.ElectionResultFacade;
import com.anosym.elexar.facade.ElectiveRegionFacade;
import com.anosym.elexar.facade.PoliticalAgentFacade;
import com.anosym.elexar.facade.PollingStationFacade;
import com.anosym.elexar.util.ElectiveRegionType;
import com.anosym.elexar.util.ElectiveSeat;
import com.anosym.elexar.util.VotingResult;
import com.anosym.jflemax.validation.RequestStatus;
import com.anosym.jflemax.validation.annotation.LoginStatus;
import com.anosym.jflemax.validation.annotation.OnRequest;
import com.anosym.jflemax.validation.annotation.OnRequests;
import com.anosym.utilities.Utility;
import com.anosym.vjax.VXMLBindingException;
import com.anosym.vjax.VXMLMemberNotFoundException;
import com.anosym.vjax.v3.VObjectMarshaller;
import com.anosym.vjax.xml.VDocument;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.component.tabview.Tab;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.ChartSeries;

/**
 *
 * @author marembo
 */
@Named(value = "electionResultController")
@SessionScoped
@OnRequests(onRequests = {
    @OnRequest(logInStatus = LoginStatus.EITHER, requestStatus = RequestStatus.ANY_REQUEST,
               toPages = "*", onRequestMethod = "handleElectionResult")
})
public class ElectionResultController implements Serializable {

    private class ElectionResultLazyDataModel extends LazyDataModel<ElectionResult> {

        public ElectionResultLazyDataModel() {
            setRowCount(electionResultFacade.countElectionResultsForElectiveRegionTypes(electiveRegionType));
        }

        @Override
        public List<ElectionResult> load(int first, int pageSize, List<SortMeta> multiSortMeta, Map<String, String> filters) {
            return load(first, pageSize);
        }

        @Override
        public List<ElectionResult> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
            return load(first, pageSize);
        }

        private List<ElectionResult> load(int first, int pageSize) {
            return electionResultFacade.findElectionResultsForElectiveRegionTypes(electiveRegionType, first, pageSize);
        }

        @Override
        public int getRowCount() {
            return electionResultFacade.countElectionResultsForElectiveRegionTypes(electiveRegionType);
        }
    }
    private static final String RESULT_PARAMETER = "result";
    static final Map<String, Integer> tabIds = new HashMap<String, Integer>();

    static {
        tabIds.put("electionResult", 0);
        tabIds.put("electionTurnout", 1);
        tabIds.put("electionMalpractice", 2);
        tabIds.put("electionResultManualEntry", 3);
        tabIds.put("electionConfigurations", 4);
    }
    @Inject
    private ElexarController elexarController;
    @Inject
    private CandidateController candidateController;
    @Inject
    private ElectiveRegionController electiveRegionController;
    @EJB
    private PoliticalAgentFacade politicalAgentFacade;
    @EJB
    private CandidateFacade candidateFacade;
    @EJB
    private PollingStationFacade pollingStationFacade;
    @EJB
    private ElectionResultFacade electionResultFacade;
    @EJB
    private ElectiveRegionFacade electiveRegionFacade;
    private ElectiveRegion electiveRegion;
    private int currentTabIndex = 0;
    private CartesianChartModel resultModel;
    private List<ElectionResult> electionResults;
    private ElectiveRegion countryElectiveRegion;
    private ElectiveRegion countyElectiveRegion;
    private ElectiveRegion constituencyElectiveRegion;
    private ElectiveRegion wardElectiveRegion;
    private ElectiveRegion pollingStationElectiveRegion;
    private String seriesColors = "ff0000,00ff00,0000ff";
    /**
     * We use this data for reporting election result locally.
     */
    private ManualEntryElectionResult electionResult;
    private ElectionResult presidentialElectionResult;
    private String electiveRegionName;
    private int votesDisputed;
    private int votesRejected;
    private ElectiveRegionType electiveRegionType;
    private DataModel<ElectionResult> electionResultModel;

    /**
     * Creates a new instance of ElectionResultController
     */
    public ElectionResultController() {
    }

    @PostConstruct
    public void loadCountry() {
        electiveRegion = countryElectiveRegion = electiveRegionController.getElectiveRegion();
        electiveRegionType = ElectiveRegionType.COUNTRY;
        electionResultModel = new ElectionResultLazyDataModel();
        prepareResultModel();
    }

    public void setElectiveRegionType(ElectiveRegionType electiveRegionType) {
        this.electiveRegionType = electiveRegionType;
    }

    public ElectiveRegionType getElectiveRegionType() {
        return electiveRegionType;
    }

    public ElectiveRegionType[] getElectiveRegionTypes() {
        return new ElectiveRegionType[]{ElectiveRegionType.COUNTRY, ElectiveRegionType.COUNTY, ElectiveRegionType.CONSTITUENCY};
    }

    public String prepareCandidateElectionResultsForElectiveRegionType() {
        if (electiveRegionType == null) {
            electiveRegionType = ElectiveRegionType.CONSTITUENCY;
        }
        List<ElectiveRegion> electiveRegions = electiveRegionFacade.getElectiveRegions(electiveRegionType);
        electionResults = new ArrayList<ElectionResult>();
        for (ElectiveRegion er : electiveRegions) {
            ElectionResult r = electionResultFacade.getPresidentialElectionResults(er);
            if (r != null) {
                electionResults.add(r);
            }
        }
        return "/election-result-dashboard.xhtml?faces-redirect=true";
    }

    public String prepareElectionResultView() {
        loadCountry();
        electiveRegionController.setElectiveRegion(electiveRegion);
        return "/election-result.xhtml?faces-redirect=true";
    }

    public void handleElectionResult() {
        String result = ElexarController.getParameter(RESULT_PARAMETER);
        System.err.println("handleElectionResult: " + result);
        handleElectionResult(result);
    }

    public String prepareAddElectionResults() {
        electionResult = new ManualEntryElectionResult();
        for (Candidate c : candidateController.getPresidentialCandidates()) {
            electionResult.addCandidateElectionResults(new CandidateElectionResult(c, 0));
        }
        return "/election-result-form.xhtml?faces-redirect=true";
    }

    public void setVotesRejected(int votesRejected) {
        this.votesRejected = votesRejected;
    }

    public int getVotesRejected() {
        return votesRejected;
    }

    public void setVotesDisputed(int votesDisputed) {
        this.votesDisputed = votesDisputed;
    }

    public int getVotesDisputed() {
        return votesDisputed;
    }

    private void handleElectionResult(final String result) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!Utility.isNullOrEmpty(result)) {
                    try {
                        /*
                         * result =
                         * <VotingResult><votes><R>4566</R><U>566</U><P>78</P></votes><disputed>63</disputed><rejected>6</rejected><agent>0788456123</agent></VotingResult>;
                         */
                        VObjectMarshaller<VotingResult> vom = new VObjectMarshaller<VotingResult>(VotingResult.class);
                        VDocument doc = VDocument.parseDocumentFromString(result);
                        VotingResult votingResult = vom.unmarshall(doc);
                        if (votingResult != null && votingResult.getResult() != null) {
                            //get the candidates.
                            PoliticalAgent agent = politicalAgentFacade.findAgentFromTelephoneNumber(votingResult.getAgent());
                            if (agent != null) {
                                Map<Candidate, Integer> cns = new HashMap<Candidate, Integer>();
                                for (String code : votingResult.getResult().keySet()) {
                                    Candidate candidate = candidateFacade.findCandidateForCode(code);
                                    if (candidate != null) {
                                        cns.put(candidate, votingResult.getResult().get(code));
                                    }
                                }
                                if (!cns.isEmpty()) {
                                    electionResultFacade.addProvisionalElectionResult(agent.getElectiveRegion(), cns, votingResult.getDisputed(),
                                                                                      votingResult.getRejected());
                                }
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

//  public void validateVotesGained(FacesContext context, UIComponent component, Object obj) {
//    if (electionResult == null || electionResult.getElectiveRegion() == null) {
//      return;
//    }
//    long totalVotesCast = electionResultFacade.findTotalVotesGained(electionResult.getElectiveRegion());
//    long registeredVoters = electionResult.getElectiveRegion().getRegisteredVoters();
//    int enteredVotesGained = (obj instanceof Integer) ? (Integer) obj : 0;
//    if (enteredVotesGained >= (totalVotesCast + registeredVoters)) {
//      JsfUtil.addErrorMessage("Invalid Votes gained! " + enteredVotesGained);
//      ((UIInput) component).setValid(false);
//    } else {
//      ((UIInput) component).setValid(true);
//    }
//  }
    public void setElectiveRegionName(String electiveRegionName) {
        this.electiveRegionName = electiveRegionName;
    }

    public String getElectiveRegionName() {
        return electiveRegionName;
    }

    public List<ElectiveRegion> findElectiveRegions() {
        if (!Utility.isNullOrEmpty(electiveRegionName)) {
            return electiveRegionController.findElectiveRegion(electiveRegionName);
        }
        return new ArrayList<ElectiveRegion>();
    }

    public void setElectionResult(ManualEntryElectionResult electionResult) {
        this.electionResult = electionResult;
    }

    public ManualEntryElectionResult getElectionResult() {
        if (electionResult == null) {
            electionResult = new ManualEntryElectionResult();
            for (Candidate c : candidateFacade.findCandidatesForElectiveSeats(ElectiveSeat.PRESIDENTAIL)) {
                electionResult.addCandidateElectionResults(new CandidateElectionResult(c, 0));
            }
        }
        return electionResult;
    }

    public void addElectionResult() {
        try {
            electionResultFacade.create(electionResult);
            JsfUtil.addSuccessMessage("Successfully Added Result");
        } catch (Exception e) {
            ElexarController.logError(e);
            JsfUtil.addErrorMessage("Error Adding Result Manually");
        } finally {
            prepareAddElectionResults();
        }
    }

    public void resetResult() {
        countryElectiveRegionSelected();
    }

    public void tabChanged(TabChangeEvent event) {
        Tab t = event.getTab();
        String id = t.getId();
        System.out.println("id: " + id);
        Integer i = tabIds.get(id);
        currentTabIndex = i != null ? i : 0;
    }

    public void setCurrentTabIndex(int currentTabIndex) {
    }

    public int getCurrentTabIndex() {
        return currentTabIndex;
    }

    public void tabChanged(AjaxBehaviorEvent event) {
    }

    public boolean showPresidentialResults() {
        return true; //electiveRegion != null;
    }

    public boolean showSenatorialResults() {
        return true; //electiveRegion != null && electiveRegion.getElectiveRegionType().ordinal() > ElectiveRegionType.COUNTRY.ordinal();
    }

    public boolean showGubernatorialResults() {
        return true; //electiveRegion != null && electiveRegion.getElectiveRegionType().ordinal() > ElectiveRegionType.COUNTRY.ordinal();
    }

    public boolean showWomenRepresentativeResults() {
        return true; //electiveRegion != null && electiveRegion.getElectiveRegionType().ordinal() > ElectiveRegionType.COUNTRY.ordinal();
    }

    public boolean showMpResults() {
        return true; //electiveRegion != null && electiveRegion.getElectiveRegionType().ordinal() > ElectiveRegionType.COUNTY.ordinal();
    }

    public boolean showCountyWardResults() {
        return true; //electiveRegion != null && electiveRegion.getElectiveRegionType().ordinal() > ElectiveRegionType.CONSTITUENCY.ordinal();
    }

    public List<ElectionResult> getElectionResults(ElectiveSeat electiveSeat) {
        return new ArrayList<ElectionResult>(Arrays.asList(electionResultFacade.getElectionResults(electiveRegion, electiveSeat)));
    }

    public ElectiveRegion getElectiveRegion() {
        return electiveRegion;
    }

    public List<ElectionResult> getPresidentialResults() {
        return getElectionResults(ElectiveSeat.PRESIDENTAIL);
    }

    public List<ElectionResult> getSenatorialResults() {
        return getElectionResults(ElectiveSeat.SENATORIAL);
    }

    public List<ElectionResult> getGubernatorialResults() {
        return getElectionResults(ElectiveSeat.GUBERNATORIAL);
    }

    public List<ElectionResult> getWomenRepresentativeResults() {
        return getElectionResults(ElectiveSeat.WOMEN_REPRESENTATIVE);
    }

    public List<ElectionResult> getMemberOfNationalAssemblyResults() {
        return getElectionResults(ElectiveSeat.MP);
    }

    public List<ElectionResult> getCountyWardRepresentativeResults() {
        return getElectionResults(ElectiveSeat.COUNTY_REPRESENTATIVE);
    }

    public void electiveRegionSelected() {
        this.electiveRegion = electiveRegionController.getElectiveRegion();
        if (electiveRegion != null) {
            switch (electiveRegion.getElectiveRegionType()) {
                case CONSTITUENCY:
                    constituencyElectiveRegion = electiveRegion;
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

    public void countryElectiveRegionSelected() {
        countyElectiveRegion = null;
        constituencyElectiveRegion = null;
        wardElectiveRegion = null;
        pollingStationElectiveRegion = null;
        if (countryElectiveRegion != null) {
            electiveRegion = countryElectiveRegion;
            prepareResultModel();
        }
    }

    public void countyElectiveRegionSelected() {
        constituencyElectiveRegion = null;
        wardElectiveRegion = null;
        pollingStationElectiveRegion = null;
        if (countyElectiveRegion != null) {
            electiveRegion = countyElectiveRegion;
            prepareResultModel();
        }
    }

    public void constituencyElectiveRegionSelected() {
        wardElectiveRegion = null;
        pollingStationElectiveRegion = null;
        if (constituencyElectiveRegion != null) {
            electiveRegion = constituencyElectiveRegion;
            prepareResultModel();
        }
    }

    public void wardElectiveRegionSelected() {
        pollingStationElectiveRegion = null;
        if (wardElectiveRegion != null) {
            electiveRegion = wardElectiveRegion;
            prepareResultModel();
        }
    }

    public void pollingStationElectiveRegionSelected() {
        if (pollingStationElectiveRegion != null) {
            electiveRegion = pollingStationElectiveRegion;
            prepareResultModel();
        }
    }

    public ElectiveRegion getPollingStationElectiveRegion() {
        return pollingStationElectiveRegion;
    }

    public void setPollingStationElectiveRegion(ElectiveRegion pollingStationElectiveRegion) {
        this.pollingStationElectiveRegion = pollingStationElectiveRegion;
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

    public ElectiveRegion getConstituencyElectiveRegion() {
        return constituencyElectiveRegion;
    }

    public void setConstituencyElectiveRegion(ElectiveRegion constituencyRegion) {
        this.constituencyElectiveRegion = constituencyRegion;
    }

    public ElectiveRegion getCountyElectiveRegion() {
        return countyElectiveRegion;
    }

    public void setCountyElectiveRegion(ElectiveRegion countyElectiveRegion) {
        System.out.println("setCountyElectiveRegion: " + countyElectiveRegion);
        this.countyElectiveRegion = countyElectiveRegion;
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
        return constituencyElectiveRegion != null
                ? JsfUtil.getSelectItems(electiveRegionFacade.getElectiveRegionsForElectiveRegion(constituencyElectiveRegion), true, "(Select Ward)") : null;
    }

    public SelectItem[] getAllPollingStationElectiveRegions() {
        return wardElectiveRegion != null
                ? JsfUtil.getSelectItems(electiveRegionFacade.getElectiveRegionsForElectiveRegion(wardElectiveRegion), true,
                                         "(Select Polling Station)") : null;
    }

    public void updateElectionResult() {
        prepareResultModel();
        electiveRegionController.prepareJavascriptRegionBounds();
    }

    public void prepareResultModel() {
        electiveRegionController.setElectiveRegion(electiveRegion);
        presidentialElectionResult = electionResultFacade.getPresidentialElectionResults(electiveRegion);
        if (presidentialElectionResult == null) {
            resultModel = null;
            return;
        }
        resultModel = new CartesianChartModel();
        seriesColors = "";
        NumberConverter nc = new NumberConverter();
        for (CandidateElectionResult result : presidentialElectionResult.getCandidateElectionResults()) {
            if (!seriesColors.isEmpty()) {
                seriesColors += ",";
            }
            seriesColors += result.getCandidate().getPartyColor();
            ChartSeries series = new ChartSeries();
            series.setLabel(result.getCandidate() + "(" + nc.getAsString(null, null, result.getVotesGained()) + ")");
            series.set(presidentialElectionResult.getElectiveRegion().getRegionName(), result.getVotesGained());
            resultModel.addSeries(series);
        }
    }

    public int getPercentageOfVotesGained(CandidateElectionResult electionResult) {
        long totalVotesCast = getTotalVotesCast();
        if (totalVotesCast == 0l) {
            return 0;
        }
        if (presidentialElectionResult == null) {
            return 0;
        }
        long votes = presidentialElectionResult.totalVotesGained();
        long percent = (votes * 100) / totalVotesCast;
        System.err.println("Percent for: " + electionResult.getCandidate() + "= " + percent);
        return (int) percent;
    }

    public int getPercentageOfVotesGained(ElectionResult presidentialElectionResult, CandidateElectionResult electionResult) {
        if (presidentialElectionResult == null) {
            return 0;
        }
        long totalVotesCast = getTotalVotesCast(presidentialElectionResult);
        if (totalVotesCast == 0l) {
            return 0;
        }
        long votes = presidentialElectionResult.totalVotesGained();
        long percent = (votes * 100) / totalVotesCast;
        System.err.println("Percent for: " + electionResult.getCandidate() + "= " + percent);
        return (int) percent;
    }

    public ElectionResult getPresidentialElectionResult() {
        return presidentialElectionResult;
    }

    public List<CandidateElectionResult> getCandidateElectionResults() {
        List<CandidateElectionResult> results = presidentialElectionResult != null ? presidentialElectionResult.getCandidateElectionResults() : null;
        if (results != null) {
            Collections.sort(results, ElectionResult.resultComparator);
        }
        return results;
    }

    public String getPercentageOfVotesGainedStr(CandidateElectionResult result) {
        long totalVotesCast = getTotalVotesCast();
        if (totalVotesCast == 0l) {
            return "0%";
        }
        BigDecimal votes = new BigDecimal(result.getVotesGained() * 100);
        BigDecimal percent = votes.divide(new BigDecimal(totalVotesCast), 2, RoundingMode.UP);
        System.err.println("Percent for: " + result.getCandidate() + "= " + percent);
        return percent + "%";
    }

    public String getPercentageOfVotesGainedStr(ElectionResult electionResult, CandidateElectionResult result) {
        long totalVotesCast = getTotalVotesCast(electionResult);
        if (totalVotesCast == 0l) {
            return "0%";
        }
        BigDecimal votes = new BigDecimal(result.getVotesGained() * 100);
        BigDecimal percent = votes.divide(new BigDecimal(totalVotesCast), 2, RoundingMode.UP);
        System.err.println("Percent for: " + result.getCandidate() + "= " + percent);
        return percent + "%";
    }

    public String getBackgroundColor(CandidateElectionResult result) {
        return "#" + result.getCandidate().getPartyColor();
    }

    public DataModel<ElectionResult> getElectionResultModel() {
        return electionResultModel;
    }

    public List<ElectionResult> getElectionResults() {
        return electionResults;
    }

    public long getTotalVotesCast() {
        return presidentialElectionResult != null ? presidentialElectionResult.getVotesCast() : 0;
    }

    public long getTotalVotesCast(ElectionResult electionResult) {
        return electionResult != null ? electionResult.getVotesCast() : 0;
    }

    public long getTotalVotesDisputed() {
        return presidentialElectionResult == null ? 0 : presidentialElectionResult.getVotesDisputed();
    }

    public long getTotalVotesDisputed(ElectionResult presidentialElectionResult) {
        return presidentialElectionResult == null ? 0 : presidentialElectionResult.getVotesDisputed();
    }

    public long getTotalVotesRejected() {
        return presidentialElectionResult == null ? 0 : presidentialElectionResult.getVotesRejected();
    }

    public long getTotalVotesRejected(ElectionResult presidentialElectionResult) {
        return presidentialElectionResult == null ? 0 : presidentialElectionResult.getVotesRejected();
    }

    public BigDecimal getVoterTurnout() {
        if (electiveRegion.getRegisteredVoters() == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal rVoters = new BigDecimal(electiveRegion.getRegisteredVoters());
        BigDecimal tVoters = new BigDecimal(getTotalVotesCast() * 100);
        BigDecimal percent = tVoters.divide(rVoters, 2, RoundingMode.UP);
        return percent;
    }

    public BigDecimal getVoterTurnout(ElectionResult presidentialElectionResult) {
        if (presidentialElectionResult.getElectiveRegion().getRegisteredVoters() == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal rVoters = new BigDecimal(presidentialElectionResult.getElectiveRegion().getRegisteredVoters());
        BigDecimal tVoters = new BigDecimal(getTotalVotesCast(presidentialElectionResult) * 100);
        BigDecimal percent = tVoters.divide(rVoters, 2, RoundingMode.UP);
        return percent;
    }

    public String getReportingStationDescription() {
        long reportingStation = electionResultFacade.getReportingPollingStations();
        long count = pollingStationFacade.count();
        String report = "<span style=\"color: cyan; font-weight:bold\">" + reportingStation + "</span>/" + count;
        return report;
    }

    public String getSeriesColors() {
        return seriesColors;
    }

    public CartesianChartModel getResultModel() {
        if (resultModel == null) {
            prepareResultModel();
        }
        return resultModel;
    }
}
