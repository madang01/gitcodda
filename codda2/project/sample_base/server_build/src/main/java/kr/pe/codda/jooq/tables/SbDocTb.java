/*
 * This file is generated by jOOQ.
*/
package kr.pe.codda.jooq.tables;


import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import kr.pe.codda.jooq.Indexes;
import kr.pe.codda.jooq.Keys;
import kr.pe.codda.jooq.SbDb;
import kr.pe.codda.jooq.tables.records.SbDocTbRecord;

import org.jooq.Field;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;
import org.jooq.types.UInteger;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.10.6"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class SbDocTb extends TableImpl<SbDocTbRecord> {

    private static final long serialVersionUID = 1706624224;

    /**
     * The reference instance of <code>sb_db.sb_doc_tb</code>
     */
    public static final SbDocTb SB_DOC_TB = new SbDocTb();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<SbDocTbRecord> getRecordType() {
        return SbDocTbRecord.class;
    }

    /**
     * The column <code>sb_db.sb_doc_tb.doc_no</code>. 문서 번호, 시퀀스(=sb_seq-tb) 테이블의 '시퀀스 식별자'(=sq_id)  3번을 참조하며  1부터 시작
     */
    public final TableField<SbDocTbRecord, UInteger> DOC_NO = createField("doc_no", org.jooq.impl.SQLDataType.INTEGERUNSIGNED.nullable(false), this, "문서 번호, 시퀀스(=sb_seq-tb) 테이블의 '시퀀스 식별자'(=sq_id)  3번을 참조하며  1부터 시작");

    /**
     * The column <code>sb_db.sb_doc_tb.doc_state</code>. 문서 상태
     */
    public final TableField<SbDocTbRecord, Byte> DOC_STATE = createField("doc_state", org.jooq.impl.SQLDataType.TINYINT.nullable(false), this, "문서 상태");

    /**
     * The column <code>sb_db.sb_doc_tb.last_doc-sq</code>. 마지막 문서 시퀀스 번호, 1부터 시작.
     */
    public final TableField<SbDocTbRecord, UInteger> LAST_DOC_SQ = createField("last_doc-sq", org.jooq.impl.SQLDataType.INTEGERUNSIGNED.nullable(false), this, "마지막 문서 시퀀스 번호, 1부터 시작.");

    /**
     * Create a <code>sb_db.sb_doc_tb</code> table reference
     */
    public SbDocTb() {
        this(DSL.name("sb_doc_tb"), null);
    }

    /**
     * Create an aliased <code>sb_db.sb_doc_tb</code> table reference
     */
    public SbDocTb(String alias) {
        this(DSL.name(alias), SB_DOC_TB);
    }

    /**
     * Create an aliased <code>sb_db.sb_doc_tb</code> table reference
     */
    public SbDocTb(Name alias) {
        this(alias, SB_DOC_TB);
    }

    private SbDocTb(Name alias, Table<SbDocTbRecord> aliased) {
        this(alias, aliased, null);
    }

    private SbDocTb(Name alias, Table<SbDocTbRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return SbDb.SB_DB;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.SB_DOC_TB_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<SbDocTbRecord> getPrimaryKey() {
        return Keys.KEY_SB_DOC_TB_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<SbDocTbRecord>> getKeys() {
        return Arrays.<UniqueKey<SbDocTbRecord>>asList(Keys.KEY_SB_DOC_TB_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbDocTb as(String alias) {
        return new SbDocTb(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbDocTb as(Name alias) {
        return new SbDocTb(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public SbDocTb rename(String name) {
        return new SbDocTb(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public SbDocTb rename(Name name) {
        return new SbDocTb(name, null);
    }
}