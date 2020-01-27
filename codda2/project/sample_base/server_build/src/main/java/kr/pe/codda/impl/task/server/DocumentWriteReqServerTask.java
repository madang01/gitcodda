/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kr.pe.codda.impl.task.server;

import static kr.pe.codda.jooq.tables.SbDocHistoryTb.SB_DOC_HISTORY_TB;
import static kr.pe.codda.jooq.tables.SbDocTb.SB_DOC_TB;
import static kr.pe.codda.jooq.tables.SbSeqTb.SB_SEQ_TB;

import java.sql.Timestamp;

import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.types.UInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.ServerTaskException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.DocumentWriteReq.DocumentWriteReq;
import kr.pe.codda.impl.message.DocumentWriteRes.DocumentWriteRes;
import kr.pe.codda.server.LoginManagerIF;
import kr.pe.codda.server.lib.DBTaskIF;
import kr.pe.codda.server.lib.DocumentStateType;
import kr.pe.codda.server.lib.PermissionType;
import kr.pe.codda.server.lib.RollbackServerTaskException;
import kr.pe.codda.server.lib.SequenceType;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;
import kr.pe.codda.server.lib.ValueChecker;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

/**
 * 문서 쓰기 요청 처리 담당 서버 비지니스 로직
 * 
 * @author Won Jonghoon
 *
 */
public class DocumentWriteReqServerTask extends AbstractServerTask implements DBTaskIF<DocumentWriteReq, DocumentWriteRes> {
	private Logger log = LoggerFactory.getLogger(DocumentWriteReqServerTask.class);

	/**
	 * 생성자
	 * @throws DynamicClassCallException 동적 호출 작업중 에러 발생시 던지는 예외
	 */
	public DocumentWriteReqServerTask() throws DynamicClassCallException {
		super();
	}
	

