/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.datamodel;

import java.util.Date;
import nz.co.gregs.dbvolution.DBRow;
import nz.co.gregs.dbvolution.annotations.AutoFillDuringQueryIfPossible;
import nz.co.gregs.dbvolution.annotations.DBAutoIncrement;
import nz.co.gregs.dbvolution.annotations.DBColumn;
import nz.co.gregs.dbvolution.annotations.DBForeignKey;
import nz.co.gregs.dbvolution.annotations.DBPrimaryKey;
import nz.co.gregs.dbvolution.annotations.DBRequiredTable;
import nz.co.gregs.dbvolution.datatypes.DBDate;
import nz.co.gregs.dbvolution.datatypes.DBInteger;
import nz.co.gregs.dbvolution.datatypes.DBLargeBinary;
import nz.co.gregs.dbvolution.datatypes.DBPasswordHash;
import nz.co.gregs.dbvolution.datatypes.DBStringTrimmed;
import nz.co.gregs.minortask.components.upload.Document;

/**
 *
 * @author gregorygraham
 */
@DBRequiredTable
public class User extends DBRow {

	@DBColumn
	@DBPrimaryKey
	@DBAutoIncrement
	private final DBInteger userID = new DBInteger();

	@DBColumn
	private final DBStringTrimmed username = new DBStringTrimmed();

	@DBColumn
	private final DBStringTrimmed email = new DBStringTrimmed();
	
	@DBColumn
	private final DBStringTrimmed blurb = new DBStringTrimmed();

	@DBColumn
	private final DBPasswordHash password = new DBPasswordHash();

	@DBColumn
	private final DBDate signupDate = new DBDate();

	@DBColumn
	private final DBDate lastLoginDate = new DBDate();

	@DBColumn
	@DBForeignKey(Document.class)
	private DBInteger profileImageID = new DBInteger();
	
	@AutoFillDuringQueryIfPossible
	public Document profileImage;

	/**
	 * @return the userID
	 */
	public DBInteger queryUserID() {
		return userID;
	}

	/**
	 * @return the username
	 */
	public DBStringTrimmed queryUsername() {
		return username;
	}

	/**
	 * @return the email
	 */
	public DBStringTrimmed queryEmail() {
		return email;
	}

	/**
	 * @return the password
	 */
	public DBPasswordHash queryPassword() {
		return password;
	}

	/**
	 * @return the signupDate
	 */
	public DBDate querySignupDate() {
		return signupDate;
	}

	/**
	 * @return the lastLoginDate
	 */
	public DBDate queryLastLoginDate() {
		return lastLoginDate;
	}

	/**
	 * @return the userID
	 */
	public Long getUserID() {
		return userID.getValue();
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username.getValue();
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email.getValue();
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password.getValue();
	}

	/**
	 * @return the signupDate
	 */
	public Date getSignupDate() {
		return signupDate.getValue();
	}

	/**
	 * @return the lastLoginDate
	 */
	public Date getLastLoginDate() {
		return lastLoginDate.getValue();
	}

	/**
	 * @param userID the userID to set
	 */
	public void setUserID(Long userID) {
		this.userID.setValue(userID);
	}

	/**
	 * @param userID the userID to set
	 */
	public void setUserID(Integer userID) {
		this.userID.setValue(userID);
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username.setValue(username);
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email.setValue(email);
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password.setValue(password);
	}

	/**
	 * @param signupDate the signupDate to set
	 */
	public void setSignupDate(Date signupDate) {
		this.signupDate.setValue(signupDate);
	}

	/**
	 * @param lastLoginDate the lastLoginDate to set
	 */
	public void setLastLoginDate(Date lastLoginDate) {
		this.lastLoginDate.setValue(lastLoginDate);
	}

	/**
	 * @return the blurb
	 */
	public DBStringTrimmed getBlurb() {
		return blurb;
	}

	/**
	 * @param blurb the blurb to set
	 */
	public void setBlurb(String blurb) {
		this.blurb.setValue(blurb);
	}

	/**
	 * @return 
	 */
	public DBStringTrimmed queryBlurb() {
		return blurb;
	}

	/**
	 * @return the profileImageID
	 */
	public DBInteger queryProfileImageID() {
		return profileImageID;
	}

	/**
	 * @return the profileImageID
	 */
	public Long getProfileImageID() {
		return profileImageID.getValue();
	}

	/**
	 * @param profileImageID the profileImageID to set
	 */
	public void setProfileImageID(Long profileImageID) {
		this.profileImageID.setValue(profileImageID);
	}

}
