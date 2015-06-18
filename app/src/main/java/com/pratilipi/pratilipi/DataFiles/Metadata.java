package com.pratilipi.pratilipi.DataFiles;

import java.io.Serializable;

public class Metadata implements Serializable{

	// private variables
    private static final long serialVersionUID = -7060210544600464481L;
    String _pid; // Pratilipi id
	String _title; // content title
	String _contentType; // PRATILIPI,IMAGE
	String _authorId;
	String _authorFullName;
    int _page_count;
	String _coverImageUrl;
	String _pageUrl;
    String _index;
	String _summary;
	long _ratingCount;
	long _starCount;

    public Metadata()
    {

    }

	public String get_pid() {
		return _pid;
	}

	public void set_pid(String _pid) {
		this._pid = _pid;
	}

	public String get_title() {
		return _title;
	}

	public void set_title(String _title) {
		this._title = _title;
	}
	
	public String get_contentType() {
		return _contentType;
	}

	public void set_contentType(String _contentType) {
		this._contentType = _contentType;
	}
	
	public String get_authorId() {
		return _authorId;
	}

	public void set_authorId(String _authorId) {
		this._authorId = _authorId;
	}

	public int get_page_count() {
		return _page_count;
	}

	public void set_page_count(int _page_count) {
		this._page_count = _page_count;
	}

    public String get_authorFullName() {
        return _authorFullName;
    }

    public void set_authorFullName(String _authorFullName) {
        this._authorFullName = _authorFullName;
    }

    public String get_index() {
        return _index;
    }

    public void set_index(String _index) {
        this._index = _index;
    }

    public String get_coverImageUrl() {
		return _coverImageUrl;
	}

	public void set_coverImageUrl(String _coverImageUrl) {
		this._coverImageUrl = _coverImageUrl;
	}

	public String get_pageUrl() {
		return _pageUrl;
	}

	public void set_pageUrl(String _pageUrl) {
		this._pageUrl = _pageUrl;
	}

	public String get_summary() {
		return _summary;
	}

	public void set_summary(String _summary) {
		this._summary = _summary;
	}

	public long get_ratingCount() {
		return _ratingCount;
	}

	public void set_ratingCount(long ratingCount) {
		this._ratingCount = ratingCount;
	}

	public long get_starCount() {
		return _starCount;
	}

	public void set_starCount(long starCount) {
		this._starCount = starCount;
	}
}
