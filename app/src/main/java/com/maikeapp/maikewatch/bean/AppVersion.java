package com.maikeapp.maikewatch.bean;

public class AppVersion {
	private String path;
	private String fileSize;
	private int versionCode;
	private String versionName;
	
	public AppVersion(String path, String fileSize, int versionCode,
			String versionName) {
		super();
		this.path = path;
		this.fileSize = fileSize;
		this.versionCode = versionCode;
		this.versionName = versionName;
	}
	public AppVersion() {
		super();
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getFileSize() {
		return fileSize;
	}
	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}
	
	public int getVersionCode() {
		return versionCode;
	}
	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}
	public String getVersionName() {
		return versionName;
	}
	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}
	@Override
	public String toString() {
		return "AppVersion [path=" + path + ", fileSize=" + fileSize
				+ ", versionCode=" + versionCode + ", versionName="
				+ versionName + "]";
	}
	
}
