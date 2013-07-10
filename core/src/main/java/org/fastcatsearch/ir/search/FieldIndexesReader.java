package org.fastcatsearch.ir.search;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.fastcatsearch.ir.settings.FieldIndexSetting;
import org.fastcatsearch.ir.settings.Schema;

public class FieldIndexesReader extends SelectableIndexesReader<FieldIndexReader, FieldIndexSetting> {
	
	public FieldIndexesReader(){ }
	
	public FieldIndexesReader(Schema schema, File dir) throws IOException{
		indexSettingList = schema.schemaSetting().getFieldIndexSettingList();
		int indexCount = indexSettingList.size();
		
		//색인파일열기.
		readerList = new ArrayList<FieldIndexReader>(indexCount);
		for (int i = 0; i < indexCount; i++) {
			FieldIndexSetting setting = indexSettingList.get(i);
			readerList.add(new FieldIndexReader(setting, schema.fieldSettingMap(), dir));
		}
		
	}
	
	@Override
	public FieldIndexesReader clone(){
		FieldIndexesReader reader = new FieldIndexesReader();
		reader.indexSettingList = indexSettingList;
    	reader.readerList = new ArrayList<FieldIndexReader>(readerList.size());
    	for (FieldIndexReader r : readerList) {
    		reader.readerList.add(r.clone());
		}
    	
    	return reader;
    }
	
	@Override
	protected FieldIndexReader cloneReader(int sequence) {
		return readerList.get(sequence).clone();
	}

}