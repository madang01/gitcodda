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
import org.jooq.types.UInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.DocumentModifyReq.DocumentModifyReq;
import kr.pe.codda.impl.message.DocumentModifyRes.DocumentModifyRes;
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
 * 문서 수정 요청 처리 담당 서버 비지니스 로직
 * 
 * @author Won Jonghoon
 *
 */
public class DocumentModifyReqServerTask extends AbstractServerTask
		implements DBAutoCommitTaskIF<DocumentModifyReq, DocumentModifyRes> {
	private Logger log = LoggerFactory.getLogger(DocumentWriteReqServerTask.class);

	/**
	 * 생성자
	 * 
	 * @throws DynamicClassCallException 동적 호출 작업중 에러 발생시 던지는 예외
	 */
	public DocumentModifyReqServerTask() throws DynamicClassCallException {
		super();
	}

	@Override
	public void doTask(String projectName, LoginManagerIF personalLoginManager, ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws Exception {

		AbstractMessage outputMessage = ServerDBUtil.execute(
				ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME, this, (DocumentModifyReq) inputMessage);
		toLetterCarrier.addSyncOutputMessage(outputMessage);
	}

	@Override
	public DocumentModifyRes doWork(final DSLContext dsl, DocumentModifyReq documentModifyReq) throws Exception {
		if (null == dsl) {
			throw new ParameterServerTaskException("the parameter dsl is null");
		}

		if (null == documentModifyReq) {
			throw new ParameterServerTaskException("the parameter documentModifyReq is null");
		}

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
			throw new ParameterServerTaskException(errorMessage);
		}

		final UInteger documentNo = UInteger.valueOf(documentModifyReq.getDocumentNo());		

		final Timestamp registeredDate = new java.sql.Timestamp(System.currentTimeMillis());

		ServerDBUtil.checkUserAccessRights(dsl, "문서 수정 서비스", PermissionType.ADMIN,
				documentModifyReq.getRequestedUserID());

		/** 수정할 문서에 락을 건다 */
		Record2<Byte, UInteger> documentRecord = dsl.select(SB_DOC_TB.DOC_STATE, SB_DOC_TB.LAST_DOC_SQ).from(SB_DOC_TB)
				.where(SB_DOC_TB.DOC_NO.eq(documentNo)).forUpdate().fetchOne();

		if (null == documentRecord) {
			String errorMessage = new StringBuilder("수정할 문서[").append(documentModifyReq.getDocumentNo())
					.append("]가 존재하지  않습니다").toString();
			throw new RollbackServerTaskException(errorMessage);
		}

		byte documentStateTypeValue = documentRecord.get(SB_DOC_TB.DOC_STATE);

		final DocumentStateType documentSateType;
		try {
			documentSateType = DocumentStateType.valueOf(documentStateTypeValue);
		} catch (IllegalArgumentException e) {
			String errorMessage = new StringBuilder("수정할 문서[").append(documentNo).append("]의 상태 값[")
					.append(documentStateTypeValue).append("]에 잘못된 값이 들어가 있습니다").toString();
			throw new RollbackServerTaskException(errorMessage);
		}

		if (!DocumentStateType.OK.equals(documentSateType)) {

			String errorMessage = new StringBuilder("문서[").append(documentNo).append("]의 상태[")
					.append(documentStateTypeValue).append("]가 정상이 아닙니다").toString();
			throw new RollbackServerTaskException(errorMessage);
		}

		UInteger lastDocumentSequence = documentRecord.get(SB_DOC_TB.LAST_DOC_SQ);

		/** 문서의 새로운 마지막 시퀀스 번호 = 문서의 마지막 시퀀스 번호 + 1 */
		long documentSequence = lastDocumentSequence.longValue() + 1;

		if (CommonStaticFinalVars.UNSIGNED_INTEGER_MAX == documentSequence) {

			String errorMessage = new StringBuilder("문서[").append(documentNo)
					.append("]는 최대 수정 횟수[unsigned integer max=").append(documentSequence).append("]를 초과하여 수정할 수 없습니다")
					.toString();
			throw new RollbackServerTaskException(errorMessage);
		}

		UInteger newLastDocumentSequence = UInteger.valueOf(documentSequence);		

		/** 문서의 새로운 마지막 시퀀스 번호로 갱신 */
		dsl.update(SB_DOC_TB).set(SB_DOC_TB.LAST_DOC_SQ, newLastDocumentSequence).where(SB_DOC_TB.DOC_NO.eq(documentNo))
				.execute();

		/** '문서 이력 테이블'(=sb_doc_history_tb)에 '문서의 새로운 마지막 시퀀스 번호'를 갖는 문서 이력 레코드를 추가한다 */
		dsl.insertInto(SB_DOC_HISTORY_TB).set(SB_DOC_HISTORY_TB.DOC_NO, documentNo)
				.set(SB_DOC_HISTORY_TB.DOC_SQ, newLastDocumentSequence)
				.set(SB_DOC_HISTORY_TB.FILE_NAME, documentModifyReq.getFileName())
				.set(SB_DOC_HISTORY_TB.SUBJECT, documentModifyReq.getSubject())
				.set(SB_DOC_HISTORY_TB.CONTENTS, documentModifyReq.getContents())
				.set(SB_DOC_HISTORY_TB.REG_DT, registeredDate).execute();

		DocumentModifyRes documentModifyRes = new DocumentModifyRes();
		documentModifyRes.setDocumentSeq(documentSequence);
		documentModifyRes.setDocumentNo(documentModifyReq.getDocumentNo());

		return documentModifyRes;
	}
}
