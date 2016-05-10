package com.maikeapp.maikewatch.bean;
/**
 * 已登录用户信息
 * @author JLJ
 *
 */
public class User {
	//登录时获取
	private int Id;//编号
	private String LoginName;//登录名
	private String Mobile;//手机号
	private String Portraits;//头像
	private int Sex;//性别
	private int Height;//身高
	private int Weight;//体重
	private int SportsTarget;//个人目标
	private boolean isBindWatch;//是否绑定手表


	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}

	public String getLoginName() {
		return LoginName;
	}

	public void setLoginName(String loginName) {
		LoginName = loginName;
	}

	public String getMobile() {
		return Mobile;
	}

	public void setMobile(String mobile) {
		Mobile = mobile;
	}

	public String getPortraits() {
		return Portraits;
	}

	public void setPortraits(String portraits) {
		Portraits = portraits;
	}

	public int getSex() {
		return Sex;
	}

	public void setSex(int sex) {
		Sex = sex;
	}

	public int getHeight() {
		return Height;
	}

	public void setHeight(int height) {
		Height = height;
	}

	public int getWeight() {
		return Weight;
	}

	public void setWeight(int weight) {
		Weight = weight;
	}

	public int getSportsTarget() {
		return SportsTarget;
	}

	public void setSportsTarget(int sportsTarget) {
		SportsTarget = sportsTarget;
	}

	public boolean isBindWatch() {
		return isBindWatch;
	}

	public void setBindWatch(boolean bindWatch) {
		isBindWatch = bindWatch;
	}

	@Override
	public String toString() {
		return "User{" +
				"Id=" + Id +
				", LoginName='" + LoginName + '\'' +
				", Mobile='" + Mobile + '\'' +
				", Portraits='" + Portraits + '\'' +
				", Sex=" + Sex +
				", Height=" + Height +
				", Weight=" + Weight +
				", SportsTarget=" + SportsTarget +
				'}';
	}
}
