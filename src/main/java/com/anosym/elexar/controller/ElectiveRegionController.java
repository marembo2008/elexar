package com.anosym.elexar.controller;

import com.anosym.elexar.Candidate;
import com.anosym.elexar.CandidateElectionResult;
import com.anosym.elexar.Constituency;
import com.anosym.elexar.Country;
import com.anosym.elexar.County;
import com.anosym.elexar.CountyWard;
import com.anosym.elexar.ElectionMalpractice;
import com.anosym.elexar.ElectionMalpracticeData;
import com.anosym.elexar.ElectionResult;
import com.anosym.elexar.ElectionTurnout;
import com.anosym.elexar.ElectiveRegion;
import com.anosym.elexar.ElectiveRegionBoundary;
import com.anosym.elexar.facade.ConstituencyFacade;
import com.anosym.elexar.facade.CountryFacade;
import com.anosym.elexar.facade.CountyFacade;
import com.anosym.elexar.facade.CountyWardFacade;
import com.anosym.elexar.facade.ElectionMalpracticeDataFacade;
import com.anosym.elexar.facade.ElectionMalpracticeFacade;
import com.anosym.elexar.facade.ElectionResultFacade;
import com.anosym.elexar.facade.ElectionTurnoutFacade;
import com.anosym.elexar.facade.ElectionTurnoutReportFacade;
import com.anosym.elexar.facade.ElectiveRegionBoundaryFacade;
import com.anosym.elexar.facade.ElectiveRegionFacade;
import com.anosym.elexar.facade.ElectiveRegionFlaggedIssueFacade;
import com.anosym.elexar.facade.PollingStationFacade;
import com.anosym.elexar.util.ElectiveRegionType;
import com.anosym.jflemax.geometry.Line;
import com.anosym.jflemax.validation.RequestStatus;
import com.anosym.jflemax.validation.annotation.LoginStatus;
import com.anosym.jflemax.validation.annotation.OnRequest;
import com.anosym.jflemax.validation.annotation.OnRequests;
import com.anosym.utilities.FormattedCalendar;
import com.anosym.utilities.Utility;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.event.map.OverlaySelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Overlay;
import org.primefaces.model.map.Polygon;

/**
 *
 * @author marembo
 */
@Named(value = "electiveRegionController")
@SessionScoped
@OnRequests(onRequests = {
    @OnRequest(logInStatus = LoginStatus.EITHER, requestStatus = RequestStatus.FULL_REQUEST,
               toPages = "*", onRequestMethod = "getRequestedElectiveRegion")
})
public class ElectiveRegionController implements Serializable {

    @EJB
    private ElectionMalpracticeFacade electionMalpracticeFacade;
    @EJB
    private ElectionMalpracticeDataFacade electionMalpracticeDataFacade;
    @EJB
    private ElectionResultFacade electionResultFacade;
    @EJB
    private ElectionTurnoutFacade electionTurnoutFacade;
    @EJB
    private ElectionTurnoutReportFacade electionTurnoutReportFacade;
    @EJB
    private ElectiveRegionFacade electiveRegionFacade;
    @EJB
    private CountryFacade countryFacade;
    @EJB
    private CountyFacade countyFacade;
    @EJB
    private PollingStationFacade pollingStationFacade;
    @EJB
    private CountyWardFacade countyWardFacade;
    @EJB
    private ElectiveRegionFlaggedIssueFacade electiveRegionFlaggedIssueFacade;
    @EJB
    private ElectiveRegionBoundaryFacade electiveRegionBoundaryFacade;
    @EJB
    private ConstituencyFacade constituencyFacade;
    @Inject
    private PoliticalAgentController politicalAgentController;
    @Inject
    private ElectionTurnoutController electionTurnoutController;
    @Inject
    private ElectionResultController electionResultController;
    private ElectiveRegion electiveRegion;
    private ElectiveRegion parentElectiveRegion;
    ElectiveRegion countryElectiveRegion;
    private boolean addElectiveRegion;
    private TreeNode electiveRegionMainNode;
    /**
     * Used for search polling stations by name
     */
    private String electiveRegionName;
    private MapModel electiveRegionBoundariesModel;
    private String javascriptRegionBounds;
    private String javascriptRegionBoundsForElectionTurnout;

    /**
     * Creates a new instance of ElectiveRegionController
     */
    public ElectiveRegionController() {
    }

