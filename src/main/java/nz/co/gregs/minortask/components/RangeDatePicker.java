/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.BlurNotifier;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.HtmlImport;
import java.util.Date;

/**
 *
 * @author gregorygraham
 */
@Tag("range-datepicker")
@HtmlImport("bower_components/range-datepicker/range-datepicker.html")
public class RangeDatePicker extends Component implements BlurNotifier<RangeDatePicker>{

	public void setNoRange(boolean value) {
		getElement().setProperty("noRange", value);
	}

	public void setNarrow(boolean value) {
		getElement().setProperty("narrow", value);
	}

	public void setForceNarrow(boolean value) {
		getElement().setProperty("forceNarrow", value);
	}

	public boolean isNoRange() {
		return getElement().getProperty("noRange", false);
	}

	public boolean isNarrow() {
		return getElement().getProperty("narrow", false);
	}

	public boolean isForceNarrow() {
		return getElement().getProperty("forceNarrow", false);
	}
	
	public void setWebLocale(String localeAs2LetterCode){
		getElement().setProperty("locale", localeAs2LetterCode);
	}
	
	public String getWebLocale(){
		String value = getElement().getProperty("locale", "en");
		return value;
	}
	
	public void setDateFrom(long unixTime){
		getElement().setProperty("dateFrom", unixTime);
	}
	
	public void setDateFrom(double unixTime){
		getElement().setProperty("dateFrom", unixTime);
	}
	
	public double getDateFrom(){
		double value = getElement().getProperty("dateFrom", new Date().getTime());
		return value;
	}
	
	public void setDateTo(long unixTime){
		getElement().setProperty("dateTo", unixTime);
	}
	
	public void setDateTo(double unixTime){
		getElement().setProperty("dateTo", unixTime);
	}
	
	public double getDateTo(){
		double value = getElement().getProperty("dateTo", new Date().getTime());
		return value;
	}

	public void setEnableYearChange(boolean value) {
		getElement().setProperty("enableYearChange", value);
	}

	public boolean isEnableYearChange() {
		return getElement().getProperty("enableYearChange", false);
	}
	
	public void setMonth(long monthAsIntegerStartingAtZero){
		getElement().setProperty("month", String.format("%02d", (monthAsIntegerStartingAtZero+1)));
	}
	
	public void setMonth(String monthAs2DigitNumberStartingAt01){
		getElement().setProperty("month", monthAs2DigitNumberStartingAt01);
	}
	
	public String getMonth(){
		@SuppressWarnings("deprecation")
		String value = getElement().getProperty("month", String.format("%02d", (new Date().getMonth()+1)));
		return value;
	}
	
	public void setYear(long year){
		getElement().setProperty("year", String.valueOf(year));
	}
	
	public void setYear(String year){
		getElement().setProperty("year", year);
	}
	
	public String getYear(){
		@SuppressWarnings("deprecation")
		String value = getElement().getProperty("year", ""+ (new Date().getYear()));
		return value;
	}
	
	public void setMin(long unixTime){
		getElement().setProperty("min", unixTime);
	}
	
	public void setMin(double unixTime){
		getElement().setProperty("min",unixTime);
	}
	
	public double getMin(){
		double value = getElement().getProperty("min", Long.MIN_VALUE);
		return value;
	}
	
	public void setMax(long unixTime){
		getElement().setProperty("max", unixTime);
	}
	
	public void setMax(double unixTime){
		getElement().setProperty("max", unixTime);
	}
	
	public double getMax(){
		double value = getElement().getProperty("max", Long.MAX_VALUE);
		return value;
	}
}
