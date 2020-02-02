package kr.pe.codda.server.lib;

import org.jooq.DSLContext;

public interface DBExecutorIF {
	public void execute(final DSLContext dsl) throws Exception;
}
