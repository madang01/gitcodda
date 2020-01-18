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

import static kr.pe.codda.jooq.tables.SbDocTb.SB_DOC_TB;
import static kr.pe.codda.jooq.tables.SbDocHistoryTb.SB_DOC_HISTORY_TB;

import java.sql.Timestamp;

import org.jooq.Record2;
import org.jooq.types.UInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.ServerTaskException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.DocumentModifyReq.DocumentModifyReq;
import kr.pe.codda.impl.message.DocumentModifyRes.DocumentModifyRes;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.server.LoginManagerIF;
import kr.pe.codda.server.lib.DocumentStateType;
import kr.pe.codda.server.lib.PermissionType;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;
import kr.pe.codda.server.lib.ValueChecker;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

/**
 * 문서 수정 요청 처리 담당 서버 비지니스 로직
 * 
 * @author Won Jonghoon
 *
 */
public class DocumentModifyReqServerTask extends AbstractServerTask {
	private Logger log = LoggerFactory.getLogger(DocumentWriteReqServerTask.class);

	/**
	 * 생성자
	 * @throws DynamicClassCallException 동적 호출 작업중 에러 발생시 던지는 예외
	 */
	public DocumentModifyReqServerTask() throws DynamicClassCallException {
		super();
	}

	/**
	 * 에러 내용을 담은 출력 메시지 송신
	 *  
	 * @param errorMessage 에러 내용
	 * @param toLetterCarrier 송신 메시지 배달자
	 * @param inputMessage 입력 메시지
	 * @throws InterruptedException 인터럽트 발생시 던지는 예외
	 */
	private void sendErrorOutputMessage(String errorMessage, ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws InterruptedException {
		log.warn("{}, inObj={}", errorMessage, inputMessage.toString());

		MessageResultRes messageResultRes = new MessageResultRes();
		messageResultRes.setTaskMessageID(inputMessage.getMessageID());
		messageResultRes.setIsSuccess(false);
		messageResultRes.setResultMessage(errorMessage);
		toLetterCarrier.addSyncOutputMessage(messageResultRes);
	}

	@Override
	public void doTask(String projectName, LoginManagerIF personalLoginManager, ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws Exception {
		try {
			AbstractMessage outputMessage = doWork(ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME,
					(DocumentModifyReq) inputMessage);
			toLetterCarrier.addSyncOutputMessage(outputMessage);
		} catch (ServerTaskException e) {
			String errorMessage = e.getMessage();
			log.warn("errmsg=={}, inObj={}", errorMessage, inputMessage.toString());

			sendErrorOutputMessage(errorMessage, toLetterCarrier, inputMessage);
			return;
		} catch (Exception e) {
			String errorMessage = new StringBuilder().append("unknwon errmsg=").append(e.getMessage())
					.append(", inObj=").append(inputMessage.toString()).toString();

			log.warn(errorMessage, e);

			sendErrorOutputMessage("문서 목록 조회가 실패하였습니다", toLetterCarrier, inputMessage);
			return;
		}
	}

	/**
	 * @param dbcpName dbcp 이름
	 * @param documentModifyReq 문서 수정 요청 메시지
	 * @return 문서 수정 응답 메시지
	 * @throws Exception 처리중 에러 발생시 던지는 예외
	 */
	public DocumentModifyRes doWork(String dbcpName, DocumentModifyReq documentModifyReq) throws Exception {
		/** FIXME! */
		log.info(documentModifyReq.toString());
		
		try {
			ValueChecker.checkValidUserID(documentModifyReq.getRequestedUserID());
			ValueChecker.checkValidIP(documentModifyReq.getIp());
			ValueChecker.checkValidFileName(documentModifyReq.getFileName());
			ValueChecker.checkValidSubject(documentModifyReq.getSubject());
			ValueChecker.checkValidContents(documentModifyReq.getContents());
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			throw new ServerTaskException(errorMessage);
		}
		
		final UInteger documentNo = UInteger.valueOf(documentModifyReq.getDocumentNo());
		
		DocumentModifyRes documentModifyRes= new DocumentModifyRes();
		
		final Timestamp registeredDate = new java.sql.Timestamp(System.currentTimeMillis());
		
		ServerDBUtil.execute(dbcpName, (conn, create) -> {
			ServerDBUtil.checkUserAccessRights(conn, create, log, "문서 수정 서비스", PermissionType.ADMIN,
					documentModifyReq.getRequestedUserID());		

			/** 수정할 문서에 락을 건다 */
			Record2<Byte, UInteger> documentRecord = create
			.select(SB_DOC_TB.DOC_STATE, SB_DOC_TB.LAST_DOC_SQ)
			.from(SB_DOC_TB)
			.where(SB_DOC_TB.DOC_NO.eq(documentNo))
			.forUpdate()
			.fetchOne();
			
			if (null == documentRecord) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}

				String errorMessage = new StringBuilder("수정할 문서[")
						.append(documentModifyReq.getDocumentNo()).append("]가 존재하지  않습니다").toString();
				throw new ServerTaskException(errorMessage);
			}
			
			byte documentStateTypeValue = documentRecord.get(SB_DOC_TB.DOC_STATE);
			
			final DocumentStateType documentSateType;
			try {
				documentSateType =  DocumentStateType.valueOf(documentStateTypeValue);
			} catch(IllegalArgumentException e) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}

				String errorMessage = new StringBuilder("수정할 문서[")
						.append(documentNo)
						.append("]의 상태 값[")
						.append(documentStateTypeValue).append("]에 잘못된 값이 들어가 있습니다").toString();
				throw new ServerTaskException(errorMessage);
			}
			
			if (! DocumentStateType.OK.equals(documentSateType)) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}

