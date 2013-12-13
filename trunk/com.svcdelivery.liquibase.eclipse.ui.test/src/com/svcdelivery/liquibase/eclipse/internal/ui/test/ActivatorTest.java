package com.svcdelivery.liquibase.eclipse.internal.ui.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.osgi.framework.Version;

import com.svcdelivery.liquibase.eclipse.internal.ui.Activator;

/**
 * @author nick
 */
public class ActivatorTest {
	/**
	 * Test handling of active version.
	 */
	@Test
	public void testShouldActivate() {
		testShouldActivate(null, null, null, false);
		testShouldActivate("1.0.0", null, null, false);
		testShouldActivate(null, "1.0.0", null, false);
		testShouldActivate("1.0.0", "1.0.0", null, false);
		testShouldActivate("1.0.0", "1.0.0", "1.0.0", false);
		testShouldActivate(null, null, "1.0.0", true);
		testShouldActivate("1.0.0", null, "1.0.0", true);
		testShouldActivate(null, "1.0.0", "1.0.0", false);
		testShouldActivate("1.0.0", null, "1.0.1", true);
		testShouldActivate("1.0.1", null, "1.0.0", true);
		testShouldActivate(null, "1.0.0", "1.0.1", true);
		testShouldActivate(null, "1.0.1", "1.0.0", false);
		testShouldActivate("1.0.0", "1.0.1", "1.0.0", true);
		testShouldActivate("1.0.1", "1.0.0", "1.0.1", true);
		testShouldActivate("1.0.0", "1.0.1", "1.0.2", true);
		testShouldActivate("1.0.0", "1.0.2", "1.0.1", false);
		testShouldActivate("1.0.0", "1.0.0", "1.0.1", false);
	}

	/**
	 * @param defaultVersion
	 * @param activeVersion
	 * @param newVersion
	 * @param expected
	 */
	private void testShouldActivate(String defaultVersion,
			String activeVersion, String newVersion, boolean expected) {
		Activator activator = new Activator();
		boolean actual = activator.shouldActivate(toVersion(defaultVersion),
				toVersion(activeVersion), toVersion(newVersion));
		assertEquals(expected, actual);
	}

	private Version toVersion(String version) {
		Version v = null;
		if (version != null) {
			v = new Version(version);
		}
		return v;
	}
}
