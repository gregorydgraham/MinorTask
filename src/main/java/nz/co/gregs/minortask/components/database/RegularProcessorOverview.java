/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.database;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Paragraph;
import nz.co.gregs.dbvolution.utility.RegularProcess;

/**
 *
 * @author gregorygraham
 */
@Tag("regular-processor-overview")
public class RegularProcessorOverview extends Div {

	/**
	 * Creates a new empty div.
	 */
	protected RegularProcessorOverview() {
		super();
	}

	RegularProcessorOverview(RegularProcess regProc) {
		super();
		Div output = new Div();
		final String lastResult = regProc.getLastResult();
		if (lastResult != null && !lastResult.isEmpty()) {
			String[] split = lastResult.split("\n");
			for (String string : split) {
				output.add(new Paragraph(string));
			}
		}
		final Div processing = new Div(
				output,
				new Div(new Label("Last Processed: " + regProc.getLastRuntime())),
				new Div(new Label("Next Processing: " + regProc.getNextRuntime())));
		processing.setTitle(regProc.getClass().getSimpleName());
		add(new Label(regProc.getClass().getSimpleName()), processing);
	}

}