    @PostConstruct
    public void loadCountryRegion() {
        electiveRegion = electiveRegionFacade.findCountryElectiveRegion();
        prepareJavascriptRegionBounds();
        prepareJavascriptRegionBoundsForElectionTurnout();
    }

    public void setElectiveRegionName(String electiveRegionName) {
        this.electiveRegionName = electiveRegionName;
    }

    public String getElectiveRegionName() {
        return electiveRegionName;
    }

    public List<ElectiveRegion> searchPollingStations() {
        List<ElectiveRegion> electiveRegions = pollingStationFacade.searchPollingStations(electiveRegionName);
        if (electiveRegions.isEmpty()) {
            electiveRegions = new ArrayList<ElectiveRegion>(pollingStationFacade.findPollingStationNotAssignedToAgents());
        }
        if (politicalAgentController.isOnEditMode() && politicalAgentController.getPoliticalAgent().getElectiveRegion() != null) {
            electiveRegions.add(politicalAgentController.getPoliticalAgent().getElectiveRegion());
        }
        return electiveRegions;
    }

    public void resetToCountry() {
        loadCountryRegion();
        electionResultController.electiveRegionSelected();
        prepareJavascriptRegionBounds();
    }

    public void getRequestedElectiveRegion() {
        if (!ElexarController.isAjaxRequest()) {
            String update_request = ElexarController.getParameter("update_request");
            System.out.println("update_request: " + update_request);
            if (!Boolean.valueOf(update_request)) {
                String elective_region = ElexarController.getParameter("elective_region");
                if (!Utility.isNullOrEmpty(elective_region)) {
                    electiveRegion = electiveRegionFacade.find(Long.parseLong(elective_region));
                    electionResultController.electiveRegionSelected();
                    electionTurnoutController.electiveRegionSelected();
                    prepareJavascriptRegionBounds();
                    prepareJavascriptRegionBoundsForElectionTurnout();
                }
            }
        }
    }

    public int getMapZoomLevel() {
        int zoom = 6;
        if (this.electiveRegion != null) {
            switch (electiveRegion.getElectiveRegionType()) {
                case COUNTRY:
                    zoom = 6;
                    break;
                case COUNTY:
                    zoom = 8;
                    break;
                case CONSTITUENCY:
                    zoom = 9;
                    break;
            }
        }
        return zoom;
    }

    public void electiveRegionSelected(OverlaySelectEvent event) {
        Overlay polygon = event.getOverlay();
        System.err.println("electiveRegionSelected: " + polygon);
        if (polygon instanceof Polygon) {
            String elective_region = polygon.getId();
            System.err.println("elective_region: " + elective_region);
            electiveRegion = electiveRegionFacade.find(Long.parseLong(elective_region));
            electionResultController.electiveRegionSelected();
        }
    }

    public MapModel getElectiveRegionBoundaries() {
//    if (electiveRegionBoundariesModel == null) {
//      electiveRegionBoundariesModel = new DefaultMapModel();
//    } else {
//      electiveRegionBoundariesModel.getPolygons().clear();
//    }
//    ElectiveRegion currentElectiveRegion = electiveRegionFacade.findElectiveRegion(this.electiveRegion.getRegionId(), this.electiveRegion.getElectiveRegionType());
//    if (currentElectiveRegion != null && currentElectiveRegion.getCentreCoordinate() != null) {
//      List<ElectiveRegion> electiveRegions = new ArrayList<ElectiveRegion>();
//      if (currentElectiveRegion.getElectiveRegionType() == ElectiveRegionType.CONSTITUENCY) {
//        electiveRegions.add(currentElectiveRegion);
//      } else {
//        electiveRegions = electiveRegionFacade.loadElectiveRegions(currentElectiveRegion);
//      }
//      for (ElectiveRegion er : electiveRegions) {
//        ElectionResult electionResult = electionResultFacade.getPresidentialElectionResults(region);
//        if (electionResult != null) {
//          CandidateElectionResult highestElectionResult = electionResult.getHighestCandidateElectionResult();
//          String color = "#" + highestElectionResult.getCandidate().getPartyColor();
//          if (er.getRegionBounds() != null) {
//            Polygon electiveRegionBounds = new Polygon();
//            for (Line line : er.getRegionBounds().getPolygonLineBounds()) {
//              Coordinate start = line.getStartCoordinate();
//              Coordinate end = line.getEndCoordinate();
//              electiveRegionBounds.getPaths().add(new LatLng(start.getLatitude().floatValue(), start.getLongitude().floatValue()));
//              electiveRegionBounds.getPaths().add(new LatLng(end.getLatitude().floatValue(), end.getLongitude().floatValue()));
//            }
//            electiveRegionBounds.setStrokeColor("#565656");
//            electiveRegionBounds.setStrokeWeight(2);
//            electiveRegionBounds.setStrokeOpacity(1.0);
//            electiveRegionBounds.setFillColor(color);
//            electiveRegionBounds.setFillOpacity(1.0);
//            electiveRegionBounds.setId(regionId + "");
//            electiveRegionBoundariesModel.addOverlay(electiveRegionBounds);
//          }
//        }
//      }
//    }
        return electiveRegionBoundariesModel;
    }

