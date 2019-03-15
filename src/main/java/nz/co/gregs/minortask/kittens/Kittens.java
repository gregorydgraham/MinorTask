/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.kittens;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.router.Route;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkAdapter;
import net.dean.jraw.http.OkHttpNetworkAdapter;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.SubredditSort;
import net.dean.jraw.oauth.Credentials;
import net.dean.jraw.oauth.OAuthHelper;
import net.dean.jraw.pagination.DefaultPaginator;
import nz.co.gregs.minortask.Globals;
import nz.co.gregs.minortask.pages.MinortaskPage;

/**
 *
 * @author gregorygraham
 */
@Route(value = "kittens")
@Tag("kittens")
@StyleSheet("frontend://styles/kittens.css")
public class Kittens extends MinortaskPage {

	public Kittens() {
		this.addClassName("kitten");
		add(getKittenImagesFromReddit());
	}

	protected static Image[] getKittenImages() {
		return new Image[]{
			new Image("https://images.unsplash.com/photo-1529778873920-4da4926a72c2?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1276&q=80",
			"kitten"),
			new Image("https://images.unsplash.com/photo-1533738363-b7f9aef128ce?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1275&q=80",
			"kitten")};
	}

	protected static Component[] getKittenImagesFromReddit() {
		UserAgent userAgent = new UserAgent("bot", "nz.co.gregs.minortask.kittens", "v0.1", "gregorydgraham");
// Create our credentials
		Credentials credentials = Credentials.script(
				getFromEnv("KittensUsername"),
				getFromEnv("KittensPassword"),
				getFromEnv("KittensClientID"),
				getFromEnv("KittensClientSecret"));

// This is what really sends HTTP requests
		NetworkAdapter adapter = new OkHttpNetworkAdapter(userAgent);

// Authenticate and get a RedditClient instance
		RedditClient redditClient = OAuthHelper.automatic(adapter, credentials);
		DefaultPaginator<Submission> kittensReddit = redditClient
				.subreddits("kittens", "Otters")//, "awww", "Otters", "puppies")
				.posts()
				.sorting(SubredditSort.NEW)
				.limit(100)
				.build();

		List<Component> images = new ArrayList<>();
		kittensReddit
				.next()
				.stream()
//				.peek((s) -> {
//					System.out.println("" + s.getTitle());
//					System.out.println("" + s.getUrl());
//				})
				.filter((s) -> (!s.isSelfPost() && (s.getUrl().endsWith(".jpg") || s.getUrl().endsWith(".png"))))
				.forEachOrdered((s) -> {
//					System.out.println("ADDING: " + s.getUrl());
					final Image image = new Image(s.getUrl(), s.getUrl());
					images.add(image);
				});
		return images.toArray(new Component[]{});
	}

	public static String getFromEnv(String envReference) {
		String envValue = "value not found";
		try {
			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup("java:comp/env");
			envValue = (String) envCtx.lookup(envReference);
			if (envValue == null || envValue.isEmpty()) {
				envValue = "";
			}
		} catch (NamingException ex) {
			Logger.getLogger(Globals.class.getName()).log(Level.SEVERE, null, ex);
		}
		return envValue;
	}

}
