package com.svcdelivery.liquibase.eclipse.internal.ui.test;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SWTBotJunit4ClassRunner.class)
public class ContextMenuTest {
	
	private static SWTBot bot;
	
	@BeforeClass
	public static void beforeClass() {
		bot = new SWTBot();
	}
	
	@Test
	public void testContextMenuExists() {
		
	}
}
