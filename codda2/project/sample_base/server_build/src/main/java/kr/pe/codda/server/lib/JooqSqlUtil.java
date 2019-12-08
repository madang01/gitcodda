package kr.pe.codda.server.lib;

import java.sql.Timestamp;

import org.jooq.Field;
import org.jooq.impl.DSL;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;

public abstract class JooqSqlUtil {
	public static final String MEMBER_TYPE_NAME_SQL_STRING = new StringBuilder("if ({0} = '")
			.append(MemberRoleType.MEMBER.getValue())
			.append("', '")
			.append(MemberRoleType.MEMBER.getName())
			.append("', if ({0} = '")
			.append(MemberRoleType.ADMIN.getValue())
			.append("', '")
			.append(MemberRoleType.ADMIN.getName())
			.append("', '알수없음'))").toString();

	public static Field<UInteger> getFieldOfLastInsertID(Class<UInteger> type) {
		return DSL.field("LAST_INSERT_ID()", type);
	}
	
	public static Field<Timestamp> getFieldOfSysDate(Class<Timestamp> type) {
		return DSL.field("NOW()", type);
	}
	
	public static Field<String> getFieldOfMemberTypeName(Field<String> memberTypeField) {
		// "if ({0} = 1, '일반회원', if ({0} = 0, '관리자', '알수없음'))"
		return DSL.field(MEMBER_TYPE_NAME_SQL_STRING, 
				String.class, memberTypeField);
	}
	
	/**
	 * if 문 필드를 반환한다, 참고) if ({0} is null, {1}, {2})
	 * @param conditionField {0} 으로 지정되는 필드
	 * @param nullValueField {1} 으로 지정되는 정수
	 * @param notNullValueField {2} 로 지정되는 필드
 	 * @return
	 */
	public static Field<Short> getIfField(Field<UByte> conditionValueField, int nullValueField, Field<UByte> notNullValueField) {
		// if ((max(`sb_db`.`sb_sitemenu_tb`.`order_sq`) + 1) is null, 0, (max(`sb_db`.`sb_sitemenu_tb`.`order_sq`) + 1))
		return DSL.field("if ({0} is null, {1}, {2})", Short.class, conditionValueField, nullValueField, notNullValueField);
	}
}
