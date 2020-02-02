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

import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.Record4;
import org.jooq.types.UInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.DocumentViewReq.DocumentViewReq;
import kr.pe.codda.impl.message.DocumentViewRes.DocumentViewRes;
import kr.pe.codda.server.LoginManagerIF;
import kr.pe.codda.server.lib.DBAutoCommitTaskIF;
import kr.pe.codda.server.lib.DocumentStateType;
import kr.pe.codda.server.lib.ParameterServerTaskException;
import kr.pe.codda.server.lib.PermissionType;
import kr.pe.codda.server.lib.RollbackServerTaskException;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;
import kr.pe.codda.server.lib.ValueChecker;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

/**
 * 개별 문서 조회 요청 처리 담당 서버 비지니스 로직
 * 
 * @author Won Jonghoon
 *
 */
public class DocumentViewReqServerTask extends AbstractServerTask
		implements DBAutoCommitTaskIF<DocumentViewReq, DocumentViewRes> {
	private Logger log = LoggerFactory.getLogger(DocumentDeleteReqServerTask.class);

	/**
	 * 생성자
	 * 
	 * @throws DynamicClassCallException 동적 호출 작업중 에러 발생시 던지는 예외
	 */
	public DocumentViewReqServerTask() throws DynamicClassCallException {
		super();
	}

	@Override
	public void doTask(String projectName, LoginManagerIF personalLoginManager, ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws Exception {

		AbstractMessage outputMessage = ServerDBUtil.doDBAutoTransationWork(
				ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME, this, (DocumentViewReq) inputMessage);
		toLetterCarrier.addSyncOutputMessage(outputMessage);
	}

	@Override
	public DocumentViewRes doWork(final DSLContext dsl, DocumentViewReq documentViewReq) throws Exception {
		if (null == dsl) {
			throw new ParameterServerTaskException("the parameter dsl is null");
		}

		if (null == documentViewReq) {
			throw new ParameterServerTaskException("the parameter documentViewReq is null");
		}
		/** FIXME! */
		log.info(documentViewReq.toString());

		try {
			ValueChecker.checkValidUserID(documentViewReq.getRequestedUserID());
			ValueChecker.checkValidIP(documentViewReq.getIp());
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			throw new ParameterServerTaskException(errorMessage);
		}

		final UInteger documentNo = UInteger.valueOf(documentViewReq.getDocumentNo());

		final DocumentViewRes documentViewRes = new DocumentViewRes();

		ServerDBUtil.checkUserAccessRights(dsl, "개별 문서 조회 서비스", PermissionType.ADMIN,
				documentViewReq.getRequestedUserID());

		Record2<Byte, UInteger> documentRecord = dsl.select(SB_DOC_TB.DOC_STATE, SB_DOC_TB.LAST_DOC_SQ).from(SB_DOC_TB)
				.where(SB_DOC_TB.DOC_NO.eq(documentNo)).fetchOne();

		if (null == documentRecord) {
			String errorMessage = new StringBuilder("보고자 하는 문서[").append(documentViewReq.getDocumentNo())
					.append("]가 존재하지  않습니다").toString();
			throw new RollbackServerTaskException(errorMessage);
		}

		byte documentStateTypeValue = documentRecord.get(SB_DOC_TB.DOC_STATE);
		UInteger lastDocumentSequence = documentRecord.get(SB_DOC_TB.LAST_DOC_SQ);

		// final DocumentStateType documentSateType;
		try {
			DocumentStateType.valueOf(documentStateTypeValue);
		} catch (IllegalArgumentException e) {
			String errorMessage = new StringBuilder("보고자 하는 문서[").append(documentNo).append("]의 상태 값[")
					.append(documentStateTypeValue).append("]에 잘못된 값이 들어가 있습니다").toString();
			throw new RollbackServerTaskException(errorMessage);
		}

		Record4<String, String, String, Timestamp> documentHistoryRecord = dsl
				.select(SB_DOC_HISTORY_TB.FILE_NAME, SB_DOC_HISTORY_TB.SUBJECT, SB_DOC_HISTORY_TB.CONTENTS,
						SB_DOC_HISTORY_TB.REG_DT)
				.from(SB_DOC_HISTORY_TB).where(SB_DOC_HISTORY_TB.DOC_NO.eq(documentNo))
				.and(SB_DOC_HISTORY_TB.DOC_SQ.eq(lastDocumentSequence)).fetchOne();

		String fileName = documentHistoryRecord.get(SB_DOC_HISTORY_TB.FILE_NAME);
		String subject = documentHistoryRecord.get(SB_DOC_HISTORY_TB.SUBJECT);
		String contents = documentHistoryRecord.get(SB_DOC_HISTORY_TB.CONTENTS);
		java.sql.Timestamp lastModifiedDate = documentHistoryRecord.get(SB_DOC_HISTORY_TB.REG_DT);

		documentViewRes.setDocumentNo(documentViewReq.getDocumentNo());
		documentViewRes.setDocumentSate(documentStateTypeValue);
		documentViewRes.setFileName(fileName);
		documentViewRes.setSubject(subject);
		documentViewRes.setContents(contents);
		documentViewRes.setLastModifiedDate(lastModifiedDate);

		return documentViewRes;
	}

}