    public int getElectiveRegionZoomLevel(ElectiveRegion currentElectiveRegion) {
        return 0;
    }

    public String getElectiveRegionJavascriptBounds(ElectiveRegionBoundary er) {
        String bounds = "";
        if (er != null && er.getRegionBoundaries() != null && er.getRegionBoundaries().getPolygonLineBounds() != null) {
            int i = 0;
            List<Line> lines = er.getRegionBoundaries().getPolygonLineBounds();
            int size = lines.size();
            for (; i < size; i += 4) {
                Line line = lines.get(i);
                if (!bounds.isEmpty()) {
                    bounds += ",\n";
                }
                bounds += ("  new google.maps.LatLng(" + line.getStartCoordinate().getLatitude() + ", " + line.getStartCoordinate().getLongitude() + "),\n");
                bounds += ("  new google.maps.LatLng(" + line.getEndCoordinate().getLatitude() + ", " + line.getEndCoordinate().getLongitude() + ")");
                i++;
            }
        }
        return ("var bounds" + er.getElectiveRegion().getRegionId() + " = [\n" + bounds + "\n];\n\n");
    }

    public void prepareJavascriptRegionBounds() {
        //We return javascript functions to draw and detect the bounds of this regions.
        String javascript = "";
        if (this.electiveRegion != null) {
            /**
             * We upload the current elective region with bounds information.
             */
            ElectiveRegionBoundary electiveRegionBoundary = electiveRegionBoundaryFacade.findElectiveRegionBoundary(electiveRegion);
            System.out.println("current electiveRegionBoundary: " + electiveRegionBoundary);
            if (electiveRegionBoundary != null && electiveRegionBoundary.getCentreCoordinate() != null) {
                System.out.println("current electiveRegionBoundary: " + electiveRegionBoundary);
                System.out.println("current electiveRegionBoundary centre: " + electiveRegionBoundary.getCentreCoordinate());
                List<ElectiveRegionBoundary> electiveRegionBoundaries = new ArrayList<ElectiveRegionBoundary>();
                if (electiveRegion.getElectiveRegionType() == ElectiveRegionType.CONSTITUENCY) {
                    electiveRegionBoundaries.add(electiveRegionBoundary);
                } else {
                    electiveRegionBoundaries = electiveRegionBoundaryFacade.getElectiveRegionBoundaries(electiveRegion);
                }
                System.out.println("current electiveRegionBoundary-regions: " + electiveRegionBoundaries.size());
                //evaluate the zoom level
                javascript = ""
                        + "var zoom = 6;\n"
                        + "var regionType = '" + electiveRegion.getElectiveRegionType() + "';\n"
                        + "if(regionType != 'Country'){\n"
                        + getElectiveRegionJavascriptBounds(electiveRegionBoundary)
                        + "var polygonBounds = new google.maps.Polygon({\n"
                        + "  path: bounds" + electiveRegion.getRegionId() + ",\n"
                        + "});\n"
                        + "var area = google.maps.geometry.spherical.computeArea(polygonBounds.getPath());\n"
                        + "console.log('Kenyan area: '+area);\n"
                        + "if(area != 0){\n"
                        + "var cmp = 57048269779.20893;\n"
                        + "var zoomFactor = Math.sqrt(Math.sqrt(Math.sqrt((cmp  / area)))) / 1.25;\n"
                        + "zoom = (8 * zoomFactor) | 0;"
                        + "if(zoom > 13){\n"
                        + "zoom = 13;\n"
                        + "}\n"
                        + "console.log('zoom Factor: '+zoomFactor);"
                        + "console.log('zoom level: '+zoom);"
                        + "}\n"
                        + "}\n\n";
                javascript += "var centre = new google.maps.LatLng(" + electiveRegionBoundary.getCentreCoordinate().getLatitude() + ", " + electiveRegionBoundary
                        .getCentreCoordinate().getLongitude() + ");\n"
                        + "var mapOptions = {\n"
                        + "zoom: zoom,\n"
                        + "center: centre,\n"
                        + "mapTypeId: google.maps.MapTypeId.ROADMAP\n"
                        + "};\n\n"
                        + "function loadSelectedElectiveRegion(regionId){\n\n"
                        + "console.log(location.host);\n"
                        + "var url = getCurrentLocationWithoutQueryParameter();\n"
                        + "window.location.assign(url+'?elective_region='+regionId);\n"
                        + "}\n"
                        + "var map = new google.maps.Map(document.getElementById(\"gmapPanel\"), mapOptions);\n\n";
                for (ElectiveRegionBoundary er : electiveRegionBoundaries) {
                    ElectiveRegion region = er.getElectiveRegion();
                    String regionId = region.getRegionId().toString();
                    ElectionResult electionResult = electionResultFacade.getPresidentialElectionResults(region);
                    String color = "#FF33CC";
                    if (electionResult != null) {
                        CandidateElectionResult highestElectionResult = electionResult.getHighestCandidateElectionResult();
                        color = "#" + highestElectionResult.getCandidate().getPartyColor();
                    }
                    if (er.getRegionBoundaries() != null) {
                        String bounds = getElectiveRegionJavascriptBounds(er);
                        javascript += bounds;
                        //create the polygon
                        String polygon = "var polygonBounds" + regionId + " = new google.maps.Polygon({\n"
                                + "  path: bounds" + regionId + ",\n"
                                + "  strokeColor: \"#999999\",\n"
                                + "  strokeOpacity: 1.0,\n"
                                + "  strokeWeight: 2,\n"
                                + "  fillColor: \"" + color + "\",\n"
                                + "  fillOpacity: 0.65\n"
                                + "});\n\n";
                        javascript += polygon;
                        javascript += ("polygonBounds" + regionId + ".setMap(map);\n");
                        //add on mouse over to show which region.
                        String onclick = "google.maps.event.addListener(polygonBounds" + regionId + ", 'click', zoomElectiveRegion" + regionId + ");\n";
                        String zoomElectiveRegion = "function zoomElectiveRegion" + regionId + "(event){\n"
                                + "  console.log(event.latLng);\n"
                                + "  loadSelectedElectiveRegion('" + regionId + "');\n"
                                + "};\n";
                        //do we have any election malpractice indications?
                        if (electionMalpracticeFacade.hasMalpractice(region)) {
                            //get the malpractices here
                            //if we mouse over, just show the
                            //get the polling statation malpractices
                            ElectionMalpracticeData electionMalpracticeData = electionMalpracticeDataFacade.findElectionMalpractice(region);
                            javascript += "var markerCentre" + regionId + " = new google.maps.LatLng(" + er.getCentreCoordinate().getLatitude() + ", " + er
                                    .getCentreCoordinate().getLongitude() + ");\n";
                            String marker = "var malpracticeMarker" + regionId + " = new google.maps.Marker({\n"
                                    + "position: markerCentre" + regionId + ",\n"
                                    + "map: map\n"
                                    + "});\n\n";
                            javascript += marker;
                            if (electionMalpracticeData != null) {
                                ElectionMalpractice electionMalpractice = electionMalpracticeData.getElectionMalpractice();
                                String onMouseOver = "google.maps.event.addListener(malpracticeMarker" + regionId + ", 'mouseover', showElectionMalpractices" + regionId + ");\n";
                                String onMouseOut = "google.maps.event.addListener(malpracticeMarker" + regionId + ", 'mouseout', hideElectionMalpractices" + regionId + ");\n";
                                String infoWindow = "var infoWindow" + regionId + " = new google.maps.InfoWindow();\n";
                                String infoWindowState = "var infoWindowOpen" + regionId + " = true;\n";
                                String showElectionMalpractices = "function showElectionMalpractices" + regionId + "(event){\n"
                                        + "if(!infoWindowOpen" + regionId + "){\n"
                                        + "infoWindow" + regionId + ".open(map);"
                                        + "}\n"
                                        + "var content = '"
                                        + "<div>"
                                        + "<span style=\"font-size: 20px; font-weight: bold\">" + electionMalpractice.getPoliticalAgent()
                                        .getElectiveRegion() + "</span>"
                                        + "<br/>"
                                        + "<span style=\"font-size: 16px\">Political Agent: " + electionMalpractice.getPoliticalAgent().getAgentName() + "(" + electionMalpractice
                                        .getPoliticalAgent().getAgentPhoneNumber() + ")</span>"
                                        + "<br/>"
                                        + "<span style=\"font-size: 14px; font-weight: bold\">Election Malpractice: " + electionMalpractice
                                        .getMalpracticeType() + "</span>"
                                        + "<br/>"
                                        + "<span>Number of Election Malpractices in this Station: <span style=\"color: red\">" + electionMalpracticeData
                                        .getNumberOfElectionMalpractices() + "</span></span>"
                                        + "<br/>"
                                        + "<span>Time of malpractice: <span style=\"color: red\">" + FormattedCalendar.toISOString(electionMalpractice
                                                .getMalpracticeTime()) + "</span></span>"
                                        + "</div>';\n"
                                        + "infoWindow" + regionId + ".setContent(content);\n"
                                        + "infoWindow" + regionId + ".setPosition(event.latLng);\n"
                                        + "};\n";
                                String hideElectionMalpractices = "function hideElectionMalpractices" + regionId + "(event){\n"
                                        + "infoWindow" + regionId + ".close();\n"
                                        + "infoWindowOpen" + regionId + " = false;\n"
                                        + "}\n\n";
                                String openInfoWindow = "infoWindow" + regionId + ".open(map);\n";
                                javascript += infoWindow;
                                javascript += infoWindowState;
                                javascript += showElectionMalpractices;
                                javascript += hideElectionMalpractices;
                                javascript += onMouseOver;
                                javascript += onMouseOut;
                                javascript += openInfoWindow;
                            }
                        }
                        javascript += onclick;
                        javascript += zoomElectiveRegion;
                    }
                }
            }
        }
        javascriptRegionBounds = javascript;
    }

