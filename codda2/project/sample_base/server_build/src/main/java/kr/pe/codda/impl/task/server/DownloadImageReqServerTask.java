package kr.pe.codda.impl.task.server;

import static kr.pe.codda.jooq.tables.SbUploadImageTb.SB_UPLOAD_IMAGE_TB;

import org.jooq.Record2;
import org.jooq.types.UInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.DownloadImageReq.DownloadImageReq;
import kr.pe.codda.impl.message.DownloadImageRes.DownloadImageRes;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.server.LoginManagerIF;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

public class DownloadImageReqServerTask extends AbstractServerTask {
	private Logger log = LoggerFactory.getLogger(DownloadImageReqServerTask.class);

	public DownloadImageReqServerTask() throws DynamicClassCallException {
		super();
	}
	
	private void sendErrorOutputMessage(String errorMessage, ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws InterruptedException {
		log.warn("{}, inObj=", errorMessage, inputMessage.toString());

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
					(DownloadImageReq) inputMessage);
			toLetterCarrier.addSyncOutputMessage(outputMessage);
		} catch (ServerServiceException e) {
			String errorMessage = e.getMessage();
			log.warn("errmsg=={}, inObj={}", errorMessage, inputMessage.toString());

			sendErrorOutputMessage(errorMessage, toLetterCarrier, inputMessage);
			return;
		} catch (Exception e) {
			String errorMessage = new StringBuilder().append("unknwon errmsg=").append(e.getMessage())
					.append(", inObj=").append(inputMessage.toString()).toString();

			log.warn(errorMessage, e);

			sendErrorOutputMessage("이미지 다운로드가 실패하였습니다", toLetterCarrier, inputMessage);
			return;
		}
	}
	
	public DownloadImageRes doWork(final String dbcpName, final DownloadImageReq downloadImageReq) throws Exception {
		// FIXME!
		log.info(downloadImageReq.toString());
		
		final DownloadImageRes downloadImageRes = new DownloadImageRes();	
		
		ServerDBUtil.execute(dbcpName, (conn, create) -> {

			Record2<String, Long> uploadImageRecord = create.select(SB_UPLOAD_IMAGE_TB.FNAME, SB_UPLOAD_IMAGE_TB.FSIZE)
			.from(SB_UPLOAD_IMAGE_TB)
			.where(SB_UPLOAD_IMAGE_TB.YYYYMMDD.eq(downloadImageReq.getYyyyMMdd()))
			.and(SB_UPLOAD_IMAGE_TB.DAY_SQ.eq(UInteger.valueOf(downloadImageReq.getDaySequence())))
			.fetchOne();
			
			if (null == uploadImageRecord) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}

				String errorMessage = "업로드 이미지가 존재하지 않습니다";
				throw new ServerServiceException(errorMessage);
			}
			
			final String imageFileName =  uploadImageRecord.get(SB_UPLOAD_IMAGE_TB.FNAME);
			final long fileSize = uploadImageRecord.get(SB_UPLOAD_IMAGE_TB.FSIZE);
			downloadImageRes.setYyyyMMdd(downloadImageReq.getYyyyMMdd());
			downloadImageRes.setDaySequence(downloadImageReq.getDaySequence());
			downloadImageRes.setImageFileName(imageFileName);
			downloadImageRes.setFileSize(fileSize);
			
			conn.commit();
		});
		
		return downloadImageRes;
	}
}
