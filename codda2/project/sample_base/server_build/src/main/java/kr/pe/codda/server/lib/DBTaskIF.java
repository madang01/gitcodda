package kr.pe.codda.server.lib;

import org.jooq.DSLContext;

public interface DBTaskIF<I, O> {
	public O doWork(final DSLContext dsl, final I req) throws Exception;
}
