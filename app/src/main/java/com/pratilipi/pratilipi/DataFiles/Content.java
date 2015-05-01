package com.pratilipi.pratilipi.DataFiles;

public class Content {

	// private variables
	String _pid;
	int _ch_no;
	String _content;

	// Empty constructor
	public Content() {

	}

	// constructor
	public Content(String _pid, int _ch_no, String _content) {
        this._pid = _pid;
        this._ch_no = _ch_no;
        this._content = _content;
    }

	public String get_pid() {
		return this._pid;
	}

	public void set_pid(String id) {
		this._pid = id;
	}

	public int get_ch_no() {
		return _ch_no;
	}

	public void set_ch_no(int _ch_no) {
		this._ch_no = _ch_no;
	}

	public String get_content() {
		return _content;
	}

	public void set_content(String _content) {
		this._content = _content;
	}

}
