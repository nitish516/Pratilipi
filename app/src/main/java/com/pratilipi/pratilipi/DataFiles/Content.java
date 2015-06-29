package com.pratilipi.pratilipi.DataFiles;

public class Content {

	// private variables
	String _pid;
	int _ch_no;
	String _content;
	byte[] _img;
	String _type;

	// Empty constructor
	public Content() {

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

	public byte[] get_img() {
		return _img;
	}

	public void set_img(byte[] _img) {
		this._img = _img;
	}

	public String get_type() {
		return _type;
	}

	public void set_type(String _type) {
		this._type = _type;
	}


}
