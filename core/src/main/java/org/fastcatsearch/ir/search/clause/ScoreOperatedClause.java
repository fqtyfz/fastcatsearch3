package org.fastcatsearch.ir.search.clause;

import org.fastcatsearch.ir.query.RankInfo;

/**
 * 검색된 posting에 대해서 hit수와 상관없이 동일한 점수(score)를 부여한다.
 * */
public class ScoreOperatedClause extends OperatedClause {
	
	private OperatedClause operatedClause;
	private int score;
	
	public ScoreOperatedClause(OperatedClause operatedClause, int score) {
		super("SCORE");
		this.operatedClause = operatedClause;
		this.score = score;
	}
	
	@Override
	protected boolean nextDoc(RankInfo docInfo) {
		if(operatedClause == null){
			return false;
		}
		
		boolean b = operatedClause.next(docInfo);
		if(b){
			docInfo.score(score);
		}
		return b;
	}

	@Override
	public void close() {
		if(operatedClause != null){
			operatedClause.close();
		}
	}

	@Override
	protected void initClause(boolean explain) {
		operatedClause.init(explanation != null ? explanation.createSubExplanation() : null);
	}

//	@Override
//	protected void initExplanation() {
//		if(operatedClause != null) {
//			operatedClause.setExplanation(explanation.createSub1());
//		}		
//	}

}