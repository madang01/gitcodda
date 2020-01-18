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

import java.sql.Timestamp;
import java.util.ArrayList;

import org.jooq.Record;
import org.jooq.Record2;
import org.jooq.Record5;
import org.jooq.Result;
import org.jooq.types.UInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.ServerTaskException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.DocumentHistoryReq.DocumentHistoryReq;
import kr.pe.codda.impl.message.DocumentHistoryRes.DocumentHistoryRes;
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
 * 개별 문서 이력 조회 요청 처리 담당 서버 비지니스 로직
 * @author Won Jonghoon
 *
 */
public class DocumentHistoryReqServerTask extends AbstractServerTask {
	private Logger log = LoggerFactory.getLogger(DocumentDeleteReqServerTask.class);

	/**
	 * 생성자
	 * @throws DynamicClassCallException 동적 호출 작업중 에러 발생시 던지는 예외
	 */
	public DocumentHistoryReqServerTask() throws DynamicClassCallException {
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
					(DocumentHistoryReq) inputMessage);
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
	 * @param documentDeleteReq 개별 문서 이력 조회 요청 메시지
	 * @return 개별 문서 이력 조회 응답 메시지
	 * @throws Exception 처리중 에러 발생시 던지는 예외
	 */
	public DocumentHistoryRes doWork(String dbcpName, DocumentHistoryReq documentHistoryReq) throws Exception {
		/** FIXME! */
		log.info(documentHistoryReq.toString());
		
		final int pageNo = documentHistoryReq.getPageNo();
		final int pageSize = documentHistoryReq.getPageSize();	
		
		try {
			ValueChecker.checkValidUserID(documentHistoryReq.getRequestedUserID());
			ValueChecker.checkValidIP(documentHistoryReq.getIp());
			
			ValueChecker.checkValidPageNoAndPageSize(pageNo, pageSize);
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			throw new ServerTaskException(errorMessage);
		}
		
		
		final int offset = (pageNo - 1) * pageSize;
		final UInteger documentNo = UInteger.valueOf(documentHistoryReq.getDocumentNo());
		final java.util.List<DocumentHistoryRes.Document> documentList = new ArrayList<DocumentHistoryRes.Document>();
		final DocumentHistoryRes documentHistoryRes = new DocumentHistoryRes();
		
		ServerDBUtil.execute(dbcpName, (conn, create) -> {
			ServerDBUtil.checkUserAccessRights(conn, create, log, "개별 문서 이력 조회 서비스", PermissionType.ADMIN,
					documentHistoryReq.getRequestedUserID());
			
			Record2<Byte, UInteger> documentRecord = create
					.select(SB_DOC_TB.DOC_STATE, SB_DOC_TB.LAST_DOC_SQ)
					.from(SB_DOC_TB)
					.where(SB_DOC_TB.DOC_NO.eq(documentNo))
					.fetchOne();
					
					if (null == documentRecord) {
						try {
							conn.rollback();
						} catch (Exception e) {
							log.warn("fail to rollback");
						}

						String errorMessage = new StringBuilder("이력을 보고자 하는 문서[")
								.append(documentHistoryReq.getDocumentNo()).append("]가 존재하지  않습니다").toString();
						throw new ServerTaskException(errorMessage);
					}			
					
					byte documentStateTypeValue = documentRecord.get(SB_DOC_TB.DOC_STATE);
					UInteger lastDocumentSequence = documentRecord.get(SB_DOC_TB.LAST_DOC_SQ);
					
					try {
						DocumentStateType.valueOf(documentStateTypeValue);
					} catch(IllegalArgumentException e) {
						try {
							conn.rollback();
						} catch (Exception e1) {
							log.warn("fail to rollback");
						}

						String errorMessage = new StringBuilder("보고자 하는 문서[")
								.append(documentNo)
								.append("]의 상태 값[")
								.append(documentStateTypeValue).append("]에 잘못된 값이 들어가 있습니다").toString();
						throw new ServerTaskException(errorMessage);
					}
					
					documentHistoryRes.setDocumentSate(documentStateTypeValue);
					documentHistoryRes.setTotal(lastDocumentSequence.longValue() + 1);					
					
					Result<Record5<UInteger, String, String, String, Timestamp>> result = create
					.select(SB_DOC_HISTORY_TB.DOC_SQ, SB_DOC_HISTORY_TB.FILE_NAME, 
							SB_DOC_HISTORY_TB.SUBJECT, SB_DOC_HISTORY_TB.CONTENTS, SB_DOC_HISTORY_TB.REG_DT)
					.from(SB_DOC_HISTORY_TB)
					.where(SB_DOC_HISTORY_TB.DOC_NO.eq(documentNo))
					.offset(offset)
					.limit(pageSize)
					.fetch();
					
					for (Record documentHistoryRecord : result) {
						UInteger documentSequence = documentHistoryRecord.get(SB_DOC_HISTORY_TB.DOC_SQ);
						String fileName = documentHistoryRecord.get(SB_DOC_HISTORY_TB.FILE_NAME);
						String subject = documentHistoryRecord.get(SB_DOC_HISTORY_TB.SUBJECT);
						String contents = documentHistoryRecord.get(SB_DOC_HISTORY_TB.CONTENTS);
						java.sql.Timestamp registeredDate = documentHistoryRecord.get(SB_DOC_HISTORY_TB.REG_DT);
						
						DocumentHistoryRes.Document document = new DocumentHistoryRes.Document();
						document.setDocumentSeq(documentSequence.longValue());
						document.setFileName(fileName);
						document.setSubject(subject);
						document.setContents(contents);
						document.setRegisteredDate(registeredDate);
						
						documentList.add(document);
					}
					
					conn.commit();
		});		
		
		
		documentHistoryRes.setDocumentNo(documentHistoryReq.getDocumentNo());
		documentHistoryRes.setPageNo(pageNo);
		documentHistoryRes.setPageSize(pageSize);
		documentHistoryRes.setCnt(documentList.size());
		documentHistoryRes.setDocumentList(documentList);
		return documentHistoryRes;
	}

}
