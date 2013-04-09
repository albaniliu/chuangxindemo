package com.albaniliu.chuangxindemo.data;

/**
 * 
 * ImageDetailInfoModel 图片详细信息结构
 * 
 * 
 * 
 * @author neusoft
 */

import java.io.Serializable;

@SuppressWarnings("serial")
public class ImageDetailInfoModel implements Serializable {

	public String strThumUid = null; // 缩略图uid
	public String strLargeUid = null; // 大图uid
	public String strSourceUid = null; // 原图uid

	public String strThumSavePath = null; // 缩略图存储路径
	public String strLargeSavePath = null; // 大图存储路径
	public String strSourceSavePath = null; // 原图存储路径

	public String strFromURL = null; // 来源网页

	public ImageDetailInfoModel() {

	}

}
