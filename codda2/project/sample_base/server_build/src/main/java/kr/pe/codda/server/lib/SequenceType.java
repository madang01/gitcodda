package kr.pe.codda.server.lib;

import org.jooq.types.UByte;

public enum SequenceType {
	MENU(UByte.valueOf(0), "메뉴에 사용되는 시퀀스"), 
	SITE_LOG_LOCK(UByte.valueOf(1), "부분 키인 날짜별 시퀀스를 얻기 위한 목적의 SB_SITE_LOG_TB 테이블 락에 사용되는 시퀀스"),
	SITE_UPLOAD_IMAGE_LOCK(UByte.valueOf(2), "부분 키인 날짜별 시퀀스를 얻기 위한 목적의 SB_UPLOAD_IMAGE_TB 테이블 락에 사용되는 시퀀스");

	private UByte sequenceID;
	private String sequenceTypeName;

	private SequenceType(UByte sequenceID, String sequenceTypeName) {
		this.sequenceID = sequenceID;
		this.sequenceTypeName = sequenceTypeName;
	}

	public UByte getSequenceID() {
		return sequenceID;
	}

	public String getName() {
		
		return sequenceTypeName;
	}

	public static SequenceType valueOf(UByte sequenceTypeID) {
		SequenceType[] boradTypes = SequenceType.values();
		for (SequenceType boardType : boradTypes) {
			if (boardType.getSequenceID().equals(sequenceTypeID)) {
				return boardType;
			}
		}

		throw new IllegalArgumentException(
				"the parameter sequenceTypeValue[" + sequenceTypeID + "] is a element of SequenceType set");
	}
}
