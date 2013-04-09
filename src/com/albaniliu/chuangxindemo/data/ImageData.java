package com.albaniliu.chuangxindemo.data;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ImageData implements Serializable {

	public ImageData() {

	}

	private int x;
	private int y;

	private int width;
	private int height;

	private String fromUrl; // 来源网页

	private String objUrl; // 原图url

	public String contsign;

	public int fileSize; // 图片大小

	public float scale; // 图片缩放比例

	public boolean isLoadLargePic = false; // 是否已加载大图

	public boolean isStartLoadLargePic = false; // 是否已加载大图

	public String getFromUrl() {
		return fromUrl;
	}

	public void setFromUrl(String fromUrl) {
		this.fromUrl = fromUrl;
	}

	public String getObjUrl() {
		return objUrl;
	}

	public void setObjUrl(String objUrl) {
		this.objUrl = objUrl;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

}