				String errorMessage = new StringBuilder("문서[")
						.append(documentNo)
						.append("]의 상태[")
						.append(documentStateTypeValue).append("]가 정상이 아닙니다").toString();
				throw new ServerTaskException(errorMessage);
			}
			
			UInteger lastDocumentSequence = documentRecord.get(SB_DOC_TB.LAST_DOC_SQ);
			
			/** 문서의 새로운 마지막 시퀀스 번호 = 문서의 마지막 시퀀스 번호 + 1 */
			long documentSequence = lastDocumentSequence.longValue() + 1;
			
			if (CommonStaticFinalVars.UNSIGNED_INTEGER_MAX == documentSequence) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}

				String errorMessage = new StringBuilder("문서[")
						.append(documentNo)
						.append("]는 최대 수정 횟수[unsigned integer max=")
						.append(documentSequence).append("]를 초과하여 수정할 수 없습니다").toString();
				throw new ServerTaskException(errorMessage);
			}

			UInteger newLastDocumentSequence = UInteger.valueOf(documentSequence);	
			
			documentModifyRes.setDocumentSeq(documentSequence);
			
			/** 문서의 새로운 마지막 시퀀스 번호로 갱신 */
			create.update(SB_DOC_TB).set(SB_DOC_TB.LAST_DOC_SQ, newLastDocumentSequence)
			.where(SB_DOC_TB.DOC_NO.eq(documentNo)).execute();

			/** '문서 이력 테이블'(=sb_doc_history_tb)에 '문서의 새로운 마지막 시퀀스 번호'를 갖는 문서 이력 레코드를 추가한다 */
			create.insertInto(SB_DOC_HISTORY_TB).set(SB_DOC_HISTORY_TB.DOC_NO, documentNo)
			.set(SB_DOC_HISTORY_TB.DOC_SQ, newLastDocumentSequence)
			.set(SB_DOC_HISTORY_TB.FILE_NAME, documentModifyReq.getFileName())
			.set(SB_DOC_HISTORY_TB.SUBJECT, documentModifyReq.getSubject())
			.set(SB_DOC_HISTORY_TB.CONTENTS, documentModifyReq.getContents())
			.set(SB_DOC_HISTORY_TB.REG_DT, registeredDate).execute();
			
			conn.commit();
		});
		
		documentModifyRes.setDocumentNo(documentModifyReq.getDocumentNo());
		
		return documentModifyRes;
	}
}
