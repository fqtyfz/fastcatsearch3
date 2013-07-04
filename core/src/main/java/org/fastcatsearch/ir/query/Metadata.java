/*
 * Copyright 2013 Websquared, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.fastcatsearch.ir.query;

import java.util.Map;
import java.util.Set;

public class Metadata {
	private int version = 1;
	private int start;
	private int rows;
	private int option;
	private Map<String, String> userData;
	private String[] highlightTags;
	private String collectionName;
	
	public Metadata(){ }
			
	public String toString(){
		String str = version+";"+start+";"+rows+";"+option+";"+collectionName;
		if(highlightTags!= null){
			if(highlightTags[0] == null)
				highlightTags[0] ="";
			if(highlightTags[1] == null)
				highlightTags[1] ="";
			
			str += ";"+highlightTags[0]+","+highlightTags[1];
		}
		if(userData != null){
			String str2 = ";";
			Set<Map.Entry<String, String>> tmp = userData.entrySet();
			for(Map.Entry<String, String> e : tmp){
				str2 += e.getKey()+"="+e.getValue()+",";
			}
			str += str2;
		}
		
		return str;
	}
	public Metadata(int start, int rows){
		this.start = start;
		this.rows = rows;
	}
	public Metadata(int start, int rows, int option){
		this.start = start;
		this.rows = rows;
		this.option = option;
	}
	public Metadata(int start, int rows, int option, Map<String, String> userData){
		this.start = start;
		this.rows = rows;
		this.option = option;
		this.userData = userData;
	}
	public Metadata(int start, int rows, int option, Map<String, String> userData, String[] tags){
		this.start = start;
		this.rows = rows;
		this.option = option;
		this.userData = userData;
		this.highlightTags = tags;
	}
	public int version(){
		return version;
	}
	public int start(){
		return start;
	}
	public void setStart(int start){
		this.start = start;
	}
	public int rows(){
		return rows;
	}
	public void setRows(int rows){
		this.rows = rows;
	}
	public int option(){
		return option;
	}
	public void setOptions(int option){
		this.option = option;
	}
	public Map<String, String> userData(){
		return userData;
	}
	public void setUserData(Map<String, String> userData){
		this.userData = userData;
	}
	public String[] tags(){
		return highlightTags;
	}
	public void setTags(String[] tags){
		if(tags.length == 2){
			this.highlightTags = tags;
		}
	}
	public String collectionId(){
		return collectionName;
	}
	public void setCollectionName(String collectionName){
		this.collectionName = collectionName;
	}
}
