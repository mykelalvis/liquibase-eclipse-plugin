/**
 * Copyright 2012 Nick Wilson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.svcdelivery.liquibase.eclipse.internal.ui;

/**
 * @author nick
 */
public enum LiquibaseResultStatus {
	/**
	 * Script is currently running.
	 */
	RUNNING("run_exc.gif"),
	/**
	 * Script ran successfully.
	 */
	SUCCESS("success.gif"),
	/**
	 * Script failed.
	 */
	FAILURE("fail.gif");

	/**
	 * The result image file name.
	 */
	private String fileName;

	/**
	 * @param imageFile
	 *            The result image file name.
	 */
	private LiquibaseResultStatus(final String imageFile) {
		fileName = imageFile;
	}

	/**
	 * @return The result image file name.
	 */
	public String getFileName() {
		return fileName;
	}
}
