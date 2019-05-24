/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.kittens;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.Route;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
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
	
	private final KittenBox[] components;
//	static ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
//	ChangeKitten changeKitten = new ChangeKitten(this);

	public Kittens() {
		this.addClassName("kitten");
		components = getKittenImagesFromReddit();
		add(components);
//		this.addAttachListener((event) -> {
//			changeKitten.start(executor);
//			this.getUI().get().addDetachListener((detach) -> {
//				changeKitten.stop();
//			});
//		});
//		this.addDetachListener((event) -> {
//			changeKitten.stop();
//		});
	}
	
	protected static Image[] getKittenImages() {
		return new Image[]{
			new Image("https://images.unsplash.com/photo-1529778873920-4da4926a72c2?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1276&q=80",
			"kitten"),
			new Image("https://images.unsplash.com/photo-1533738363-b7f9aef128ce?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1275&q=80",
			"kitten")};
	}
	
	public static KittenBox[] getKittenImagesFromReddit() {
		List<KittenBox> boxes = new ArrayList<>();
		
		UserAgent userAgent = new UserAgent("bot", "nz.co.gregs.minortask.kittens", "v0.1", "gregorydgraham");
// Create our credentials
		Credentials credentials = Credentials.script(
				getFromEnv("KittensUsername"),
				getFromEnv("KittensPassword"),
				getFromEnv("KittensClientID"),
				getFromEnv("KittensClientSecret"));
		
		try {
// This is what really sends HTTP requests
			NetworkAdapter adapter = new OkHttpNetworkAdapter(userAgent);

// Authenticate and get a RedditClient instance
			RedditClient redditClient = OAuthHelper.automatic(adapter, credentials);
			DefaultPaginator<Submission> kittensReddit = redditClient
					.subreddits("kittens", "Otters", "corgi")//, "awww", "Otters", "puppies")
					.posts()
					.sorting(SubredditSort.HOT)
					.limit(100)
					.build();
			
			List<Image> images = new ArrayList<>();
			kittensReddit
					.next()
					.stream()
					.filter((s) -> (!s.isSelfPost() && (s.getUrl().endsWith(".jpg") || s.getUrl().endsWith(".png"))))
					.forEachOrdered((s) -> {
						final Image image = new Image(s.getUrl(), s.getUrl());
						images.add(image);
					});
			boxes = images
					.stream()
					.map((s) -> new KittenBox(s))
					.collect(Collectors.toList());
		} catch (Exception ex) {
			Globals.error("Getting Kittens", ex);
		}
		return boxes.toArray(new KittenBox[]{});
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
	
	private static class ChangeKitten implements Runnable {

//		private final UI ui;
		private final Kittens view;
		private boolean keepGoing = true;
		private ScheduledExecutorService executor;
		private ScheduledFuture<?> schedule;
		
		public ChangeKitten(Kittens view) {
//			this.ui = ui;
			this.view = view;
			
		}
		
		@Override
		public void run() {
			//only run once as we're going to use an executor
			if (keepGoing()) {
				view.getUI().get().access(() -> {
					// Do the changes to the page
					System.out.println("CHANGE KITTEN " + (new Date()));
					KittenBox[] comps = view.components;
					int random = new Double(Math.random() * comps.length).intValue();
					KittenBox target = comps[random];
					if (target != null) {
						// do something
					}
				});
			} else {
				this.stop();
			}
		}
		
		public synchronized void start(ScheduledExecutorService executor) {
			if (schedule != null) {
				this.stop();
			}
			keepGoing = true;
			this.executor = executor;
			this.schedule = this.executor.scheduleWithFixedDelay(this, 10, 10, TimeUnit.SECONDS);
		}
		
		private synchronized void stop() {
			keepGoing = false;
//			remove from the executor
			if (schedule != null) {
				schedule.cancel(false);
				schedule = null;
			}
		}
		
		private boolean keepGoing() {
			return keepGoing && view.getUI().isPresent();
		}
	}
	
	@Tag("kittenbox")
	public static class KittenBox extends Span {
		
		private Image frontImage;
		
		public KittenBox(Image front) {
			makeFrontAndBack(front);
			add(front);
			addClassName("kittenbox");
		}
		
		private void makeFrontAndBack(Image front) {
			frontImage = front;
			frontImage.getElement().getClassList().remove("back");
			frontImage.getElement().getClassList().add("front");
		}
	}
}
