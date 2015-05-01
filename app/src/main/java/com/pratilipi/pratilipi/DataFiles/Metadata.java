package com.pratilipi.pratilipi.DataFiles;

public class Metadata {

	// private variables
	String _pid; // Pratilipi id
	String _title; // content title
	String _contentType; // PRATILIPI,IMAGE
	String _authorId;
	String _authorFullName;
    String _ch_count;
	String _coverImageUrl;
	String _pageUrl;
    String _index;

	// constructor
	public Metadata(String _pid, String _title, String _contentType, String _authorId, String _authorFullName, String _ch_count, String _index, String _coverImageUrl, String _pageUrl) {
		this._pid = _pid;
		this._title = _title;
        this._contentType = _contentType;
        this._authorId = _authorId;
        this._authorFullName = _authorFullName;
        this._ch_count = _ch_count;
        this._index = _index;
        this._coverImageUrl = _coverImageUrl;
        this._pageUrl = _pageUrl;
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

	public String get_ch_count() {
		return _ch_count;
	}

	public void set_ch_count(String _ch_count) {
		this._ch_count = _ch_count;
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
}
