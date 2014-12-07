package com.anosym.elexar.controller;

import com.anosym.elexar.Candidate;
import com.anosym.elexar.ElectiveRegion;
import com.anosym.elexar.facade.CandidateFacade;
import com.anosym.elexar.facade.ElectiveRegionFacade;
import com.anosym.elexar.util.ElectiveRegionType;
import com.anosym.elexar.util.ElectiveSeat;
import com.anosym.utilities.Utility;
import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author marembo
 */
@Named(value = "candidateController")
@SessionScoped
public class CandidateController implements Serializable {

    @EJB
    private ElectiveRegionFacade electiveRegionFacade;
    @EJB
    private CandidateFacade candidateFacade;
    private Candidate candidate;
    private UploadedFile candidateImage;
    private UploadedFile partyLogo;
    private boolean addCandidate;
    private boolean onEditMode;
    private ElectiveRegionType electiveRegionType;

    /**
     * Creates a new instance of CandidateController
     */
    public CandidateController() {
    }

    public void prepareAddPresidentialCandidate() {
        setAddCandidate(true);
        candidate = new Candidate();
        electiveRegionType = ElectiveRegionType.COUNTRY;
        candidate.setElectiveRegion(electiveRegionFacade.findCountryElectiveRegion());
        candidate.setElectiveSeat(ElectiveSeat.PRESIDENTAIL);
    }

    public void prepareEditPresidentialCandidate(Candidate candidate) {
        setAddCandidate(true);
        this.candidate = candidate;
        prepareElectiveRegionsForElectiveSeats();
        this.onEditMode = true;
    }

    public List<ElectiveRegion> getElectiveRegions() {
        if (electiveRegionType != null) {
            return electiveRegionFacade.getElectiveRegions(electiveRegionType);
        }
        return new ArrayList<ElectiveRegion>();
    }

    public void addElectionCandidate() {
        try {
            if (onEditMode) {
                candidateFacade.edit(candidate);
                System.err.println("candidate party color: " + candidate.getPartyColor());
            } else {
                candidateFacade.create(candidate);
            }
            JsfUtil.addSuccessMessage("Candidate successfully added");
        } catch (Exception e) {
            ElexarController.logError(e);
            JsfUtil.addErrorMessage("Error adding election candidate");
        } finally {
            if (!onEditMode) {
                if (addCandidate) {
                    prepareAddPresidentialCandidate();
                }
            } else {
                addCandidate = false;
            }
            onEditMode = false;
        }
    }

    public void setPartyLogo(UploadedFile partyLogo) {
        this.partyLogo = partyLogo;
        System.out.println(partyLogo.getFileName());
    }

    public UploadedFile getPartyLogo() {
        return partyLogo;
    }

    public void setCandidateImage(UploadedFile candidateImage) {
        this.candidateImage = candidateImage;
        System.out.println(candidateImage.getFileName());
    }

    public UploadedFile getCandidateImage() {
        return candidateImage;
    }

    public File getCandidateResourceUrl() {
        File file = new File(Utility.getHomeDirectory() + File.separator + "candidates" + File.separator + getCandidate().getCandidateId());
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    public List<Candidate> getPresidentialCandidates() {
        return candidateFacade.findCandidatesForElectiveSeats(ElectiveSeat.PRESIDENTAIL);
    }

    public void candidateUpdate(Candidate editedCandidate) {
        System.err.println("Updating candidate: " + editedCandidate);
        System.err.println("Updating candidate: " + editedCandidate.getPartyColor());
        if (editedCandidate != null) {
            candidateFacade.edit(editedCandidate);
        }
    }

    public String getCandidateContextResourceUrl() {
        return File.separator + "candidates" + File.separator + getCandidate().getCandidateId();
    }

    public String getCandidateContextResourceUrl(Candidate candidate) {
        return File.separator + "candidates" + File.separator + candidate.getCandidateId();
    }

    public String getCandidatePhotoContextUrl() {
        return getCandidateContextResourceUrl() + File.separator + candidate.getCandidatePhoto();
    }

    public String getCandidatePoliticalLogoContextUrl() {
        return getCandidateContextResourceUrl() + File.separator + candidate.getPoliticalLogo();
    }

    public String getPartLogo(Candidate candidate) {
        return getCandidateContextResourceUrl(candidate) + File.separator + candidate.getPoliticalLogo();
    }

    public void handleUploadCandidateImage(FileUploadEvent event) {
        UploadedFile file = event.getFile();
        getCandidate().setCandidatePhoto(file.getFileName());
        try {
            FileOutputStream out = new FileOutputStream(new File(getCandidateResourceUrl(), file.getFileName()));
            byte[] data = file.getContents();
            out.write(data);
            out.close();
        } catch (Exception e) {
            ElexarController.logError(e);
        }
    }

    public void handleUploadPartyLogo(FileUploadEvent event) {
        UploadedFile file = event.getFile();
        getCandidate().setPoliticalLogo(file.getFileName());
        try {
            FileOutputStream out = new FileOutputStream(new File(getCandidateResourceUrl(), file.getFileName()));
            byte[] data = file.getContents();
            out.write(data);
            out.close();
        } catch (Exception e) {
            ElexarController.logError(e);
        }
    }

    public void setCandidate(Candidate candidate) {
        this.candidate = candidate;
    }

    public void setAddCandidate(boolean addCandidate) {
        this.addCandidate = addCandidate;
    }

    public boolean isAddCandidate() {
        return addCandidate;
    }

    public void prepareElectiveRegionsForElectiveSeats() {
        if (getCandidate() != null && getCandidate().getElectiveSeat() != null) {
            switch (getCandidate().getElectiveSeat()) {
                case PRESIDENTAIL:
                    electiveRegionType = ElectiveRegionType.COUNTRY;
                    break;
                case WOMEN_REPRESENTATIVE:
                case SENATORIAL:
                case GUBERNATORIAL:
                    electiveRegionType = ElectiveRegionType.COUNTY;
                    break;
                case MP:
                    electiveRegionType = ElectiveRegionType.CONSTITUENCY;
                    break;
                case COUNTY_REPRESENTATIVE:
                    electiveRegionType = ElectiveRegionType.COUNTY_WARD;
                    break;
            }
        }
    }

    public Candidate getCandidate() {
        if (candidate == null) {
            candidate = new Candidate();
        }
        return candidate;
    }

    public ElectiveSeat[] getElectiveSeats() {
        return ElectiveSeat.values();
    }

    public String getCandidatePhoto() {
        return getCandidatePhoto(candidate);
    }

    public String getCandidatePhoto(Candidate candidate) {
        return "/candidates/" + candidate.getCandidateId() + "/" + candidate.getCandidatePhoto();
    }

    public String getCandidatePhotoFile() {
        return getCandidatePhotoFile(candidate);
    }

    public String getCandidatePhotoFile(Candidate candidate) {
        return Utility.getHomeDirectory() + "/candidates/" + candidate.getCandidateId() + "/" + candidate.getCandidatePhoto();
    }
}
