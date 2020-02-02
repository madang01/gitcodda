/*
 * This file is generated by jOOQ.
*/
package kr.pe.codda.jooq.tables.records;


import java.sql.Timestamp;

import javax.annotation.Generated;

import kr.pe.codda.jooq.tables.SbMemberTb;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record14;
import org.jooq.Row14;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.UByte;
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
public class SbMemberTbRecord extends UpdatableRecordImpl<SbMemberTbRecord> implements Record14<String, String, String, String, String, Byte, Byte, UByte, Timestamp, Timestamp, Timestamp, Timestamp, Timestamp, UInteger> {

    private static final long serialVersionUID = 685021842;

    /**
     * Setter for <code>sb_db.sb_member_tb.user_id</code>. 사용자 아이디
     */
    public void setUserId(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>sb_db.sb_member_tb.user_id</code>. 사용자 아이디
     */
    public String getUserId() {
        return (String) get(0);
    }

    /**
     * Setter for <code>sb_db.sb_member_tb.nickname</code>. 별명
     */
    public void setNickname(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>sb_db.sb_member_tb.nickname</code>. 별명
     */
    public String getNickname() {
        return (String) get(1);
    }

    /**
     * Setter for <code>sb_db.sb_member_tb.email</code>. 이메일 주소,  320 =  주소 64 byte + @ 1 byte + 도메인주소 255 byte
     */
    public void setEmail(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>sb_db.sb_member_tb.email</code>. 이메일 주소,  320 =  주소 64 byte + @ 1 byte + 도메인주소 255 byte
     */
    public String getEmail() {
        return (String) get(2);
    }

    /**
     * Setter for <code>sb_db.sb_member_tb.pwd_base64</code>. 비밀번호, 비밀번호는 해쉬 값으로 변환되어 base64 형태로 저장된다.
     */
    public void setPwdBase64(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>sb_db.sb_member_tb.pwd_base64</code>. 비밀번호, 비밀번호는 해쉬 값으로 변환되어 base64 형태로 저장된다.
     */
    public String getPwdBase64() {
        return (String) get(3);
    }

    /**
     * Setter for <code>sb_db.sb_member_tb.pwd_salt_base64</code>. 비밀번호를 해쉬로 바꿀때 역 추적 방해를 목적으로 함께 사용하는 랜덤 값
     */
    public void setPwdSaltBase64(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>sb_db.sb_member_tb.pwd_salt_base64</code>. 비밀번호를 해쉬로 바꿀때 역 추적 방해를 목적으로 함께 사용하는 랜덤 값
     */
    public String getPwdSaltBase64() {
        return (String) get(4);
    }

    /**
     * Setter for <code>sb_db.sb_member_tb.role</code>. 회원 역할, A:관리자, M:일반회원, SELECT char(ascii('A') using ascii);
     */
    public void setRole(Byte value) {
        set(5, value);
    }

    /**
     * Getter for <code>sb_db.sb_member_tb.role</code>. 회원 역할, A:관리자, M:일반회원, SELECT char(ascii('A') using ascii);
     */
    public Byte getRole() {
        return (Byte) get(5);
    }

    /**
     * Setter for <code>sb_db.sb_member_tb.state</code>. 회원 상태, Y : 정상, B:블락, W:탈퇴, SELECT char(ascii('Y') using ascii);
     */
    public void setState(Byte value) {
        set(6, value);
    }

    /**
     * Getter for <code>sb_db.sb_member_tb.state</code>. 회원 상태, Y : 정상, B:블락, W:탈퇴, SELECT char(ascii('Y') using ascii);
     */
    public Byte getState() {
        return (Byte) get(6);
    }

    /**
     * Setter for <code>sb_db.sb_member_tb.pwd_fail_cnt</code>. 비밀번호 틀린 횟수, 로그인시 비밀번호 틀릴 경우 1 씩 증가하며 최대 n 번까지 시도 가능하다.  비밀번호를 맞쳤을 경우 0 으로 초기화 된다.
     */
    public void setPwdFailCnt(UByte value) {
        set(7, value);
    }

    /**
     * Getter for <code>sb_db.sb_member_tb.pwd_fail_cnt</code>. 비밀번호 틀린 횟수, 로그인시 비밀번호 틀릴 경우 1 씩 증가하며 최대 n 번까지 시도 가능하다.  비밀번호를 맞쳤을 경우 0 으로 초기화 된다.
     */
    public UByte getPwdFailCnt() {
        return (UByte) get(7);
    }

    /**
     * Setter for <code>sb_db.sb_member_tb.reg_dt</code>. 회원 가입일
     */
    public void setRegDt(Timestamp value) {
        set(8, value);
    }

    /**
     * Getter for <code>sb_db.sb_member_tb.reg_dt</code>. 회원 가입일
     */
    public Timestamp getRegDt() {
        return (Timestamp) get(8);
    }

    /**
     * Setter for <code>sb_db.sb_member_tb.last_nickname_mod_dt</code>. 마지막 이메일 수정일
     */
    public void setLastNicknameModDt(Timestamp value) {
        set(9, value);
    }

    /**
     * Getter for <code>sb_db.sb_member_tb.last_nickname_mod_dt</code>. 마지막 이메일 수정일
     */
    public Timestamp getLastNicknameModDt() {
        return (Timestamp) get(9);
    }

    /**
     * Setter for <code>sb_db.sb_member_tb.last_email_mod_dt</code>. 마지막 이메일 수정일
     */
    public void setLastEmailModDt(Timestamp value) {
        set(10, value);
    }

    /**
     * Getter for <code>sb_db.sb_member_tb.last_email_mod_dt</code>. 마지막 이메일 수정일
     */
    public Timestamp getLastEmailModDt() {
        return (Timestamp) get(10);
    }

    /**
     * Setter for <code>sb_db.sb_member_tb.last_pwd_mod_dt</code>. 마지막 비밀번호 변경일
     */
    public void setLastPwdModDt(Timestamp value) {
        set(11, value);
    }

    /**
     * Getter for <code>sb_db.sb_member_tb.last_pwd_mod_dt</code>. 마지막 비밀번호 변경일
     */
    public Timestamp getLastPwdModDt() {
        return (Timestamp) get(11);
    }

    /**
     * Setter for <code>sb_db.sb_member_tb.last_state_mod_dt</code>.
     */
    public void setLastStateModDt(Timestamp value) {
        set(12, value);
    }

    /**
     * Getter for <code>sb_db.sb_member_tb.last_state_mod_dt</code>.
     */
    public Timestamp getLastStateModDt() {
        return (Timestamp) get(12);
    }

    /**
     * Setter for <code>sb_db.sb_member_tb.next_active_sq</code>. 다음 활동 이력 시퀀스, 0 부터 시작
     */
    public void setNextActiveSq(UInteger value) {
        set(13, value);
    }

    /**
     * Getter for <code>sb_db.sb_member_tb.next_active_sq</code>. 다음 활동 이력 시퀀스, 0 부터 시작
     */
    public UInteger getNextActiveSq() {
        return (UInteger) get(13);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record1<String> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record14 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row14<String, String, String, String, String, Byte, Byte, UByte, Timestamp, Timestamp, Timestamp, Timestamp, Timestamp, UInteger> fieldsRow() {
        return (Row14) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row14<String, String, String, String, String, Byte, Byte, UByte, Timestamp, Timestamp, Timestamp, Timestamp, Timestamp, UInteger> valuesRow() {
        return (Row14) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field1() {
        return SbMemberTb.SB_MEMBER_TB.USER_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return SbMemberTb.SB_MEMBER_TB.NICKNAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return SbMemberTb.SB_MEMBER_TB.EMAIL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field4() {
        return SbMemberTb.SB_MEMBER_TB.PWD_BASE64;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field5() {
        return SbMemberTb.SB_MEMBER_TB.PWD_SALT_BASE64;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Byte> field6() {
        return SbMemberTb.SB_MEMBER_TB.ROLE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Byte> field7() {
        return SbMemberTb.SB_MEMBER_TB.STATE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<UByte> field8() {
        return SbMemberTb.SB_MEMBER_TB.PWD_FAIL_CNT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field9() {
        return SbMemberTb.SB_MEMBER_TB.REG_DT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field10() {
        return SbMemberTb.SB_MEMBER_TB.LAST_NICKNAME_MOD_DT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field11() {
        return SbMemberTb.SB_MEMBER_TB.LAST_EMAIL_MOD_DT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field12() {
        return SbMemberTb.SB_MEMBER_TB.LAST_PWD_MOD_DT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field13() {
        return SbMemberTb.SB_MEMBER_TB.LAST_STATE_MOD_DT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<UInteger> field14() {
        return SbMemberTb.SB_MEMBER_TB.NEXT_ACTIVE_SQ;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component1() {
        return getUserId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component2() {
        return getNickname();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component3() {
        return getEmail();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component4() {
        return getPwdBase64();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component5() {
        return getPwdSaltBase64();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte component6() {
        return getRole();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte component7() {
        return getState();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UByte component8() {
        return getPwdFailCnt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component9() {
        return getRegDt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component10() {
        return getLastNicknameModDt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component11() {
        return getLastEmailModDt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component12() {
        return getLastPwdModDt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component13() {
        return getLastStateModDt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UInteger component14() {
        return getNextActiveSq();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value1() {
        return getUserId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value2() {
        return getNickname();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value3() {
        return getEmail();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value4() {
        return getPwdBase64();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value5() {
        return getPwdSaltBase64();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte value6() {
        return getRole();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte value7() {
        return getState();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UByte value8() {
        return getPwdFailCnt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value9() {
        return getRegDt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value10() {
        return getLastNicknameModDt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value11() {
        return getLastEmailModDt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value12() {
        return getLastPwdModDt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value13() {
        return getLastStateModDt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UInteger value14() {
        return getNextActiveSq();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbMemberTbRecord value1(String value) {
        setUserId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbMemberTbRecord value2(String value) {
        setNickname(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbMemberTbRecord value3(String value) {
        setEmail(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbMemberTbRecord value4(String value) {
        setPwdBase64(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbMemberTbRecord value5(String value) {
        setPwdSaltBase64(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbMemberTbRecord value6(Byte value) {
        setRole(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbMemberTbRecord value7(Byte value) {
        setState(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbMemberTbRecord value8(UByte value) {
        setPwdFailCnt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbMemberTbRecord value9(Timestamp value) {
        setRegDt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbMemberTbRecord value10(Timestamp value) {
        setLastNicknameModDt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbMemberTbRecord value11(Timestamp value) {
        setLastEmailModDt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbMemberTbRecord value12(Timestamp value) {
        setLastPwdModDt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbMemberTbRecord value13(Timestamp value) {
        setLastStateModDt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbMemberTbRecord value14(UInteger value) {
        setNextActiveSq(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbMemberTbRecord values(String value1, String value2, String value3, String value4, String value5, Byte value6, Byte value7, UByte value8, Timestamp value9, Timestamp value10, Timestamp value11, Timestamp value12, Timestamp value13, UInteger value14) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        value9(value9);
        value10(value10);
        value11(value11);
        value12(value12);
        value13(value13);
        value14(value14);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached SbMemberTbRecord
     */
    public SbMemberTbRecord() {
        super(SbMemberTb.SB_MEMBER_TB);
    }

    /**
     * Create a detached, initialised SbMemberTbRecord
     */
    public SbMemberTbRecord(String userId, String nickname, String email, String pwdBase64, String pwdSaltBase64, Byte role, Byte state, UByte pwdFailCnt, Timestamp regDt, Timestamp lastNicknameModDt, Timestamp lastEmailModDt, Timestamp lastPwdModDt, Timestamp lastStateModDt, UInteger nextActiveSq) {
        super(SbMemberTb.SB_MEMBER_TB);

        set(0, userId);
        set(1, nickname);
        set(2, email);
        set(3, pwdBase64);
        set(4, pwdSaltBase64);
        set(5, role);
        set(6, state);
        set(7, pwdFailCnt);
        set(8, regDt);
        set(9, lastNicknameModDt);
        set(10, lastEmailModDt);
        set(11, lastPwdModDt);
        set(12, lastStateModDt);
        set(13, nextActiveSq);
    }
}