    public String getGoogleMapJavascriptApi() {
        return "http://maps.google.com/maps/api/js?sensor=false&libraries=geometry";
    }

    public void prepareJavascriptRegionBoundsForElectionTurnout() {
        //We return javascript functions to draw and detect the bounds of this regions.
        String javascript = "";
        if (this.electiveRegion != null) {
            /**
             * We upload the current elective region with bounds information.
             */
            ElectiveRegionBoundary electiveRegionBoundary = electiveRegionBoundaryFacade.findElectiveRegionBoundary(electiveRegion);
            if (electiveRegionBoundary != null && electiveRegionBoundary.getCentreCoordinate() != null) {
                List<ElectiveRegionBoundary> electiveRegionBoundaries = new ArrayList<ElectiveRegionBoundary>();
                if (electiveRegion.getElectiveRegionType() == ElectiveRegionType.CONSTITUENCY) {
                    electiveRegionBoundaries.add(electiveRegionBoundary);
                } else {
                    electiveRegionBoundaries = electiveRegionBoundaryFacade.getElectiveRegionBoundaries(electiveRegion);
                }
                javascript = ""
                        + "var zoom = 6;\n"
                        + "var regionType = '" + electiveRegion.getElectiveRegionType() + "';\n"
                        + "if(regionType != 'Country'){\n"
                        + getElectiveRegionJavascriptBounds(electiveRegionBoundary)
                        + "var polygonBounds = new google.maps.Polygon({\n"
                        + "  path: bounds" + electiveRegion.getRegionId() + ",\n"
                        + "});\n"
                        + "var area = google.maps.geometry.spherical.computeArea(polygonBounds.getPath());\n"
                        + "console.log('Kenyan area: '+area);\n"
                        + "if(area != 0){\n"
                        + "var cmp = 57048269779.20893;\n"
                        + "var zoomFactor = Math.sqrt(Math.sqrt(Math.sqrt((cmp  / area)))) / 1.25;\n"
                        + "zoom = (8 * zoomFactor) | 0;"
                        + "if(zoom > 13){\n"
                        + "zoom = 13;\n"
                        + "}\n"
                        + "console.log('zoom Factor: '+zoomFactor);"
                        + "console.log('zoom level: '+zoom);"
                        + "}\n"
                        + "}\n\n";
                javascript += "var centre = new google.maps.LatLng(" + electiveRegionBoundary.getCentreCoordinate().getLatitude() + ", " + electiveRegionBoundary
                        .getCentreCoordinate().getLongitude() + ");\n"
                        + "var mapOptions = {\n"
                        + "zoom: zoom,\n"
                        + "center: centre,\n"
                        + "mapTypeId: google.maps.MapTypeId.ROADMAP\n"
                        + "};\n\n"
                        + "function loadSelectedElectiveRegion(regionId){\n\n"
                        + "console.log(location.host);\n"
                        + "var url = getCurrentLocationWithoutQueryParameter();\n"
                        + "window.location.assign(url+'?elective_region='+regionId);\n"
                        + "}\n"
                        + "var map = new google.maps.Map(document.getElementById(\"electionTurnoutMapPanel\"), mapOptions);\n\n";
                for (ElectiveRegionBoundary er : electiveRegionBoundaries) {
                    ElectiveRegion region = er.getElectiveRegion();
                    String regionId = region.getRegionId() + "";
                    long registeredVoters = region.getRegisteredVoters();
                    long castVotes = 0l;
                    ElectionTurnout et = electionTurnoutFacade.getElectionTurnout(region);
                    if (et != null) {
                        castVotes = electionTurnoutReportFacade.getTotalVotesCast(et);
                    }
                    BigDecimal percentTurnout = BigDecimal.valueOf(castVotes * 100 + 0.000001).divide(BigDecimal.valueOf(registeredVoters + 0.00001),
                                                                                                      2, RoundingMode.UP);
                    String color = "#FF11EE";
                    if (electiveRegionFlaggedIssueFacade.hasElectiveRegionFlaggedIssue(region)) {
                        color = "#FF5050";
                    } else if (percentTurnout.compareTo(BigDecimal.valueOf(60.0)) > 0) {
                        color = "#33CC33";
                    } else if (percentTurnout.compareTo(BigDecimal.valueOf(30.0)) > 0) {
                        color = "#0066FF";
                    }
                    System.err.println(er + ":color=" + color + "(" + percentTurnout + ")");
                    if (er.getRegionBoundaries() != null) {
                        String bounds = getElectiveRegionJavascriptBounds(er);
                        javascript += bounds;
                        //create the polygon
                        String polygon = "var polygonBounds" + regionId + " = new google.maps.Polygon({\n"
                                + "  path: bounds" + regionId + ",\n"
                                + "  strokeColor: \"#999999\",\n"
                                + "  strokeOpacity: 1.0,\n"
                                + "  strokeWeight: 2,\n"
                                + "  fillColor: \"" + color + "\",\n"
                                + "  fillOpacity: 0.65\n"
                                + "});\n\n";
                        javascript += polygon;
                        javascript += ("polygonBounds" + regionId + ".setMap(map);\n");
                        //add on mouse over to show which region.
                        String onclick = "google.maps.event.addListener(polygonBounds" + regionId + ", 'click', zoomElectiveRegion" + regionId + ");\n";
                        String zoomElectiveRegion = "function zoomElectiveRegion" + regionId + "(event){\n"
                                + "  console.log(event.latLng);\n"
                                + "  loadSelectedElectiveRegion('" + regionId + "');\n"
                                + "};\n";
                        //do we have any election malpractice indications?
                        if (electionMalpracticeFacade.hasMalpractice(region)) {
                            //get the malpractices here
                            //if we mouse over, just show the
                            //get the polling statation malpractices
                            ElectionMalpracticeData electionMalpracticeData = electionMalpracticeDataFacade.findElectionMalpractice(region);
                            javascript += "var markerCentre" + regionId + " = new google.maps.LatLng(" + er.getCentreCoordinate().getLatitude() + ", " + er
                                    .getCentreCoordinate().getLongitude() + ");\n";
                            String marker = "var malpracticeMarker" + regionId + " = new google.maps.Marker({\n"
                                    + "position: markerCentre" + regionId + ",\n"
                                    + "map: map\n"
                                    + "});\n\n";
                            javascript += marker;
                            if (electionMalpracticeData != null) {
                                ElectionMalpractice electionMalpractice = electionMalpracticeData.getElectionMalpractice();
                                String onMouseOver = "google.maps.event.addListener(malpracticeMarker" + regionId + ", 'mouseover', showElectionMalpractices" + regionId + ");\n";
                                String onMouseOut = "google.maps.event.addListener(malpracticeMarker" + regionId + ", 'mouseout', hideElectionMalpractices" + regionId + ");\n";
                                String infoWindow = "var infoWindow" + regionId + " = new google.maps.InfoWindow();\n";
                                String infoWindowState = "var infoWindowOpen" + regionId + " = true;\n";
                                String showElectionMalpractices = "function showElectionMalpractices" + regionId + "(event){\n"
                                        + "if(!infoWindowOpen" + regionId + "){\n"
                                        + "infoWindow" + regionId + ".open(map);"
                                        + "}\n"
                                        + "var content = '"
                                        + "<div>"
                                        + "<span style=\"font-size: 20px; font-weight: bold\">" + electionMalpractice.getPoliticalAgent()
                                        .getElectiveRegion() + "</span>"
                                        + "<br/>"
                                        + "<span style=\"font-size: 16px\">Political Agent: " + electionMalpractice.getPoliticalAgent().getAgentName() + "(" + electionMalpractice
                                        .getPoliticalAgent().getAgentPhoneNumber() + ")</span>"
                                        + "<br/>"
                                        + "<span style=\"font-size: 14px; font-weight: bold\">Election Malpractice: " + electionMalpractice
                                        .getMalpracticeType() + "</span>"
                                        + "<br/>"
                                        + "<span>Number of Election Malpractices in this Station: <span style=\"color: red\">" + electionMalpracticeData
                                        .getNumberOfElectionMalpractices() + "</span></span>"
                                        + "<br/>"
                                        + "<span>Time of malpractice: <span style=\"color: red\">" + FormattedCalendar.toISOString(electionMalpractice
                                                .getMalpracticeTime()) + "</span></span>"
                                        + "</div>';\n"
                                        + "infoWindow" + regionId + ".setContent(content);\n"
                                        + "infoWindow" + regionId + ".setPosition(event.latLng);\n"
                                        + "};\n";
                                String hideElectionMalpractices = "function hideElectionMalpractices" + regionId + "(event){\n"
                                        + "infoWindow" + regionId + ".close();\n"
                                        + "infoWindowOpen" + regionId + " = false;\n"
                                        + "}\n\n";
                                String openInfoWindow = "infoWindow" + regionId + ".open(map);\n";
                                javascript += infoWindow;
                                javascript += infoWindowState;
                                javascript += showElectionMalpractices;
                                javascript += hideElectionMalpractices;
                                javascript += onMouseOver;
                                javascript += onMouseOut;
                                javascript += openInfoWindow;
                            }
                        }
                        javascript += onclick;
                        javascript += zoomElectiveRegion;
                    }
                }
            }
        }
        javascriptRegionBoundsForElectionTurnout = javascript;
    }

