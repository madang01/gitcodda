package kr.pe.codda.weblib.summernote;

public class BoardImageFileInformation {	
	private String boardImageMimeType;
	private byte[] boardImageFileContents;
	private String boardImageFileName;
		
	
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
	
	
}
