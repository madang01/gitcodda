/*
 * This file is generated by jOOQ.
*/
package kr.pe.codda.jooq;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import kr.pe.codda.jooq.tables.SbAccountSerarchTb;
import kr.pe.codda.jooq.tables.SbBoardFilelistTb;
import kr.pe.codda.jooq.tables.SbBoardHistoryTb;
import kr.pe.codda.jooq.tables.SbBoardInfoTb;
import kr.pe.codda.jooq.tables.SbBoardTb;
import kr.pe.codda.jooq.tables.SbBoardVoteTb;
import kr.pe.codda.jooq.tables.SbDocHistoryTb;
import kr.pe.codda.jooq.tables.SbDocTb;
import kr.pe.codda.jooq.tables.SbMemberActivityHistoryTb;
import kr.pe.codda.jooq.tables.SbMemberTb;
import kr.pe.codda.jooq.tables.SbSeqTb;
import kr.pe.codda.jooq.tables.SbSiteLogTb;
import kr.pe.codda.jooq.tables.SbSitemenuTb;
import kr.pe.codda.jooq.tables.SbUploadImageTb;

import org.jooq.Catalog;
import org.jooq.Table;
import org.jooq.impl.SchemaImpl;


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
public class SbDb extends SchemaImpl {

    private static final long serialVersionUID = -1046974919;

    /**
     * The reference instance of <code>sb_db</code>
     */
    public static final SbDb SB_DB = new SbDb();

    /**
     * 계정 찾기 테이블
     */
    public final SbAccountSerarchTb SB_ACCOUNT_SERARCH_TB = kr.pe.codda.jooq.tables.SbAccountSerarchTb.SB_ACCOUNT_SERARCH_TB;

    /**
     * The table <code>sb_db.sb_board_filelist_tb</code>.
     */
    public final SbBoardFilelistTb SB_BOARD_FILELIST_TB = kr.pe.codda.jooq.tables.SbBoardFilelistTb.SB_BOARD_FILELIST_TB;

    /**
     * The table <code>sb_db.sb_board_history_tb</code>.
     */
    public final SbBoardHistoryTb SB_BOARD_HISTORY_TB = kr.pe.codda.jooq.tables.SbBoardHistoryTb.SB_BOARD_HISTORY_TB;

    /**
     * The table <code>sb_db.sb_board_info_tb</code>.
     */
    public final SbBoardInfoTb SB_BOARD_INFO_TB = kr.pe.codda.jooq.tables.SbBoardInfoTb.SB_BOARD_INFO_TB;

    /**
     * The table <code>sb_db.sb_board_tb</code>.
     */
    public final SbBoardTb SB_BOARD_TB = kr.pe.codda.jooq.tables.SbBoardTb.SB_BOARD_TB;

    /**
     * The table <code>sb_db.sb_board_vote_tb</code>.
     */
    public final SbBoardVoteTb SB_BOARD_VOTE_TB = kr.pe.codda.jooq.tables.SbBoardVoteTb.SB_BOARD_VOTE_TB;

    /**
     * The table <code>sb_db.sb_doc_history_tb</code>.
     */
    public final SbDocHistoryTb SB_DOC_HISTORY_TB = kr.pe.codda.jooq.tables.SbDocHistoryTb.SB_DOC_HISTORY_TB;

    /**
     * The table <code>sb_db.sb_doc_tb</code>.
     */
    public final SbDocTb SB_DOC_TB = kr.pe.codda.jooq.tables.SbDocTb.SB_DOC_TB;

    /**
     * The table <code>sb_db.sb_member_activity_history_tb</code>.
     */
    public final SbMemberActivityHistoryTb SB_MEMBER_ACTIVITY_HISTORY_TB = kr.pe.codda.jooq.tables.SbMemberActivityHistoryTb.SB_MEMBER_ACTIVITY_HISTORY_TB;

    /**
     * The table <code>sb_db.sb_member_tb</code>.
     */
    public final SbMemberTb SB_MEMBER_TB = kr.pe.codda.jooq.tables.SbMemberTb.SB_MEMBER_TB;

    /**
     * The table <code>sb_db.sb_seq_tb</code>.
     */
    public final SbSeqTb SB_SEQ_TB = kr.pe.codda.jooq.tables.SbSeqTb.SB_SEQ_TB;

    /**
     * The table <code>sb_db.sb_sitemenu_tb</code>.
     */
    public final SbSitemenuTb SB_SITEMENU_TB = kr.pe.codda.jooq.tables.SbSitemenuTb.SB_SITEMENU_TB;

    /**
     * The table <code>sb_db.sb_site_log_tb</code>.
     */
    public final SbSiteLogTb SB_SITE_LOG_TB = kr.pe.codda.jooq.tables.SbSiteLogTb.SB_SITE_LOG_TB;

    /**
     * The table <code>sb_db.sb_upload_image_tb</code>.
     */
    public final SbUploadImageTb SB_UPLOAD_IMAGE_TB = kr.pe.codda.jooq.tables.SbUploadImageTb.SB_UPLOAD_IMAGE_TB;

    /**
     * No further instances allowed
     */
    private SbDb() {
        super("sb_db", null);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Catalog getCatalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

    @Override
    public final List<Table<?>> getTables() {
        List result = new ArrayList();
        result.addAll(getTables0());
        return result;
    }

    private final List<Table<?>> getTables0() {
        return Arrays.<Table<?>>asList(
            SbAccountSerarchTb.SB_ACCOUNT_SERARCH_TB,
            SbBoardFilelistTb.SB_BOARD_FILELIST_TB,
            SbBoardHistoryTb.SB_BOARD_HISTORY_TB,
            SbBoardInfoTb.SB_BOARD_INFO_TB,
            SbBoardTb.SB_BOARD_TB,
            SbBoardVoteTb.SB_BOARD_VOTE_TB,
            SbDocHistoryTb.SB_DOC_HISTORY_TB,
            SbDocTb.SB_DOC_TB,
            SbMemberActivityHistoryTb.SB_MEMBER_ACTIVITY_HISTORY_TB,
            SbMemberTb.SB_MEMBER_TB,
            SbSeqTb.SB_SEQ_TB,
            SbSitemenuTb.SB_SITEMENU_TB,
            SbSiteLogTb.SB_SITE_LOG_TB,
            SbUploadImageTb.SB_UPLOAD_IMAGE_TB);
    }
}
