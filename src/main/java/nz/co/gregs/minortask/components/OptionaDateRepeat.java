/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.AbstractCompositeField;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.TextField;
import java.util.Objects;
import org.joda.time.Period;

/**
 *
 * @author gregorygraham
 */
public class OptionaDateRepeat extends AbstractCompositeField<Div, OptionaDateRepeat, Period> {

	private final Checkbox enabler = new Checkbox(false);
	private final TextField input = new TextField();
	private final String YEARS = "Years";
	private final String MONTHS = "Months";
	private final String WEEKS = "Weeks";
	private final String DAYS = "Days";
	private final ComboBox<String> selectDuration = new ComboBox<>();

	public OptionaDateRepeat(String label) {
		this((Period) null);

		setLabel(label);
	}

	public OptionaDateRepeat(Period defaultValue) {
		super(defaultValue);

		if (defaultValue == null) {
			input.setReadOnly(true);
			selectDuration.setReadOnly(true);
		}

		input.setWidth("4em");
		input.setPlaceholder("1");

		selectDuration.setItems(DAYS, WEEKS, MONTHS, YEARS);
		selectDuration.setValue(DAYS);

		this.getContent().addClassName("daterepeat-editor");
		enabler.addClassName("daterepeat-editor-enabler");
		input.addClassName("daterepeat-editor-input");

		selectDuration.setRequired(true);
		selectDuration.setAllowCustomValue(false);
		selectDuration.addClassName("daterepeat-editor-duration");

		enabler.addValueChangeListener((event) -> {
			toggleDateField(event);
			if (event.getValue()) {
				setModelValue(this.getCurrentPeriod(), false);
			} else {
				setModelValue(null, false);
			}
		});

		input.addValueChangeListener((event) -> {
			setModelValue(this.getCurrentPeriod(), false);
		});

		selectDuration.addValueChangeListener((event) -> {
			setModelValue(this.getCurrentPeriod(), false);
		});

		this.getContent().add(enabler, input, selectDuration);
	}

	public final void setLabel(String label) {
		enabler.setLabel(label);
	}

	@Override
	protected void setPresentationValue(Period newValue) {
		if (newValue != null) {
			enabler.setValue(Boolean.TRUE);
			if (newValue.getYears() > 0) {
				input.setValue(String.valueOf(newValue.getYears()));
				selectDuration.setValue(YEARS);
			} else if (newValue.getMonths() > 0) {
				input.setValue(String.valueOf(newValue.getMonths()));
				selectDuration.setValue(MONTHS);
			} else if (newValue.getWeeks() > 0) {
				input.setValue(String.valueOf(newValue.getWeeks()));
				selectDuration.setValue(WEEKS);
			} else {
				input.setValue(String.valueOf(newValue.getDays()));
				selectDuration.setValue(DAYS);
			}
		} else {
			enabler.setValue(Boolean.FALSE);
		}
	}

	private void toggleDateField(AbstractField.ComponentValueChangeEvent<Checkbox, Boolean> event) {
		input.setReadOnly(!event.getValue());
		selectDuration.setReadOnly(!event.getValue());
	}
	
	private Period getCurrentPeriod() {
		final String inputValue = input.getValue();
		if (Objects.equals(enabler.getValue(), Boolean.FALSE) || inputValue == null || inputValue.isEmpty()) {
			return null;
		} else {
			final Integer val = Integer.valueOf(inputValue);
			System.out.println("" + val);
			if (val != null) {
				int years = 0;
				int months = 0;
				int weeks = 0;
				int days = 0;
				switch (selectDuration.getValue()) {
					case YEARS: {
						years = val.intValue();
						break;
					}
					case MONTHS: {
						months = val.intValue();
						break;
					}
					case WEEKS: {
						weeks = val.intValue();
						break;
					}
					default: {
						days = val.intValue();
						break;
					}
				}
				final Period period = new Period(years, months, weeks, days, 0, 0, 0, 0);
				return period;
			} else {
				return null;
			}
		}
	}

}
