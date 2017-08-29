package main.java.com.qlink.common.persistence;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionListener;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

@Entity
@RevisionEntity(RevisionListener.class)
@Table(name = "tl_revisions")
public class URevisionEntity {
	@Id
	@GeneratedValue
	@RevisionNumber
	@Column(name = "revisionId")
	private int id;
	@RevisionTimestamp
	@Column(name = "revisionTimestamp")
	private Date timestamp;
	/**
	 * @return the id
	 */
	public int getId() {
	    return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
	    this.id = id;
	}
	/**
	 * @return the timestamp
	 */
	public Date getTimestamp() {
	    return timestamp;
	}
	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(Date timestamp) {
	    this.timestamp = timestamp;
	}
}
