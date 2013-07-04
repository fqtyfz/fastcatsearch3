package org.fastcatsearch.ir.index;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Random;

import org.fastcatsearch.ir.common.IRException;
import org.fastcatsearch.ir.io.ByteArrayInput;
import org.fastcatsearch.ir.io.FastByteBuffer;
import org.fastcatsearch.ir.io.Input;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PostingBufferWithPositionTest {
	private Logger logger = LoggerFactory.getLogger(PostingBufferWithPositionTest.class);

	@Test
	public void testSmall() throws IRException, IOException{
		PostingBufferWithPosition postingBuffer = new PostingBufferWithPosition();
		postingBuffer.addOne(1, 1);
		postingBuffer.addOne(1, 3);
		postingBuffer.addOne(1, 10);
		postingBuffer.addOne(1, 12);
		postingBuffer.addOne(1, 15);
		postingBuffer.finish();
		
		FastByteBuffer buffer = postingBuffer.buffer();
//		logger.debug(">posting buffer size= {}", buffer.remaining());
		
		Input postingInput = new ByteArrayInput(buffer.array(), buffer.pos(), buffer.limit());
		int count = postingInput.readInt();
		int lastDocNo = postingInput.readInt();
		assertEquals(1, count);
		assertEquals(1, lastDocNo);
		
		assertEquals(1,postingInput.readVariableByte());
		assertEquals(5,postingInput.readVariableByte());
		int tfd = 1;
		assertEquals(1,postingInput.readVariableByte());
		assertEquals(3 - 1 - 1,postingInput.readVariableByte());
		assertEquals(10 - 3 - 1,postingInput.readVariableByte());
		assertEquals(12 - 10 - 1,postingInput.readVariableByte());
		assertEquals(15 - 12 - 1,postingInput.readVariableByte());
				
//		System.out.println(postingInput.readVariableByte());
//		System.out.println(postingInput.readVariableByte());
//		System.out.println(postingInput.readVariableByte());
//		System.out.println(postingInput.readVariableByte());
//		System.out.println(postingInput.readVariableByte());
//		System.out.println(postingInput.readVariableByte());
//		System.out.println(postingInput.readVariableByte());
	}
	
	@Test
	public void testFull() throws IRException, IOException {
		
		PostingBufferWithPosition postingBuffer = new PostingBufferWithPosition();
		Random r = new Random(System.currentTimeMillis());
		
		int COUNT = 100000;
		int[] actualDocs = new int[COUNT];
		int[] actualTf = new int[COUNT];
		int[][] actualPositions = new int[COUNT][];
		
		int lastDoc = -1;
		int lastPosition = 0;
		int freq = 0;
		int realSize = -1;
		for (int i = 0; i < COUNT; i++) {
			int docNo = 0;
			if(i == 0){
				docNo = r.nextInt(5);
			}else{
				docNo = lastDoc + r.nextInt(5);
			}
			
			int position = 0;
			if(docNo == lastDoc){
				position = lastPosition + r.nextInt(10) + 1;
//				logger.debug("2 >> {}", realSize);
				freq = actualTf[realSize]++;
				actualPositions[realSize][freq] = position;
				lastPosition = position;
			}else{
				realSize++;
				position = r.nextInt(10);
//				logger.debug("1 >> {}", realSize);
				actualDocs[realSize] = docNo;
				freq = actualTf[realSize]++;
				actualPositions[realSize] = new int[256];
				actualPositions[realSize][freq] = position;
			}
//			logger.debug(">> {}, {}", docNo, position);
			postingBuffer.addOne(docNo, position);
			lastDoc = docNo;
		}
		postingBuffer.finish();
		
		FastByteBuffer buffer = postingBuffer.buffer();
//		logger.debug(">posting buffer size= {}", buffer.remaining());
		
		Input postingInput = new ByteArrayInput(buffer.array(), buffer.pos(), buffer.limit());
		
		int count = postingInput.readInt();
		int lastDocNo = postingInput.readInt();
		logger.debug("========= {}, {} =========", count, lastDocNo);
		int[] docs = new int[count];
		int[] tf = new int[count];
		int[][] positions = new int[count][];
		
		int n = 0;

		int docId = -1;
		int prevId = -1;
		
//		while(true){
//			logger.debug("> {}", postingInput.readVariableByte());
//		}
		for (int i = 0; i < count; i++) {
			if (prevId >= 0) {
				docId = postingInput.readVariableByte() + prevId + 1;
			} else {
				docId = postingInput.readVariableByte();
			}
			docs[n] = docId;

			tf[n] = postingInput.readVariableByte();
			
			assertEquals(actualDocs[n], docs[n]);
			assertEquals(actualTf[n], tf[n]);
			
			positions[n] = new int[tf[n]];
			
			int lastPos = -1;
//			logger.debug("## {}:{} / {}", docs[n], tf[n]);
			for (int j = 0; j < tf[n]; j++) {
				positions[n][j] = postingInput.readVariableByte();
				if(j > 0){
					positions[n][j] += (lastPos + 1);
				}
//				logger.debug("## {}:{}", docs[n], positions[n][j]);
				
				assertEquals(actualPositions[n][j], positions[n][j]);
				lastPos = positions[n][j];
			}

			prevId = docId;

			n++;
		}
	}

}
