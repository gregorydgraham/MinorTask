/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.pages;

import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import nz.co.gregs.minortask.MinorTaskTemplate;
import nz.co.gregs.minortask.components.FooterMenu;
import nz.co.gregs.minortask.components.LoggedoutComponent;
import nz.co.gregs.minortask.components.PublicBannerMenu;


/**
 *
 * @author gregorygraham
 */
@HtmlImport("styles/shared-styles.html")
@Route("bye")
public class LoggedOutPage extends VerticalLayout{

	public LoggedOutPage() {
		final MinorTaskTemplate minorTaskTemplate = new MinorTaskTemplate();
		add(minorTaskTemplate);
		add(new PublicBannerMenu());
		add(new LoggedoutComponent());
		add(new FooterMenu());
	}
	
}
