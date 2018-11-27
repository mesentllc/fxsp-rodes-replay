package com.fedex.smartpost.utilities.evs.model;

import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;

public class UnmanifestedInfo {
    private XMLGregorianCalendar eventDate;
    private String rectifySource;
    private String status;
    private BigDecimal postageAmount;
    private String clientName;
    private XMLGregorianCalendar ptsTransmissionDate;
    private String mailerId;
    private String mailClass;

    @Override
	public String toString() {
    	StringBuilder sb = new StringBuilder();

    	sb.append("Event Date: ").append(eventDate).append("\n")
    	  .append("Rectify Src: ").append(rectifySource).append("\n")
    	  .append("Status: ").append(status).append("\n")
    	  .append("Postage Amt: ").append(postageAmount).append("\n")
    	  .append("Client Name: ").append(clientName).append("\n")
    	  .append("PTS Trans Date: ").append(ptsTransmissionDate).append("\n")
    	  .append("Mailer Id: ").append(mailerId).append("\n")
    	  .append("Mailer Class: ").append(mailClass).append("\n");
    	return sb.toString();
    }

	public XMLGregorianCalendar getEventDate() {
		return eventDate;
	}
	public void setEventDate(XMLGregorianCalendar eventDate) {
		this.eventDate = eventDate;
	}
	public String getRectifySource() {
		return rectifySource;
	}
	public void setRectifySource(String rectifySource) {
		this.rectifySource = rectifySource;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public BigDecimal getPostageAmount() {
		return postageAmount;
	}
	public void setPostageAmount(BigDecimal postageAmount) {
		this.postageAmount = postageAmount;
	}
	public String getClientName() {
		return clientName;
	}
	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
	public XMLGregorianCalendar getPtsTransmissionDate() {
		return ptsTransmissionDate;
	}
	public void setPtsTransmissionDate(XMLGregorianCalendar ptsTransmissionDate) {
		this.ptsTransmissionDate = ptsTransmissionDate;
	}
	public String getMailerId() {
		return mailerId;
	}
	public void setMailerId(String mailerId) {
		this.mailerId = mailerId;
	}
	public String getMailClass() {
		return mailClass;
	}
	public void setMailClass(String mailClass) {
		this.mailClass = mailClass;
	}
}