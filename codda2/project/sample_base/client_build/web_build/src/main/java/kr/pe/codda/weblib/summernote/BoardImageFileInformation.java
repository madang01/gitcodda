package kr.pe.codda.weblib.summernote;

import java.util.Arrays;

public class BoardImageFileInformation {	
	private String boardImageMimeType = null;
	private byte[] boardImageFileContents = null;
	private String boardImageFileName = null;
	
	private String yyyyMMdd;
	private long daySequence;
	
	public String getBoardImageMimeType() {
		return boardImageMimeType;
	}
	public void setBoardImageMimeType(String boardImageMimeType) {
		this.boardImageMimeType = boardImageMimeType;
	}
	public byte[] getBoardImageFileContents() {
		return boardImageFileContents;
	}
	public void setBoardImageFileContents(byte[] boardImageFileContents) {
		this.boardImageFileContents = boardImageFileContents;
	}
	public String getBoardImageFileName() {
		return boardImageFileName;
	}
	public void setBoardImageFileName(String boardImageFileName) {
		this.boardImageFileName = boardImageFileName;
	}
	
	
	public String getYyyyMMdd() {
		return yyyyMMdd;
	}

	public void setYyyyMMdd(String yyyyMMdd) {
		this.yyyyMMdd = yyyyMMdd;
	}
	public long getDaySequence() {
		return daySequence;
	}

	public void setDaySequence(long daySequence) {
		this.daySequence = daySequence;
	}
	@Override
	public String toString() {
		final int maxLen = 10;
		StringBuilder builder = new StringBuilder();
		builder.append("BoardImageFileInformation [boardImageMimeType=");
		builder.append(boardImageMimeType);
		builder.append(", boardImageFileContents=");
		builder.append(
				boardImageFileContents != null
						? Arrays.toString(
								Arrays.copyOf(boardImageFileContents, Math.min(boardImageFileContents.length, maxLen)))
						: null);
		builder.append(", boardImageFileName=");
		builder.append(boardImageFileName);
		builder.append(", yyyyMMdd=");
		builder.append(yyyyMMdd);
		builder.append(", daySequence=");
		builder.append(daySequence);
		builder.append("]");
		return builder.toString();
	}
	
}
