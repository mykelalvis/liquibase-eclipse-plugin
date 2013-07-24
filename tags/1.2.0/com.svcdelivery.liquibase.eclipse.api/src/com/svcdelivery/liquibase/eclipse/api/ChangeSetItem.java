/**
 * Copyright 2013 Nick Wilson
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
package com.svcdelivery.liquibase.eclipse.api;

import java.util.Date;

public class ChangeSetItem {

	private String changeLog;
	private String id;
	private String author;
	private String lastCheckSum;
	private Date dateExecuted;
	private String tag;
	private String execType;

	public String getChangeLog() {
		return changeLog;
	}

	public void setChangeLog(String changeLog) {
		this.changeLog = changeLog;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getLastCheckSum() {
		return lastCheckSum;
	}

	public void setLastCheckSum(String lastCheckSum) {
		this.lastCheckSum = lastCheckSum;
	}

	public Date getDateExecuted() {
		return dateExecuted;
	}

	public void setDateExecuted(Date dateExecuted) {
		this.dateExecuted = dateExecuted;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getExecType() {
		return execType;
	}

	public void setExecType(String execType) {
		this.execType = execType;
	}

	@Override
	public int hashCode() {
		return id.hashCode() + (changeLog == null ? 0 : changeLog.hashCode());
	}

	@Override
	public boolean equals(final Object obj) {
		boolean equal = false;
		if (obj instanceof ChangeSetItem) {
			ChangeSetItem other = (ChangeSetItem) obj;
			if (id.equals(other.id)) {
				if ((changeLog == null && other.changeLog == null)
						|| (changeLog != null && changeLog
								.equals(other.changeLog))) {
					equal = true;
				}
			}
		}
		return equal;
	}

	@Override
	public String toString() {
		return id + ":" + changeLog;
	}

}
