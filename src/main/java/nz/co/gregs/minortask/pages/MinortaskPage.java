/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.pages;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.server.InitialPageSettings;
import com.vaadin.flow.server.PageConfigurator;
import nz.co.gregs.minortask.MinorTask;

//@Push // used by the cluster adminitration page
public class MinortaskPage extends Div
		implements RouterLayout, PageConfigurator {

	@Override
	public void configurePage(InitialPageSettings settings) {
//		settings.addInlineFromFile(InitialPageSettings.Position.PREPEND,
//				"inline.js", InitialPageSettings.WrapMode.JAVASCRIPT);
//
//		settings.addMetaTag("og:title", "The Rock");
//		settings.addMetaTag("og:type", "video.movie");
//		settings.addMetaTag("og:url",
//				"http://www.imdb.com/title/tt0117500/");
//		settings.addMetaTag("og:image",
//				"http://ia.media-imdb.com/images/rock.jpg");

		settings.addMetaTag("apple-mobile-web-app-title", MinorTask.getApplicationName());
		settings.addMetaTag("apple-mobile-web-app-capable", "yes");

		settings.addLink("shortcut icon", "favicon.ico");
		settings.addLink("apple-touch-startup-image", "favicons/apple-icon-180x180.png");
		settings.addFavIcon("icon", "favicons/android-icon-192x192.png", "192x192");
		settings.addFavIcon("apple-touch-icon", "favicons/apple-icon-180x180.png", "180x180");
		settings.addFavIcon("apple-touch-icon", "favicons/apple-icon-152x152.png", "152x152");
		settings.addFavIcon("apple-touch-icon", "favicons/apple-icon-144x144.png", "144x144");
		settings.addFavIcon("apple-touch-icon", "favicons/apple-icon-120x120.png", "120x120");
		settings.addFavIcon("apple-touch-icon", "favicons/apple-icon-114x114.png", "114x114");
		settings.addFavIcon("apple-touch-icon", "favicons/apple-icon-76x76.png", "76x76");
		settings.addFavIcon("apple-touch-icon", "favicons/apple-icon-72x72.png", "72x72");
		settings.addFavIcon("apple-touch-icon", "favicons/apple-icon-60x60.png", "60x60");
		settings.addFavIcon("apple-touch-icon", "favicons/apple-icon-57x57.png", "57x57");
	}
}
