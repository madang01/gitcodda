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

import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.types.UInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.DocumentDeleteReq.DocumentDeleteReq;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
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
 * 문서 삭제 요청 처리 담당 서버 비지니스 로직
 * 
 * @author Won Jonghoon
 *
 */
public class DocumentDeleteReqServerTask extends AbstractServerTask
		implements DBAutoCommitTaskIF<DocumentDeleteReq, MessageResultRes> {
	private Logger log = LoggerFactory.getLogger(DocumentDeleteReqServerTask.class);

	/**
	 * 생성자
	 * 
	 * @throws DynamicClassCallException 동적 호출 작업중 에러 발생시 던지는 예외
	 */
	public DocumentDeleteReqServerTask() throws DynamicClassCallException {
		super();
	}

	@Override
	public void doTask(String projectName, LoginManagerIF personalLoginManager, ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws Exception {

		AbstractMessage outputMessage = ServerDBUtil.doDBAutoTransationWork(
				ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME, this, (DocumentDeleteReq) inputMessage);
		toLetterCarrier.addSyncOutputMessage(outputMessage);
	}

	@Override
	public MessageResultRes doWork(final DSLContext dsl, DocumentDeleteReq documentDeleteReq) throws Exception {
		if (null == dsl) {
			throw new ParameterServerTaskException("the parameter dsl is null");
		}

		if (null == documentDeleteReq) {
			throw new ParameterServerTaskException("the parameter documentDeleteReq is null");
		}

		/** 처리성 관리자 전용 서비스이기때문에 요청 메시지 로그를 남긴다 */
		log.info(documentDeleteReq.toString());

		try {
			ValueChecker.checkValidUserID(documentDeleteReq.getRequestedUserID());
			ValueChecker.checkValidIP(documentDeleteReq.getIp());
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			throw new ParameterServerTaskException(errorMessage);
		}

		final UInteger documentNo = UInteger.valueOf(documentDeleteReq.getDocumentNo());

		final String successMessage = new StringBuilder().append("문서[no=").append(documentDeleteReq.getDocumentNo())
				.append("]를 삭제하였습니다").toString();

		ServerDBUtil.checkUserAccessRights(dsl, "문서 삭제 서비스", PermissionType.ADMIN,
				documentDeleteReq.getRequestedUserID());

		/** 삭제할 문서에 락을 건다 */
		Record1<Byte> documentRecord = dsl.select(SB_DOC_TB.DOC_STATE).from(SB_DOC_TB)
				.where(SB_DOC_TB.DOC_NO.eq(documentNo)).forUpdate().fetchOne();

		if (null == documentRecord) {

			String errorMessage = new StringBuilder("삭제할 문서[").append(documentDeleteReq.getDocumentNo())
					.append("]가 존재하지  않습니다").toString();
			throw new RollbackServerTaskException(errorMessage);
		}

		byte documentStateTypeValue = documentRecord.get(SB_DOC_TB.DOC_STATE);

		final DocumentStateType documentSateType;
		try {
			documentSateType = DocumentStateType.valueOf(documentStateTypeValue);
		} catch (IllegalArgumentException e) {

			String errorMessage = new StringBuilder("삭제할 문서[").append(documentNo).append("]의 상태 값[")
					.append(documentStateTypeValue).append("]에 잘못된 값이 들어가 있습니다").toString();
			throw new RollbackServerTaskException(errorMessage);
		}

		if (DocumentStateType.DELETE.equals(documentSateType)) {

			String errorMessage = new StringBuilder("삭제 대상 문서[").append(documentNo).append("]는 이미 삭제되었습니다").toString();
			throw new RollbackServerTaskException(errorMessage);
		}

		dsl.update(SB_DOC_TB).set(SB_DOC_TB.DOC_STATE, DocumentStateType.DELETE.getValue())
				.where(SB_DOC_TB.DOC_NO.eq(documentNo)).execute();

		ServerDBUtil.insertSiteLog(dsl, documentDeleteReq.getRequestedUserID(), successMessage,
				new java.sql.Timestamp(System.currentTimeMillis()), documentDeleteReq.getIp());

		MessageResultRes messageResultRes = new MessageResultRes();
		messageResultRes.setTaskMessageID(documentDeleteReq.getMessageID());
		messageResultRes.setIsSuccess(true);
		messageResultRes.setResultMessage(successMessage);

		return messageResultRes;
	}

}