    public String getJavascriptRegionBoundsForElectionTurnout() {
        System.out.println(
                javascriptRegionBoundsForElectionTurnout != null ? "javascriptRegionBoundsForElectionTurnout: " + javascriptRegionBoundsForElectionTurnout
                .length() : " javascriptRegionBoundsForElectionTurnout is null");
        return javascriptRegionBoundsForElectionTurnout;
    }

    public String getJavascriptRegionBounds() {
        System.out.println(
                javascriptRegionBounds != null ? "javascriptRegionBounds: " + javascriptRegionBounds.length() : " javascriptRegionBounds is null");
        return javascriptRegionBounds;
    }

    public void setAddElectiveRegion(boolean addElectiveRegion) {
        this.addElectiveRegion = addElectiveRegion;
    }

    public boolean isAddElectiveRegion() {
        return addElectiveRegion;
    }

    public boolean viewElectiveRegions() {
        return electiveRegionFacade.find(getElectiveRegion().getRegionId()) != null;
    }

    public ElectiveRegion getCountryElectiveRegion() {
        return countryElectiveRegion;
    }

    public void prepareViewElectiveRegions() {
        countryElectiveRegion = electiveRegionFacade.findCountryElectiveRegion();
        if (countryElectiveRegion != null) {
            electiveRegionMainNode = new DefaultTreeNode();
            for (ElectiveRegion er : electiveRegionFacade.getElectiveRegionsForElectiveRegion(countryElectiveRegion)) {
                addNodes(electiveRegionMainNode, er);
            }
        }
        if (electiveRegionMainNode != null) {
            electiveRegionMainNode.setExpanded(true);
        }
    }