	@Override
	public void doTask(String projectName, LoginManagerIF personalLoginManager, ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws Exception {

		/*
		AbstractMessage outputMessage = doWork(ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME,
				(DocumentWriteReq) inputMessage);
		toLetterCarrier.addSyncOutputMessage(outputMessage);
		*/
		
		
		AbstractMessage outputMessage = ServerDBUtil.doDBWork(ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME, (DocumentWriteReq)inputMessage, this);
		
		toLetterCarrier.addSyncOutputMessage(outputMessage);
	}

	/**
	 * @param dbcpName dbcp 이름
	 * @param DocumentWriteReq 문서 쓰기 요청 메시지
	 * @return 문서 쓰기 응답 메시지
	 * @throws Exception 처리중 에러 발생시 던지는 예외
	 */
	public DocumentWriteRes doWork(String dbcpName, DocumentWriteReq documentWriteReq) throws Exception {
		/** FIXME! */
		log.info(documentWriteReq.toString());

		try {
			ValueChecker.checkValidUserID(documentWriteReq.getRequestedUserID());
			ValueChecker.checkValidIP(documentWriteReq.getIp());
			ValueChecker.checkValidFileName(documentWriteReq.getFileName());
			ValueChecker.checkValidSubject(documentWriteReq.getSubject());
			ValueChecker.checkValidContents(documentWriteReq.getContents());
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			throw new ServerTaskException(errorMessage);
		}

		DocumentWriteRes documentWriteRes = new DocumentWriteRes();

		final Timestamp registeredDate = new java.sql.Timestamp(System.currentTimeMillis());
		final UInteger lastDocumentSequence = UInteger.valueOf(0);

		ServerDBUtil.execute(dbcpName, (conn, dsl) -> {
			ServerDBUtil.checkUserAccessRights( dsl, log, "문서 조회 서비스", PermissionType.ADMIN,
					documentWriteReq.getRequestedUserID());

			/** 문서 쓰기와 수정을 위한 문서 번호 락 */
			Record1<UInteger> seqRecord = dsl.select(SB_SEQ_TB.SQ_VALUE).from(SB_SEQ_TB)
					.where(SB_SEQ_TB.SQ_ID.eq(SequenceType.DOCUMENT_NO.getSequenceID())).forUpdate().fetchOne();

			if (null == seqRecord) {
				String errorMessage = new StringBuilder("시퀀스 테이블에서 문서 번호 시퀀스[")
						.append(SequenceType.DOCUMENT_NO.getSequenceID()).append("]가 존재하지  않습니다").toString();
				throw new RollbackServerTaskException(errorMessage);
			}

			UInteger newDocumentNo = seqRecord.get(SB_SEQ_TB.SQ_VALUE);
			
			documentWriteRes.setDocumentNo(newDocumentNo.longValue());

			dsl.update(SB_SEQ_TB).set(SB_SEQ_TB.SQ_VALUE, SB_SEQ_TB.SQ_VALUE.add(1))
					.where(SB_SEQ_TB.SQ_ID.eq(SequenceType.DOCUMENT_NO.getSequenceID())).execute();

			dsl.insertInto(SB_DOC_TB).set(SB_DOC_TB.DOC_NO, newDocumentNo)
					.set(SB_DOC_TB.DOC_STATE, DocumentStateType.OK.getValue())
					.set(SB_DOC_TB.LAST_DOC_SQ, lastDocumentSequence).execute();

			dsl.insertInto(SB_DOC_HISTORY_TB).set(SB_DOC_HISTORY_TB.DOC_NO, newDocumentNo)
					.set(SB_DOC_HISTORY_TB.DOC_SQ, lastDocumentSequence)
					.set(SB_DOC_HISTORY_TB.FILE_NAME, documentWriteReq.getFileName())
					.set(SB_DOC_HISTORY_TB.SUBJECT, documentWriteReq.getSubject())
					.set(SB_DOC_HISTORY_TB.CONTENTS, documentWriteReq.getContents())
					.set(SB_DOC_HISTORY_TB.REG_DT, registeredDate).execute();
			
			conn.commit();
		});
		

		return documentWriteRes;
	}

	@Override
	public DocumentWriteRes doWork(final DSLContext dsl, final DocumentWriteReq documentWriteReq) throws Exception {
		/** FIXME! */
		log.info(documentWriteReq.toString());

		try {
			ValueChecker.checkValidUserID(documentWriteReq.getRequestedUserID());
			ValueChecker.checkValidIP(documentWriteReq.getIp());
			ValueChecker.checkValidFileName(documentWriteReq.getFileName());
			ValueChecker.checkValidSubject(documentWriteReq.getSubject());
			ValueChecker.checkValidContents(documentWriteReq.getContents());
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			throw new ServerTaskException(errorMessage);
		}

		DocumentWriteRes documentWriteRes = new DocumentWriteRes();

		final Timestamp registeredDate = new java.sql.Timestamp(System.currentTimeMillis());
		final UInteger lastDocumentSequence = UInteger.valueOf(0);

		
			ServerDBUtil.checkUserAccessRights( dsl, log, "문서 조회 서비스", PermissionType.ADMIN,
					documentWriteReq.getRequestedUserID());

			/** 문서 쓰기와 수정을 위한 문서 번호 락 */
			Record1<UInteger> seqRecord = dsl.select(SB_SEQ_TB.SQ_VALUE).from(SB_SEQ_TB)
					.where(SB_SEQ_TB.SQ_ID.eq(SequenceType.DOCUMENT_NO.getSequenceID())).forUpdate().fetchOne();

			if (null == seqRecord) {
				String errorMessage = new StringBuilder("시퀀스 테이블에서 문서 번호 시퀀스[")
						.append(SequenceType.DOCUMENT_NO.getSequenceID()).append("]가 존재하지  않습니다").toString();
				throw new RollbackServerTaskException(errorMessage);
			}

			UInteger newDocumentNo = seqRecord.get(SB_SEQ_TB.SQ_VALUE);
			
			documentWriteRes.setDocumentNo(newDocumentNo.longValue());

			dsl.update(SB_SEQ_TB).set(SB_SEQ_TB.SQ_VALUE, SB_SEQ_TB.SQ_VALUE.add(1))
					.where(SB_SEQ_TB.SQ_ID.eq(SequenceType.DOCUMENT_NO.getSequenceID())).execute();

			dsl.insertInto(SB_DOC_TB).set(SB_DOC_TB.DOC_NO, newDocumentNo)
					.set(SB_DOC_TB.DOC_STATE, DocumentStateType.OK.getValue())
					.set(SB_DOC_TB.LAST_DOC_SQ, lastDocumentSequence).execute();

			dsl.insertInto(SB_DOC_HISTORY_TB).set(SB_DOC_HISTORY_TB.DOC_NO, newDocumentNo)
					.set(SB_DOC_HISTORY_TB.DOC_SQ, lastDocumentSequence)
					.set(SB_DOC_HISTORY_TB.FILE_NAME, documentWriteReq.getFileName())
					.set(SB_DOC_HISTORY_TB.SUBJECT, documentWriteReq.getSubject())
					.set(SB_DOC_HISTORY_TB.CONTENTS, documentWriteReq.getContents())
					.set(SB_DOC_HISTORY_TB.REG_DT, registeredDate).execute();
		

		return documentWriteRes;
	}


}
