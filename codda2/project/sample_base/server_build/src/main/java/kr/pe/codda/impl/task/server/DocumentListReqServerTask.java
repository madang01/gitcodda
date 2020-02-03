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
import java.util.ArrayList;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Record5;
import org.jooq.Result;
import org.jooq.types.UInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.DocumentListReq.DocumentListReq;
import kr.pe.codda.impl.message.DocumentListRes.DocumentListRes;
import kr.pe.codda.jooq.tables.SbDocHistoryTb;
import kr.pe.codda.jooq.tables.SbDocTb;
import kr.pe.codda.server.LoginManagerIF;
import kr.pe.codda.server.lib.DBAutoCommitTaskIF;
import kr.pe.codda.server.lib.DocumentSateSearchType;
import kr.pe.codda.server.lib.DocumentStateType;
import kr.pe.codda.server.lib.ParameterServerTaskException;
import kr.pe.codda.server.lib.PermissionType;
import kr.pe.codda.server.lib.RollbackServerTaskException;
import kr.pe.codda.server.lib.SequenceType;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;
import kr.pe.codda.server.lib.ValueChecker;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

/**
 * 문서 목록 요청 처리 담당 서버 비지니스 로직
 * 
 * @author Won Jonghoon
 *
 */
public class DocumentListReqServerTask extends AbstractServerTask
		implements DBAutoCommitTaskIF<DocumentListReq, DocumentListRes> {
	private Logger log = LoggerFactory.getLogger(DocumentListReqServerTask.class);

	/**
	 * 생성자
	 * 
	 * @throws DynamicClassCallException 동적 호출 작업중 에러 발생시 던지는 예외
	 */
	public DocumentListReqServerTask() throws DynamicClassCallException {
		super();
	}

	@Override
	public void doTask(String projectName, LoginManagerIF personalLoginManager, ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws Exception {

		AbstractMessage outputMessage = ServerDBUtil.execute(
				ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME, this, (DocumentListReq) inputMessage);
		toLetterCarrier.addSyncOutputMessage(outputMessage);
	}

	@Override
	public DocumentListRes doWork(final DSLContext dsl, final DocumentListReq documentListReq) throws Exception {
		if (null == dsl) {
			throw new ParameterServerTaskException("the parameter dsl is null");
		}

		if (null == documentListReq) {
			throw new ParameterServerTaskException("the parameter documentListReq is null");
		}

		/** FIXME! */
		log.info(documentListReq.toString());

		final int pageNo = documentListReq.getPageNo();
		final int pageSize = documentListReq.getPageSize();
		final DocumentSateSearchType documentSateSearchType;

		try {
			ValueChecker.checkValidUserID(documentListReq.getRequestedUserID());
			ValueChecker.checkValidPageNoAndPageSize(pageNo, pageSize);

			documentSateSearchType = DocumentSateSearchType.valueOf(documentListReq.getDocumentSateSearchType());
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			throw new ParameterServerTaskException(errorMessage);
		}

		final int offset = (pageNo - 1) * pageSize;
		final java.util.List<DocumentListRes.Document> documentList = new ArrayList<DocumentListRes.Document>();
		final DocumentListRes documentListRes = new DocumentListRes();

		ServerDBUtil.checkUserAccessRights(dsl, "문서 조회 서비스", PermissionType.ADMIN,
				documentListReq.getRequestedUserID());

		Record1<UInteger> seqRecord = dsl.select(SB_SEQ_TB.SQ_VALUE).from(SB_SEQ_TB)
				.where(SB_SEQ_TB.SQ_ID.eq(SequenceType.DOCUMENT_NO.getSequenceID())).fetchOne();

		if (null == seqRecord) {
			String errorMessage = new StringBuilder("시퀀스 테이블에서 문서 번호 시퀀스[")
					.append(SequenceType.DOCUMENT_NO.getSequenceID()).append("]가 존재하지  않습니다").toString();
			throw new RollbackServerTaskException(errorMessage);
		}

		UInteger nextDocumentNo = seqRecord.get(SB_SEQ_TB.SQ_VALUE);
		documentListRes.setTotal(nextDocumentNo.longValue() - 1);

		SbDocTb a = SB_DOC_TB.as("a");

		SbDocHistoryTb b = SB_DOC_HISTORY_TB.as("b");

		Result<Record5<UInteger, Byte, String, String, Timestamp>> result = null;

		if (DocumentSateSearchType.OK.equals(documentSateSearchType)) {
			result = dsl.select(a.DOC_NO, a.DOC_STATE, b.FILE_NAME, b.SUBJECT, b.REG_DT).from(a).join(b)
					.on(a.DOC_NO.eq(b.DOC_NO)).and(a.LAST_DOC_SQ.eq(b.DOC_SQ))
					.where(a.DOC_STATE.eq(DocumentStateType.OK.getValue())).orderBy(a.DOC_NO.desc()).offset(offset)
					.limit(pageSize).fetch();
		} else if (DocumentSateSearchType.DELETE.equals(documentSateSearchType)) {
			result = dsl.select(a.DOC_NO, a.DOC_STATE, b.FILE_NAME, b.SUBJECT, b.REG_DT).from(a).join(b)
					.on(a.DOC_NO.eq(b.DOC_NO)).and(a.LAST_DOC_SQ.eq(b.DOC_SQ))
					.where(a.DOC_STATE.eq(DocumentStateType.DELETE.getValue())).orderBy(a.DOC_NO.desc()).offset(offset)
					.limit(pageSize).fetch();
		} else {
			result = dsl.select(a.DOC_NO, a.DOC_STATE, b.FILE_NAME, b.SUBJECT, b.REG_DT).from(a).join(b)
					.on(a.DOC_NO.eq(b.DOC_NO)).and(a.LAST_DOC_SQ.eq(b.DOC_SQ)).orderBy(a.DOC_NO.desc()).offset(offset)
					.limit(pageSize).fetch();
		}

		for (Record documentRecord : result) {
			UInteger documentNo = documentRecord.get(SB_DOC_TB.DOC_NO);
			byte documentSateType = documentRecord.get(SB_DOC_TB.DOC_STATE);
			String fileName = documentRecord.get(SB_DOC_HISTORY_TB.FILE_NAME);
			String subject = documentRecord.get(SB_DOC_HISTORY_TB.SUBJECT);
			java.sql.Timestamp lastModifiedDate = documentRecord.get(SB_DOC_HISTORY_TB.REG_DT);

			DocumentListRes.Document document = new DocumentListRes.Document();
			document.setDocumentNo(documentNo.longValue());
			document.setDocumentSate(documentSateType);
			document.setFileName(fileName);
			document.setSubject(subject);
			document.setLastModifiedDate(lastModifiedDate);

			documentList.add(document);
		}

		documentListRes.setPageNo(pageNo);
		documentListRes.setPageSize(pageSize);
		documentListRes.setDocumentList(documentList);
		documentListRes.setCnt(documentList.size());

		return documentListRes;
	}

}
