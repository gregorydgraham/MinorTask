/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.pages;

import nz.co.gregs.minortask.components.tasklists.IdeasList;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.router.Route;

@Route("ideas")
public class IdeasPage extends AuthorisedPage {

	@Override
	public Component getInternalComponent() {
		return new IdeasList();
	}

	@Override
	public String getPageTitle() {
		return "MinorTask: Ideas";
	}
}
