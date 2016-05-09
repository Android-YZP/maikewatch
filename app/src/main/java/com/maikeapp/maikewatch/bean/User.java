package com.maikeapp.maikewatch.bean;
/**
 * 已登录用户信息
 * @author JLJ
 *
 */
public class User {
	//登录时获取
	private String account;
	private String password;
	private int id;
	private String userImg;
	private String username;
	private String mobile;
	private String verifyCode;

	
	//加入时间
	private String registerTime;
	
	private String realname;
	public User() {
		super();
	}
	
	public String getRealname() {
		return realname;
	}

	public void setRealname(String realname) {
		this.realname = realname;
	}


	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUserImg() {
		return userImg;
	}
	public void setUserImg(String userImg) {
		this.userImg = userImg;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getVerifyCode() {
		return verifyCode;
	}
	public void setVerifyCode(String verifyCode) {
		this.verifyCode = verifyCode;
	}

	public String getRegisterTime() {
		return registerTime;
	}

	public void setRegisterTime(String registerTime) {
		this.registerTime = registerTime;
	}

	public User(String account, String password, int id, String userImg,
			String username, String mobile, String verifyCode,String registerTime) {
		super();
		this.account = account;
		this.password = password;
		this.id = id;
		this.userImg = userImg;
		this.username = username;
		this.mobile = mobile;
		this.verifyCode = verifyCode;
		this.registerTime = registerTime;
	}


	
}
