package kr.pe.codda.common.io;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import kr.pe.codda.common.etc.StreamCharsetFamily;
import kr.pe.codda.common.exception.NoMoreDataPacketBufferException;

/**
 * 소켓 읽기를 통해 얻은 스트림을 다루기 위한 클래스. WARNING! 단위 테스트가 아닌 실 운영에서 putXXX 계열 쓰기 메소드를 호출하지 말것, 만약 호출되면 소켓으로 부터 얻지 않은 데이터가 스트림에 섞임. 
 *   
 * @author Won Jonghoon
 *
 */
public final class InputStreamResource extends StreamBuffer {
	private Object userDefObject = null;

	public InputStreamResource(StreamCharsetFamily streamCharsetFamily, int dataPacketBufferMaxCntPerMessage, WrapBufferPoolIF wrapBufferPool) {
		super(streamCharsetFamily, dataPacketBufferMaxCntPerMessage, wrapBufferPool);
	}
	
	/**
	 * 소켓 읽기를 통해 축적한 이진 데이터 스트림의 맨 앞쪽에서 부터 지정한 크기로 잘라 반환한다. 
	 * WARNING! 이 메소드는 {@link #read(SocketChannel)} 메소드를 통해 입력 스트림을 구성했다는 것을 전제로 하며 이 전제가 보장되지 않으면 정상 동작하지 않는다.
	 * {@link #read(SocketChannel)} 는 소켓으로 부터 데이터를 읽어와서 입력 스트림을 꾸린다. 하여 수신한 입력 스트림의 크기는 '위치 속성 값'(=postion) 이 되고
	 * '스트림을 담는 그릇인 랩 버퍼 배열'(=wrapBufferArray) 은 입력 스트림을 담은 만큼의 랩버퍼들을 갖게 된다.    
	 * 
	 * @param wantedSizeToCut 자르기 원하는 크기, 0 보다 커야 한다.
	 * @return 소켓 읽기를 통해 축적한 이진 데이터 스트림 맨 앞쪽에서 추출된 스트림을 갖는 '스트림 버퍼'
	 * @throws IllegalArgumentException
	 * @throws NoMoreDataPacketBufferException
	 * @throws IllegalStateException 실질적인 IO 를 통해야만 값이 설정되는 '마지막 버퍼 인덱스' 와 '위치 속성 값' 만큼 크기를 갖는 스트림이이 담겼을 경우 마지막 인덱스가 불일치한 경우 
	 */
	public StreamBuffer cutMessageInputStream(long wantedSizeToCut) throws IllegalArgumentException, NoMoreDataPacketBufferException, IllegalStateException {
		if (wantedSizeToCut <= 0) {
			String errorMessage = new StringBuilder()
					.append("the parameter wantedSizeToCut[")
					.append(wantedSizeToCut)
					.append("] is less than or equal to zero").toString();
			throw new IllegalArgumentException(errorMessage);
		}
				
		
		if (wantedSizeToCut > position) {
			String errorMessage = new StringBuilder()
					.append("the parameter wantedSizeToCut[")
					.append(wantedSizeToCut)
					.append("] is greater than the var position[")
					.append(position)
					.append("] which means the size of input streame").toString();
			throw new IllegalArgumentException(errorMessage);
		}		
		
		int remaingEndIndex = (int) ((position - 1) / dataPacketBufferSize);
		
		if (lastBufferIndex != remaingEndIndex) {
			/** 실질적인 IO 를 통해야만 값이 설정되는 '마지막 버퍼 인덱스' 와 '위치 속성 값' 만큼 크기를 갖는 스트림이이 담겼을 경우 마지막 인덱스가 불일치한 경우 */
			
			String errorMessage = new StringBuilder()
					.append("the var lastIndex[")
					.append(lastBufferIndex)
					.append("] is different from the var remaingEndIndex[")
					.append(remaingEndIndex)
					.append("]").toString();
			throw new IllegalStateException(errorMessage);
		}
		
		
		long cutEndPosition = wantedSizeToCut - 1;
		int cutEndPostionIndex = (int) ( cutEndPosition / dataPacketBufferSize);
		int cutEndPostionOffset = (int) (cutEndPosition % dataPacketBufferSize);
		
		// FIXME!
		/*
		log.log(Level.INFO, new StringBuilder().append("before::position=[").append(position)
				.append("], wantedSizeToCut=[")
				.append(wantedSizeToCut)
				.append("], cuttingEndIndex=[")
				.append(cuttingEndIndex)
				.append("], lastIndex=[")
				.append(lastIndex)
				.append("]").toString());
				*/		
		
		WrapBuffer targetWrapBufferArray[] = new WrapBuffer[cutEndPostionIndex + 1];
		ByteBuffer targetByteBufferArray[] = new ByteBuffer[cutEndPostionIndex + 1];
		
		for (int i = 0; i <= cutEndPostionIndex; i++) {
			targetWrapBufferArray[i] = wrapBufferArray[i];
			targetByteBufferArray[i] = byteBufferArray[i];
			
			wrapBufferArray[i] = null;
			byteBufferArray[i] = null;
		}

		/** 잘려진 크기에 맞도록 스트림이 담긴 마지막 버퍼의 limit 속성 설정 */
		targetByteBufferArray[cutEndPostionIndex].limit(cutEndPostionOffset + 1);
				
		if ((position - wantedSizeToCut) > 0) {
			int remaingStartIndex = (int) (wantedSizeToCut / dataPacketBufferSize);
			int remaingStartOffset = (int) (wantedSizeToCut % dataPacketBufferSize);
			
			int remaingEndOffset = (int) ((position - 1) % dataPacketBufferSize);
			
			// FIXME!
			/*
			log.log(Level.INFO, new StringBuilder().append("remaingStartIndex=[").append(remaingStartIndex)
					.append("], remaingStartOffset=[")
					.append(remaingStartOffset)
					.append("], remaingEndIndex=[")
					.append(remaingEndIndex)
					.append("], remaingEndOffset=[")
					.append(remaingEndOffset)
					.append("]").toString());
					*/
			
			
			ByteBuffer remaingStartByteBuffer = byteBufferArray[remaingStartIndex];
			if (null == remaingStartByteBuffer) {
				/** (1) 잘려진 스트림을 담고 있는 마지막 랩 버퍼에 잔존 데이터가 존재하는 경우 */
				
				// FIXME!
				// log.log(Level.INFO, "잘려진 스트림을 담고 있는 마지막 랩 버퍼에 잔존 데이터가 존재하는 경우");
				
				ByteBuffer dupCuttingEndByteBuffer = targetByteBufferArray[cutEndPostionIndex].duplicate();
				dupCuttingEndByteBuffer.limit(dupCuttingEndByteBuffer.capacity());
				dupCuttingEndByteBuffer.position(remaingStartOffset);
				
				ByteBuffer remaingEndByteBuffer = byteBufferArray[remaingEndIndex];
				
				/** (1-1) 잔존 데이터를 담고 있는 마지막 버퍼의 limit 속성 지정 하기 */				
				if (null == remaingEndByteBuffer) {
					/** (1-1-1) 잘려진 스트림을 담고 있는 마지막 랩 버퍼에 잔존 데이터가 모두 존재하는 경우 잔존 데이터를 담고 있는 마지막 버퍼는 잘려진 스트림을 담고 있는 마지막 랩 버퍼이다 */
					dupCuttingEndByteBuffer.limit(remaingEndOffset + 1);
					
					// FIXME!
					// log.log(Level.INFO, "after dupCuttingEndByteBuffer="+dupCuttingEndByteBuffer.toString());
					// log.log(Level.INFO, "after remaingEndByteBuffer is null");
				} else {
					/** (1-1-2) 잘려진 스트림을 담고 있는 마지막 랩 버퍼에 잔존 데이터 일부만 존재하는 경우 잔존 데이터를 담고 있는 마지막 버퍼는 잔존데이터를 담고 있는 스트림의 마지막 랩 버퍼이다 */
					remaingEndByteBuffer.limit(remaingEndOffset + 1);
					
					// FIXME!
					// log.log(Level.INFO, "after dupCuttingEndByteBuffer="+dupCuttingEndByteBuffer.toString());
					// log.log(Level.INFO, "after remaingEndByteBuffer="+remaingEndByteBuffer.toString());
				}	
				
				/** (1-2) 잔존 데이터 스트림을 첫 시작 위치로 옮기기 */
				
				/** (1-2-1) 신규 랩 버퍼를 받아 첫번째 버퍼로 등록하기 */
				wrapBufferArray[0] = wrapBufferPool.pollDataPacketBuffer();
				byteBufferArray[0] = wrapBufferArray[0].getByteBuffer();
				
				// FIXME!
				// log.log(Level.INFO, "byteBufferArray[0]="+byteBufferArray[0].toString());
				// log.log(Level.INFO, "dupCuttingEndByteBuffer="+dupCuttingEndByteBuffer);
				
				/** (1-2-2) 잘려진 스트림에서 잔존 데이터를 담고 있는 마지막 버퍼의 내용을 스트림의 첫번째 버퍼로 옮기기 */
				byteBufferArray[0].put(dupCuttingEndByteBuffer);
				
				/** (1-2-3) 스트림에 남아 있는 잔존 데이터를 앞쪽으로 옮기기   */
				int newLastIndex=0;
				for (int i=remaingStartIndex + 1; i <= remaingEndIndex; i++) {
					
					/** 잔존 데이터 담긴 랩 버퍼의 내용을 스트림 앞쪽으로 밀어 넣기 */
					if (byteBufferArray[i].remaining() > byteBufferArray[newLastIndex].remaining()) {
						int oldLimit = byteBufferArray[i].limit();
						
						byteBufferArray[i].limit(byteBufferArray[newLastIndex].remaining());
						
						byteBufferArray[newLastIndex].put(byteBufferArray[i]);
						
						byteBufferArray[i].limit(oldLimit);
					} else {
						byteBufferArray[newLastIndex].put(byteBufferArray[i]);
					}
					
					if (byteBufferArray[i].hasRemaining()) {
						/** 스트림 앞쪽으로 밀어 넣기를 하고도 잔존 데이터가 있는 경우 해당 랩 버퍼를 스트림에 합류 시킴 */
						byteBufferArray[newLastIndex].position(0);
						byteBufferArray[newLastIndex].limit(byteBufferArray[newLastIndex].capacity());
						newLastIndex++;
						
						byteBufferArray[i].compact();
						wrapBufferArray[newLastIndex] = wrapBufferArray[i];
						byteBufferArray[newLastIndex] = byteBufferArray[i]; 
					} else {
						/**  잔존 데이터 없는 경우 랩 버퍼 회수*/
						wrapBufferPool.putDataPacketBuffer(wrapBufferArray[i]);
					}			
				}
				
				byteBufferArray[newLastIndex].position(0);
				byteBufferArray[newLastIndex].limit(byteBufferArray[newLastIndex].capacity());
								
				for (int i=newLastIndex + 1; i <= lastBufferIndex; i++) {
					wrapBufferArray[i] = null;
					byteBufferArray[i] = null;
				}
				
				lastBufferIndex = newLastIndex;
			} else {
				/** (2) 잘려진 스트림을 담고 있는 랩 버퍼에는 잔존 데이터가 없는 경우 */
				// FIXME!
				// log.log(Level.INFO, "잘려진 스트림을 담고 있는 랩 버퍼에는 잔존 데이터가 없는 경우");

				int newLastIndex=-1;
				for (int i=remaingStartIndex; i <= remaingEndIndex; i++) {
					newLastIndex++;
					wrapBufferArray[newLastIndex] = wrapBufferArray[i];
					byteBufferArray[newLastIndex] = byteBufferArray[i];
					
					// FIXME!
					// log.log(Level.INFO, "newLastIndex="+newLastIndex);
					// log.log(Level.INFO, "i="+i);
				}
				
				for (int i=newLastIndex + 1; i <= lastBufferIndex; i++) {
					wrapBufferArray[i] = null;
					byteBufferArray[i] = null;
				}
				
				lastBufferIndex = newLastIndex;
			}
			
			
		} else {
			// FIXME!
			// log.log(Level.INFO, "스트림 전체를 자를 경우");
			
			/** 
			 * 읽은 모든 데이터를 자를 경우 읽은 데이터들이 있는 할당된 버퍼들은 모두 '스트림 버퍼' 에서  새로운 '스트립 버퍼'로 옮겨가면서 
			 * 삭제에 따른 '할당된 랩 버퍼의 마지막 인덱스' 값은 할당된 버퍼들이 없다를 뜻하는 -1 로 초기화된다 
			 * */
			lastBufferIndex = -1;
		}
		
		position -= wantedSizeToCut;
		// limit = capacity;
		
		// FIXME!
		// log.log(Level.INFO, new StringBuilder().append("after::position=[").append(position).append("]").toString());

		return new StreamBuffer(streamCharsetFamily, targetWrapBufferArray, targetByteBufferArray, wantedSizeToCut, wrapBufferPool);
	}

	/**
	 * 새로운 사용자 정의 객체를 저장한다.
	 * 
	 * @param newUserDefObject 새로운 사용자 정의 객체
	 */
	public void setUserDefObject(Object newUserDefObject) {
		this.userDefObject = newUserDefObject;
	}

	/**
	 * @return 사용자 정의 객체
	 */
	public Object getUserDefObject() {
		return userDefObject;
	}
	
}