    public TreeNode getElectiveRegionMainNode() {
        if (electiveRegionMainNode == null) {
            prepareViewElectiveRegions();
        }
        return electiveRegionMainNode;
    }

    public void addNodes(TreeNode parent, ElectiveRegion parentRegion) {
        parent = new DefaultTreeNode(parentRegion, parent);
        for (ElectiveRegion r : electiveRegionFacade.getElectiveRegionsForElectiveRegion(parentRegion)) {
            addNodes(parent, r);
        }
    }

    public void prepareCountryCreate() {
        prepareCreate(ElectiveRegionType.COUNTRY);
    }

    public void prepareCountyCreate() {
        prepareCreate(ElectiveRegionType.COUNTY);
    }

    public void prepareConstituencyCreate() {
        prepareCreate(ElectiveRegionType.CONSTITUENCY);
    }

    public void prepareCountyWardCreate() {
        prepareCreate(ElectiveRegionType.COUNTY_WARD);
    }

    public void setParentElectiveRegion(ElectiveRegion parentElectiveRegion) {
        this.parentElectiveRegion = parentElectiveRegion;
    }

    public ElectiveRegion getParentElectiveRegion() {
        return parentElectiveRegion;
    }

    public void prepareCreate(ElectiveRegionType electiveRegionType) {
        try {
            switch (electiveRegionType) {
                case CONSTITUENCY:
                    electiveRegion = new Constituency();
                    break;
                case COUNTRY:
                    electiveRegion = new Country();
                    break;
                case COUNTY:
                    electiveRegion = new County();
                    break;
                case COUNTY_WARD:
                    electiveRegion = new CountyWard();
                    break;
            }
            setAddElectiveRegion(getElectiveRegion().getElectiveRegionType() == electiveRegionType);
        } finally {
        }
    }

