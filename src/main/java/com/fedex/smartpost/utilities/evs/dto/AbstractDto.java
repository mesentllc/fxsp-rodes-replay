package com.fedex.smartpost.utilities.evs.dto;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.Date;

/**
 * <h3>Property Of <font color="#3333ff">FedEx </font> <font
 * color="#00ff00">Ground </font> Package System, Inc. </b></h3>
 * <p>
 *
 * @author 2195024, <a
 *         href="mailto:scheekala@fedex.com">scheekala@fedex.com</a>
 * @since Jun 25, 2008, $Revision: 1.1 $
 * @version <code>$Id: AbstractDto.java,v 1.1 2005/11/23 19:39:57 scheekala Exp $ </code>
 * @version <code>$Revision: 1.1 $</code>
 */

public abstract class AbstractDto implements Serializable {

	private Integer dbSeqNumber;
	private String createdBy;
	private Date createdDt;
	private String updatedBy;
	private Date updatedDt;
	//private int operStatusCode; // variable to indicate the status code of the last called method

	public Integer getDbSeqNumber() {
		return dbSeqNumber;
	}

	public void setDbSeqNumber(Integer dbSeqNumber) {
		this.dbSeqNumber = dbSeqNumber;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	/**
	 * @return Returns the createdDt.
	 */
	public Date getCreatedDt() {
		return createdDt;
	}

	/**
	 * @param createdDt The createdDt to set.
	 */
	public void setCreatedDt(Date createdDt) {
		this.createdDt = createdDt;
	}

	/**
	 * @return Returns the updatedDt.
	 */
	public Date getUpdatedDt() {
		return updatedDt;
	}

	/**
	 * @param updatedDt The updatedDt to set.
	 */
	public void setUpdatedDt(Date updatedDt) {
		this.updatedDt = updatedDt;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("dbSeqNumber", dbSeqNumber)
										.append("createdBy", createdBy).append("createdDt", createdDt)
										.append("updatedBy", updatedBy).append("updatedDt", updatedDt)
										.toString();
	}
}
