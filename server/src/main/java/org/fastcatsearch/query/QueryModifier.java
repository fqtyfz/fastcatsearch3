package org.fastcatsearch.query;

import org.fastcatsearch.ir.query.Query;

public abstract class QueryModifier {
	public QueryModifier(){
		
	}
	
	public abstract Query modify(Query q);
}
