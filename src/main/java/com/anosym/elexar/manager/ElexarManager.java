package com.anosym.elexar.manager;

import com.anosym.elexar.ElectionTurnout;
import com.anosym.elexar.ElectiveRegion;
import com.anosym.elexar.FlaggedIssue;
import com.anosym.elexar.PollingStation;
import com.anosym.elexar.controller.ElexarController;
import com.anosym.elexar.facade.ElectionResultFacade;
import com.anosym.elexar.facade.ElectionTurnoutFacade;
import com.anosym.elexar.facade.ElectionTurnoutReportFacade;
import com.anosym.elexar.facade.ElectiveRegionFacade;
import com.anosym.elexar.facade.ElectiveRegionFlaggedIssueFacade;
import com.anosym.elexar.facade.PollingStationFacade;
import com.anosym.elexar.mapperdomain.MappedDomainManager;
import com.anosym.elexar.util.FlaggedIssueType;
import com.anosym.jflemax.validation.PageInformation;
import com.anosym.utilities.FormattedCalendar;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;

/**
 *
 * @author marembo
 */
@Singleton
@ApplicationScoped
@Startup
public class ElexarManager {

    private static Calendar POLLING_STATION_OPENING_TIME;
    private static boolean DEBUG_MODE = true;

    public static Calendar getPollingStationOpeningTime() {
        if (DEBUG_MODE) {
            Calendar now = Calendar.getInstance();
            now.set(Calendar.MINUTE, now.get(Calendar.MINUTE) - 2);
            return now;
        }
        if (POLLING_STATION_OPENING_TIME != null) {
            return POLLING_STATION_OPENING_TIME;
        }
        Calendar now = FormattedCalendar.newDateInstance();
        now.set(Calendar.HOUR_OF_DAY, 6);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        return (POLLING_STATION_OPENING_TIME = now);
    }

    @EJB
    private PollingStationFacade pollingStationFacade;
    @EJB
    private ElectionResultFacade electionResultFacade;
    @EJB
    private ElectiveRegionFacade electiveRegionFacade;
    @EJB
    private ElectiveRegionFlaggedIssueFacade electiveRegionFlaggedIssueFacade;
    @EJB
    private ElectionTurnoutReportFacade electionTurnoutReportFacade;
    @EJB
    private ElectionTurnoutFacade electionTurnoutFacade;
    private final int turnoutRange[] = {0, POLLING_STATION_PAGE};
    private static final int POLLING_STATION_PAGE = 100;
    private final int currentOverTurnoutRange[] = {0, POLLING_STATION_PAGE};

    @EJB
    private MappedDomainManager mappedDomainManager;

    @PostConstruct
    public void onStart() {
        PageInformation.onApplicationStart();
        mappedDomainManager.mapDomains();
    }

    public void normalizeElectiveRegionRegisteredVoters() {
        if (electiveRegionFacade.findCountryElectiveRegion().getRegisteredVoters() == 0l) {
            electiveRegionFacade.normalizeRegisteredVoters();
        }
    }

    public void determineFlaggedIssues() {
        /**
         * We need to determine the number of turnout in each region, and the number of turnout so far.
         */
    }

    @Schedule(hour = "*", minute = "*/5")
    public void determineTurnoutIssuesByMidday() {
//    System.err.println("determineTurnoutIssuesByMidday:......");
//    Calendar now = Calendar.getInstance();
//    List<ElectionTurnout> electionTurnouts = electionTurnoutFacade.findElectionTurnoutForElectiveRegionTypes(ElectiveRegionType.POLLING_STATION);
//    if (electionTurnouts != null && electionTurnouts.isEmpty()) {
//      for (ElectionTurnout et : electionTurnouts) {
//        Long totalTurnOutByMidday = electionTurnoutReportFacade.getTotalVotesCast(et, getPollingStationOpeningTime(), now);
//        BigDecimal totalTurnOut = new BigDecimal(totalTurnOutByMidday);
//        BigDecimal registeredVoters = new BigDecimal(et.getElectiveRegion().getRegisteredVoters());
//        if (registeredVoters.compareTo(BigDecimal.ZERO) != 0) {
//          BigDecimal percentTurnout = totalTurnOut.multiply(BigDecimal.valueOf(100)).divide(registeredVoters, 2, RoundingMode.UP);
//          if (percentTurnout.compareTo(MIDDAY_EXPECTED_TURNOUT) < 0) {
//            String issueDescription = "Less than half percent turn out:<br/>"
//                    + "<b>Registered Voters: " + registeredVoters + "</b><br/>"
//                    + "<b>Turnout: " + totalTurnOut + "</b><br/>"
//                    + "<b>Percent Turnout: " + percentTurnout + "</b>";
//            FlaggedIssue flaggedIssue = new FlaggedIssue(FlaggedIssueType.LESS_THAN_HALF_TURNOUT_BY_MIDDAY, issueDescription);
//            electiveRegionFlaggedIssueFacade.addFlaggedIssue(et.getElectiveRegion(), flaggedIssue);
//          }
//        }
//      }
//    }
    }

    @Schedule(hour = "*", minute = "*/2", persistent = false)
    public void determineOverturnOutIssue() {
        System.err.println("determineOverturnOutIssue:......");
        List<PollingStation> pollingStations = pollingStationFacade.findRange(currentOverTurnoutRange);
        if (pollingStations != null && !pollingStations.isEmpty()) {
            for (PollingStation er : pollingStations) {
                ElectionTurnout et = electionTurnoutFacade.getElectionTurnout(er);
                boolean electiveRegionHasThisFlaggedIssue = electiveRegionFlaggedIssueFacade
                        .hasFlaggedIssue(er, FlaggedIssueType.OVER_VOTER_TURNOUT);
                if (!electiveRegionHasThisFlaggedIssue) {
                    long registeredVoters = er.getRegisteredVoters();
                    long totalTurnout = electionTurnoutReportFacade.getTotalVotesCast(et);
                    if (totalTurnout > registeredVoters) {
                        String issue = "Voter turnout more than registered voters:(Voter Turnout: " + totalTurnout + ", Registered Voters: " + registeredVoters + ")";
                        FlaggedIssue flaggedIssue = new FlaggedIssue(FlaggedIssueType.OVER_VOTER_TURNOUT, issue);
                        electiveRegionFlaggedIssueFacade.addFlaggedIssue(et.getElectiveRegion(), flaggedIssue);
                    }
                }
            }
        }
        currentOverTurnoutRange[0] = currentOverTurnoutRange[1];
        currentOverTurnoutRange[1] += POLLING_STATION_PAGE;
    }

    @Schedule(hour = "*", minute = "*/1", persistent = false)
    public void generateElectionResults() {
        electionResultFacade.addTestElectionResultData();
    }

    @Schedule(hour = "*", minute = "*/1", persistent = false)
    public void addTestTurnoutData() {
        try {
            int count = Math.max(1, pollingStationFacade.count());
            for (final ElectiveRegion e : pollingStationFacade.findRange(turnoutRange)) {
                electionTurnoutFacade.doAddTestTurnoutData(e);
            }
            turnoutRange[0] = turnoutRange[1] % count;
            turnoutRange[1] += POLLING_STATION_PAGE;
        } catch (Exception e) {
            ElexarController.logError(e);
        }
    }
}