    public void createRegion() {
        try {
            System.out.println("parentElectiveRegion: " + parentElectiveRegion);
            electiveRegion.setParentElectiveRegion(parentElectiveRegion);
            electiveRegionFacade.create(electiveRegion);
            JsfUtil.addSuccessMessage("Region " + electiveRegion + " Successfully created");
        } catch (Exception e) {
            ElexarController.logError(e);
            JsfUtil.addErrorMessage("Failed to create elective region");
        } finally {
            setAddElectiveRegion(false);
            prepareViewElectiveRegions();
        }
    }

    public boolean hasCountryRegion() {
        return electiveRegionFacade.countElectiveRegions(ElectiveRegionType.COUNTRY) > 0;
    }

    public boolean isCountyWardRegion() {
        return getElectiveRegion().getElectiveRegionType() == ElectiveRegionType.COUNTY_WARD;
    }

    public boolean isCountryRegion() {
        return getElectiveRegion().getElectiveRegionType() == ElectiveRegionType.COUNTRY;
    }

    public List<ElectiveRegion> getElectiveRegionsForCurrentRegion() {
        List<ElectiveRegion> ers = electiveRegionFacade.getElectiveRegionsForElectiveRegion(getElectiveRegion());
        if (!ers.isEmpty()) {
            parentElectiveRegion = ers.get(0);
        }
        return ers;
    }

