package kr.pe.codda.impl.task.server;

import static kr.pe.codda.jooq.tables.SbUploadImageTb.SB_UPLOAD_IMAGE_TB;

import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.types.UInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.DownloadImageReq.DownloadImageReq;
import kr.pe.codda.impl.message.DownloadImageRes.DownloadImageRes;
import kr.pe.codda.server.LoginManagerIF;
import kr.pe.codda.server.lib.DBAutoCommitTaskIF;
import kr.pe.codda.server.lib.ParameterServerTaskException;
import kr.pe.codda.server.lib.RollbackServerTaskException;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

public class DownloadImageReqServerTask extends AbstractServerTask
		implements DBAutoCommitTaskIF<DownloadImageReq, DownloadImageRes> {
	private Logger log = LoggerFactory.getLogger(DownloadImageReqServerTask.class);

	public DownloadImageReqServerTask() throws DynamicClassCallException {
		super();
	}

	@Override
	public void doTask(String projectName, LoginManagerIF personalLoginManager, ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws Exception {

		AbstractMessage outputMessage = ServerDBUtil.doDBAutoTransationWork(
				ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME, this, (DownloadImageReq) inputMessage);
		toLetterCarrier.addSyncOutputMessage(outputMessage);
	}

	@Override
	public DownloadImageRes doWork(final DSLContext dsl, final DownloadImageReq downloadImageReq) throws Exception {
		if (null == dsl) {
			throw new ParameterServerTaskException("the parameter dsl is null");
		}

		if (null == downloadImageReq) {
			throw new ParameterServerTaskException("the parameter downloadImageReq is null");
		}

		// FIXME!
		log.info(downloadImageReq.toString());

		Record2<String, Long> uploadImageRecord = dsl.select(SB_UPLOAD_IMAGE_TB.FNAME, SB_UPLOAD_IMAGE_TB.FSIZE)
				.from(SB_UPLOAD_IMAGE_TB).where(SB_UPLOAD_IMAGE_TB.YYYYMMDD.eq(downloadImageReq.getYyyyMMdd()))
				.and(SB_UPLOAD_IMAGE_TB.DAY_SQ.eq(UInteger.valueOf(downloadImageReq.getDaySequence()))).fetchOne();

		if (null == uploadImageRecord) {
			String errorMessage = "업로드 이미지가 존재하지 않습니다";
			throw new RollbackServerTaskException(errorMessage);
		}

		final String imageFileName = uploadImageRecord.get(SB_UPLOAD_IMAGE_TB.FNAME);
		final long fileSize = uploadImageRecord.get(SB_UPLOAD_IMAGE_TB.FSIZE);

		final DownloadImageRes downloadImageRes = new DownloadImageRes();
		downloadImageRes.setYyyyMMdd(downloadImageReq.getYyyyMMdd());
		downloadImageRes.setDaySequence(downloadImageReq.getDaySequence());
		downloadImageRes.setImageFileName(imageFileName);
		downloadImageRes.setFileSize(fileSize);

		return downloadImageRes;
	}
}
