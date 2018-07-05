package nz.co.gregs.minortask;

import nz.co.gregs.minortask.components.LoginComponent;
import nz.co.gregs.minortask.components.LoggedoutComponent;
import com.vaadin.annotations.PreserveOnRefresh;
import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.*;
import com.vaadin.ui.*;
import java.sql.SQLException;
import nz.co.gregs.dbvolution.exceptions.UnexpectedNumberOfRowsException;
import nz.co.gregs.minortask.components.BannerMenu;
import nz.co.gregs.minortask.components.FooterMenu;
import nz.co.gregs.minortask.components.SignupComponent;
import nz.co.gregs.minortask.components.TaskCreator;
import nz.co.gregs.minortask.components.TaskEditor;
import nz.co.gregs.minortask.datamodel.*;

/**
 * This UI is the application entry point. A UI may either represent a browser
 * window (or tab) or some part of an HTML page where a Vaadin application is
 * embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is
 * intended to be overridden to add component to the user interface and
 * initialize non-component functionality.
 */
@Theme("minortasktheme")
@PreserveOnRefresh
public class MinorTaskUI extends UI {

	private MinorTask minortask;


	@Override
	protected void init(VaadinRequest vaadinRequest) {


		this.minortask = new MinorTask(this);
		minortask.setupSession(vaadinRequest);

		if (minortask.notLoggedIn) {
			minortask.showLogin();
		} else {
			minortask.showTask(null);
		}
	}


	@WebServlet(urlPatterns = "/*", name = "MinorTaskUIServlet", asyncSupported = true)
	@VaadinServletConfiguration(ui = MinorTaskUI.class, productionMode = false)
	public static class MinorTaskUIServlet extends VaadinServlet {
	}

}
