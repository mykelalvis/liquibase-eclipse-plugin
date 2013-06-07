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
