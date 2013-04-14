package com.albaniliu.chuangxindemo.data;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

public class FInode {
	private FInode parent;
	private JSONArray dirs;
	private List<FInode> children;
	private int index;
	
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public FInode() {
		parent = null;
		children = new ArrayList<FInode>();
		dirs = new JSONArray();
		index = 0;
	}
	
	public FInode(FInode parent) {
		this.parent = parent;
		children = new ArrayList<FInode>();
		dirs = new JSONArray();
		index = 0;
	}
	
	public boolean isRoot() {
		return parent == null;
	}
	
	public FInode getParent() {
		return parent;
	}

	public void setParent(FInode parent) {
		this.parent = parent;
	}

	public JSONArray getDirs() {
		return dirs;
	}

	public void setDirs(JSONArray dirs) {
		this.dirs = dirs;
	}

	public List<FInode> getChildren() {
		return children;
	}

	public void setChildren(List<FInode> children) {
		this.children = children;
	}

	public boolean isLeaf() {
		return children.size() == 0;
	}
	
	public void addChild(FInode child) {
		children.add(child);
	}
}
