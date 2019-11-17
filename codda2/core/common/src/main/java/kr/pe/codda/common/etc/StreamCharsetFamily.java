package kr.pe.codda.common.etc;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

public class StreamCharsetFamily {
	private final Charset streamCharset;
	private final CharsetDecoder streamCharsetDecoder;
	private final CharsetEncoder streamCharsetEncoder;
	
	public StreamCharsetFamily(Charset streamCharset) {
		if (null == streamCharset) {
			throw new IllegalArgumentException("the parameter streamCharset is null");
		}
		
		this.streamCharset = streamCharset;
		
		streamCharsetDecoder = CharsetUtil.createCharsetDecoder(streamCharset);
		streamCharsetEncoder = CharsetUtil.createCharsetEncoder(streamCharset);
	}
	
	public Charset getCharset() {
		return streamCharset;
	}
	
	public CharsetDecoder getCharsetDecoder() {
		return streamCharsetDecoder;
	}
	
	public CharsetEncoder getCharsetEncoder() {
		return streamCharsetEncoder;
	}
}
