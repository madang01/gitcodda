package kr.pe.codda.impl.task.server;

import static kr.pe.codda.jooq.tables.SbSeqTb.SB_SEQ_TB;
import static kr.pe.codda.jooq.tables.SbUploadImageTb.SB_UPLOAD_IMAGE_TB;

import java.text.SimpleDateFormat;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.jooq.types.UInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.UploadImageReq.UploadImageReq;
import kr.pe.codda.impl.message.UploadImageRes.UploadImageRes;
import kr.pe.codda.server.LoginManagerIF;
import kr.pe.codda.server.lib.DBAutoCommitTaskIF;
import kr.pe.codda.server.lib.ParameterServerTaskException;
import kr.pe.codda.server.lib.RollbackServerTaskException;
import kr.pe.codda.server.lib.SequenceType;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;
import kr.pe.codda.server.lib.ValueChecker;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

public class UploadImageReqServerTask extends AbstractServerTask
		implements DBAutoCommitTaskIF<UploadImageReq, UploadImageRes> {
	private Logger log = LoggerFactory.getLogger(AccountSearchProcessReqServerTask.class);

	public UploadImageReqServerTask() throws DynamicClassCallException {
		super();
	}

	@Override
	public void doTask(String projectName, LoginManagerIF personalLoginManager, ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws Exception {

		AbstractMessage outputMessage = ServerDBUtil.execute(ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME, this,
				(UploadImageReq) inputMessage);

		toLetterCarrier.addSyncOutputMessage(outputMessage);
	}

	@Override
	public UploadImageRes doWork(final DSLContext dsl, final UploadImageReq uploadImageReq) throws Exception {
		if (null == dsl) {
			throw new ParameterServerTaskException("the parameter dsl is null");
		}

		if (null == uploadImageReq) {
			throw new ParameterServerTaskException("the parameter uploadImageReq is null");
		}

		// FIXME!
		log.info(uploadImageReq.toString());

		try {
			ValueChecker.checkValidFileName(uploadImageReq.getImageFileName());
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			throw new ParameterServerTaskException(errorMessage);
		}

		if (uploadImageReq.getFileSize() <= 0) {
			String errorMessage = "이미지 파일 크기가 0 보다 작거나 같습니다";
			throw new ParameterServerTaskException(errorMessage);
		}

		final UploadImageRes uploadImageRes = new UploadImageRes();
		final java.sql.Timestamp registeredDate = new java.sql.Timestamp(System.currentTimeMillis());
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		final String yyyyMMdd = sdf.format(registeredDate);

		dsl.select(SB_SEQ_TB.SQ_ID).from(SB_SEQ_TB)
				.where(SB_SEQ_TB.SQ_ID.eq(SequenceType.SITE_UPLOAD_IMAGE_LOCK.getSequenceID())).forUpdate().fetchOne();

		long maxOfDaySeq = dsl
				.select(DSL.field("if ({0} is null, {1}, {2})", Long.class, SB_UPLOAD_IMAGE_TB.DAY_SQ.max(),
						Long.valueOf(0), SB_UPLOAD_IMAGE_TB.DAY_SQ.max()))
				.from(SB_UPLOAD_IMAGE_TB).where(SB_UPLOAD_IMAGE_TB.YYYYMMDD.eq(yyyyMMdd)).fetchOne().value1();

		if (maxOfDaySeq == CommonStaticFinalVars.UNSIGNED_INTEGER_MAX) {
			throw new RollbackServerTaskException(
					"작업 시점의 SB_UPLOAD_IMAGE_TB 테이블의 날짜 시퀀스가 최대치에 도달하여 더 이상 이미지를 추가할 수 없습니다");
		}

		long newDayLogSeq = maxOfDaySeq + 1;

		dsl.insertInto(SB_UPLOAD_IMAGE_TB).set(SB_UPLOAD_IMAGE_TB.YYYYMMDD, yyyyMMdd)
				.set(SB_UPLOAD_IMAGE_TB.DAY_SQ, UInteger.valueOf(newDayLogSeq))
				.set(SB_UPLOAD_IMAGE_TB.FNAME, uploadImageReq.getImageFileName())
				.set(SB_UPLOAD_IMAGE_TB.FSIZE, uploadImageReq.getFileSize()).execute();

		uploadImageRes.setYyyyMMdd(yyyyMMdd);
		uploadImageRes.setDaySequence(newDayLogSeq);

		return uploadImageRes;
	}
}
