package nl.molnet.action;

import io.searchbox.annotations.JestId;

import java.io.Serializable;
import java.math.BigDecimal;

import nl.molnet.model.common.AbstractModelObject;

public class EventResultBean extends AbstractModelObject implements Serializable {

    private static final long serialVersionUID = 1741036124678156510L;
    
    @JestId
    private String documentId;
    
    private String sport;
    private int position;
    private int laps;
    private int bestLap;
    private BigDecimal bestSpeed;
    private String startNumber;
    private String firstname;
    private String lastname;
    private String country;

    public EventResultBean(final int id) {
        this.documentId = String.valueOf(id);
    }

    public EventResultBean(int id, String sport, int position, int laps, int bestLap, BigDecimal bestSpeed,
            String startNumber, String firstname, String lastname, String country) {
        this(id);
        this.sport = sport;
        this.position = position;
        this.laps = laps;
        this.bestLap = bestLap;
        this.bestSpeed = bestSpeed;
        this.startNumber = startNumber;
        this.firstname = firstname;
        this.lastname = lastname;
        this.country = country;
    }

    public String getSport() {
        return sport;
    }

    public void setSport(String sport) {
        this.sport = sport;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getLaps() {
        return laps;
    }

    public void setLaps(int laps) {
        this.laps = laps;
    }

    public int getBestLap() {
        return bestLap;
    }

    public void setBestLap(int bestLap) {
        this.bestLap = bestLap;
    }

    public BigDecimal getBestSpeed() {
        return bestSpeed;
    }

    public void setBestSpeed(BigDecimal bestSpeed) {
        this.bestSpeed = bestSpeed;
    }

    public String getStartNumber() {
        return startNumber;
    }

    public void setStartNumber(String startNumber) {
        this.startNumber = startNumber;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public String getDocumentId() {
        return documentId;
    }

    @Override
    public String toString() {
        return "EventResultBean [documentId=" + documentId + ", sport=" + sport + ", position=" + position + ", laps="
                + laps + ", bestLap=" + bestLap + ", bestSpeed=" + bestSpeed + ", startNumber=" + startNumber
                + ", firstname=" + firstname + ", lastname=" + lastname + ", country=" + country + "]";
    }

}
