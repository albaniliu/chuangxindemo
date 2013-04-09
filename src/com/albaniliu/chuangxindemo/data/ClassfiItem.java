package com.albaniliu.chuangxindemo.data;

public class ClassfiItem {

	private int tagNo;
	private String tag = "";
	private boolean isHot;
	private String showText = "";
	private int seq;
	private String objUrl;
	public boolean bimapSet = false;

	public int getTagNo() {
		return tagNo;
	}
	
	public void setTagNo(int tagNo) {
		this.tagNo = tagNo;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getTag() {
		return this.tag;
	}

	public boolean isHot() {
		return isHot;
	}

	public void setHot(boolean isHot) {
		this.isHot = isHot;
	}

	public String getShowText() {
		return showText;
	}

	public void setShowText(String showText) {
		this.showText = showText;
	}

	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

	public String getObjUrl() {
		return objUrl;
	}

	public void setObjUrl(String imageUrl) {
		this.objUrl = imageUrl;
	}

}
