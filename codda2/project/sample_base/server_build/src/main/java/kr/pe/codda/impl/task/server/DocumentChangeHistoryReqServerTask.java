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

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record2;
import org.jooq.Record5;
import org.jooq.Result;
import org.jooq.types.UInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.DocumentChangeHistoryReq.DocumentChangeHistoryReq;
import kr.pe.codda.impl.message.DocumentChangeHistoryRes.DocumentChangeHistoryRes;
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
 * 개별 문서 이력 조회 요청 처리 담당 서버 비지니스 로직
 * 
 * @author Won Jonghoon
 *
 */
public class DocumentChangeHistoryReqServerTask extends AbstractServerTask
		implements DBAutoCommitTaskIF<DocumentChangeHistoryReq, DocumentChangeHistoryRes> {
	private Logger log = LoggerFactory.getLogger(DocumentDeleteReqServerTask.class);

	@Override
	public void doTask(String projectName, LoginManagerIF personalLoginManager, ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws Exception {

		AbstractMessage outputMessage = ServerDBUtil.execute(
				ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME, this, (DocumentChangeHistoryReq) inputMessage);
		toLetterCarrier.addSyncOutputMessage(outputMessage);
	}

	@Override
	public DocumentChangeHistoryRes doWork(final DSLContext dsl, final DocumentChangeHistoryReq documentHistoryReq)
			throws Exception {
		if (null == dsl) {
			throw new ParameterServerTaskException("the parameter dsl is null");
		}

		if (null == documentHistoryReq) {
			throw new ParameterServerTaskException("the parameter documentHistoryReq is null");
		}

		/** FIXME! */
		log.info(documentHistoryReq.toString());
		
		// dsl.parsingConnection();

		final int pageNo = documentHistoryReq.getPageNo();
		final int pageSize = documentHistoryReq.getPageSize();

		try {
			ValueChecker.checkValidUserID(documentHistoryReq.getRequestedUserID());
			ValueChecker.checkValidIP(documentHistoryReq.getIp());

			ValueChecker.checkValidPageNoAndPageSize(pageNo, pageSize);
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			throw new ParameterServerTaskException(errorMessage);
		}

		final int offset = (pageNo - 1) * pageSize;
		final UInteger documentNo = UInteger.valueOf(documentHistoryReq.getDocumentNo());
		final java.util.List<DocumentChangeHistoryRes.Document> documentList = new ArrayList<DocumentChangeHistoryRes.Document>();
		final DocumentChangeHistoryRes documentHistoryRes = new DocumentChangeHistoryRes();

		ServerDBUtil.checkUserAccessRights(dsl, "개별 문서 이력 조회 서비스", PermissionType.ADMIN,
				documentHistoryReq.getRequestedUserID());

		Record2<Byte, UInteger> documentRecord = dsl.select(SB_DOC_TB.DOC_STATE, SB_DOC_TB.LAST_DOC_SQ).from(SB_DOC_TB)
				.where(SB_DOC_TB.DOC_NO.eq(documentNo)).fetchOne();

		if (null == documentRecord) {
			String errorMessage = new StringBuilder("이력을 보고자 하는 문서[").append(documentHistoryReq.getDocumentNo())
					.append("]가 존재하지  않습니다").toString();
			throw new RollbackServerTaskException(errorMessage);
		}

		byte documentStateTypeValue = documentRecord.get(SB_DOC_TB.DOC_STATE);
		UInteger lastDocumentSequence = documentRecord.get(SB_DOC_TB.LAST_DOC_SQ);

		try {
			DocumentStateType.valueOf(documentStateTypeValue);
		} catch (IllegalArgumentException e) {

			String errorMessage = new StringBuilder("보고자 하는 문서[").append(documentNo).append("]의 상태 값[")
					.append(documentStateTypeValue).append("]에 잘못된 값이 들어가 있습니다").toString();
			throw new RollbackServerTaskException(errorMessage);
		}

		documentHistoryRes.setDocumentSate(documentStateTypeValue);
		documentHistoryRes.setTotal(lastDocumentSequence.longValue() + 1);

		Result<Record5<UInteger, String, String, String, Timestamp>> result = dsl
				.select(SB_DOC_HISTORY_TB.DOC_SQ, SB_DOC_HISTORY_TB.FILE_NAME, SB_DOC_HISTORY_TB.SUBJECT,
						SB_DOC_HISTORY_TB.CONTENTS, SB_DOC_HISTORY_TB.REG_DT)
				.from(SB_DOC_HISTORY_TB).where(SB_DOC_HISTORY_TB.DOC_NO.eq(documentNo))
				.orderBy(SB_DOC_HISTORY_TB.DOC_SQ.desc()).offset(offset).limit(pageSize).fetch();

		for (Record documentHistoryRecord : result) {
			UInteger documentSequence = documentHistoryRecord.get(SB_DOC_HISTORY_TB.DOC_SQ);
			String fileName = documentHistoryRecord.get(SB_DOC_HISTORY_TB.FILE_NAME);
			String subject = documentHistoryRecord.get(SB_DOC_HISTORY_TB.SUBJECT);
			String contents = documentHistoryRecord.get(SB_DOC_HISTORY_TB.CONTENTS);
			java.sql.Timestamp registeredDate = documentHistoryRecord.get(SB_DOC_HISTORY_TB.REG_DT);

			DocumentChangeHistoryRes.Document document = new DocumentChangeHistoryRes.Document();
			document.setDocumentSeq(documentSequence.longValue());
			document.setFileName(fileName);
			document.setSubject(subject);
			document.setContents(contents);
			document.setRegisteredDate(registeredDate);

			documentList.add(document);
		}

		documentHistoryRes.setDocumentNo(documentHistoryReq.getDocumentNo());
		documentHistoryRes.setPageNo(pageNo);
		documentHistoryRes.setPageSize(pageSize);
		documentHistoryRes.setCnt(documentList.size());
		documentHistoryRes.setDocumentList(documentList);
		return documentHistoryRes;
	}

}