    public boolean showElectiveRegionsForCurrentRegion() {
        int count = electiveRegionFacade.countElectiveRegionsForElectiveRegion(getElectiveRegion());
        System.out.println("showElectiveRegionsForCurrentRegion: " + count);
        return count > 0;
    }

    public ElectiveRegion getElectiveRegion() {
        if (electiveRegion == null) {
            electiveRegion = new Country();
        }
        return electiveRegion;
    }

    public void setElectiveRegion(ElectiveRegion electiveRegion) {
        this.electiveRegion = electiveRegion;
    }

    public ElectiveRegionType[] getElectiveRegionTypes() {
        return ElectiveRegionType.values();
    }

    public List<ElectiveRegion> findElectiveRegion(String query) {
        System.err.println("findElectiveRegion: " + query);
        return electiveRegionFacade.findElectiveRegion(query);
    }

    public List<ElectiveRegion> findElectiveRegion(String query, Candidate candidate) {
        System.err.println("findElectiveRegion: " + query);
        return electiveRegionFacade.findElectiveRegion(query, candidate);
    }

    @FacesConverter("electiverRegionConverter")
    public static class ElectiveRegionConverter implements Converter {

        static Map<String, ElectiveRegion> map = new HashMap<String, ElectiveRegion>();

        @Override
        public Object getAsObject(FacesContext context, UIComponent component, String value) {
            return map.get(value);
        }

        @Override
        public String getAsString(FacesContext context, UIComponent component, Object value) {
            if (value instanceof ElectiveRegion) {
                map.put(value.toString(), (ElectiveRegion) value);
                return value.toString();
            }
            return null;
        }
    }
}
