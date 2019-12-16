-- MySQL Workbench Synchronization
-- Generated: 2019-04-19 12:57
-- Model: New Model
-- Version: 1.0
-- Project: Name of the project
-- Author: madang02

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

CREATE SCHEMA IF NOT EXISTS `SB_DB` DEFAULT CHARACTER SET utf8 ;

CREATE TABLE IF NOT EXISTS `SB_DB`.`SB_BOARD_INFO_TB` (
  `board_id` TINYINT(3) UNSIGNED NOT NULL COMMENT '�Խ��� �ĺ���,\n1 : ����, 2:����, 3:�̽�',
  `board_name` VARCHAR(30) NULL DEFAULT NULL COMMENT '�Խ��� �̸�',
  `list_type` TINYINT(4) NOT NULL COMMENT '�Խ��� ��� ����, 0:�׷� ��Ʈ, 1; ����',
  `reply_policy_type` TINYINT(4) NOT NULL COMMENT '��� ��å ����, 0:��۾���, 1:�����ۿ���, 2:������ ��� ���',
  `write_permission_type` TINYINT(4) NOT NULL COMMENT '���� ���� ���� ����, 0:����, 1:�Ϲ���, 2:�մ�',
  `reply_permission_type` TINYINT(4) NOT NULL COMMENT '��� ���� ���� ����, 0:����, 1:�Ϲ���, 2:�մ�, ��) \'��� ���� ���� ����\'�� \'�� ����\'���� \'0 ��۾���\' �� �ƴ� ��츸 ��ȿ�ϴ�',
  `cnt` BIGINT(20) NOT NULL DEFAULT 0 COMMENT '�Խ��� ��� ����',
  `total` BIGINT(20) NOT NULL DEFAULT 0 COMMENT '�Խ��� �� ��ü ����',
  `next_board_no` INT(10) UNSIGNED NOT NULL DEFAULT 1 COMMENT '���� �Խ��� ��ȣ, ������ �θ� �Խ��� ��ȣ�� 0 ���� ����Ǿ� �־� 1���� ����',
  PRIMARY KEY (`board_id`))
ENGINE = InnoDB
AUTO_INCREMENT = 3
DEFAULT CHARACTER SET = utf8;

CREATE TABLE IF NOT EXISTS `SB_DB`.`SB_MEMBER_TB` (
  `user_id` VARCHAR(20) NOT NULL COMMENT '����� ���̵�',
  `nickname` VARCHAR(45) NOT NULL COMMENT '����',
  `email` VARCHAR(320) NOT NULL COMMENT '�̸��� �ּ�,  320 =  �ּ� 64 byte + @ 1 byte + �������ּ� 255 byte',
  `pwd_base64` VARCHAR(88) NOT NULL COMMENT '��й�ȣ, ��й�ȣ�� �ؽ� ������ ��ȯ�Ǿ� base64 ���·� ����ȴ�.',
  `pwd_salt_base64` VARCHAR(12) NOT NULL COMMENT '��й�ȣ�� �ؽ��� �ٲܶ� �� ���� ���ظ� �������� �Բ� ����ϴ� ���� ��',
  `role` TINYINT(4) NOT NULL COMMENT 'ȸ�� ����, A:������, M:�Ϲ�ȸ��, SELECT char(ascii(\'A\') using ascii);',
  `state` TINYINT(4) NOT NULL COMMENT 'ȸ�� ����, Y : ����, B:���, W:Ż��, SELECT char(ascii(\'Y\') using ascii);',
  `pwd_fail_cnt` TINYINT(4) UNSIGNED NULL DEFAULT NULL COMMENT '��й�ȣ Ʋ�� Ƚ��, �α��ν� ��й�ȣ Ʋ�� ��� 1 �� �����ϸ� �ִ� n ������ �õ� �����ϴ�.  ��й�ȣ�� ������ ��� 0 ���� �ʱ�ȭ �ȴ�.',
  `reg_dt` DATETIME NULL DEFAULT NULL COMMENT 'ȸ�� ������',
  `last_nickname_mod_dt` DATETIME NULL DEFAULT NULL COMMENT '������ �̸��� ������',
  `last_email_mod_dt` DATETIME NULL DEFAULT NULL COMMENT '������ �̸��� ������',
  `last_pwd_mod_dt` DATETIME NULL DEFAULT NULL COMMENT '������ ��й�ȣ ������',
  PRIMARY KEY (`user_id`),
  UNIQUE INDEX `sb_member_idx1` (`nickname` ASC),
  INDEX `sb_member_idx2` (`state` ASC),
  UNIQUE INDEX `email_UNIQUE` (`email` ASC))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

CREATE TABLE IF NOT EXISTS `SB_DB`.`SB_BOARD_TB` (
  `board_id` TINYINT(3) UNSIGNED NOT NULL COMMENT '�Խ��� ���� �ĺ���, � �Խ������� �����ϴ� �Խ��� ����(board_info) ���̺��� �ٶ󺻴�.',
  `board_no` INT(10) UNSIGNED NOT NULL COMMENT '�Խ��� ��ȣ,  1���� �����Ѵ�. 1 �� �ʱ�ȭ �Ǵ� ������ ���̺�(SB_SEQ_TB) �� ���� �Խ��� Ÿ�Ժ��� �Խ��� ��ȣ�� ����',
  `group_no` INT(10) UNSIGNED NOT NULL COMMENT '�׷� ��ȣ',
  `group_sq` SMALLINT(5) UNSIGNED NOT NULL COMMENT '�׷� �� ������ �׷� ��ȣ(=group_no)  ���� 0 ���� ���۵Ǵ� ����',
  `parent_no` INT(10) UNSIGNED NULL DEFAULT NULL COMMENT '�θ� �Խ��� ��ȣ,  �Խ��� ��ȣ�� 1���� �����ϸ� �θ� ���� ��� �θ� �Խ��� ��ȣ�� 0 ���� ���´�.',
  `depth` TINYINT(3) UNSIGNED NULL DEFAULT NULL COMMENT 'Ʈ�� ����,  0 ���� �����ϸ� Ʈ�� ���̰� 0 �� ��� �ֻ��� �۷ν� �ֻ��� ���� �������� ���� ����� �޸���. �ڽ� ���� ��� ���̴� �θ� ���� ��� ���̺��� 1 �� ũ��.',
  `view_cnt` INT(11) NULL DEFAULT NULL COMMENT '��ȸ��',
  `board_st` TINYINT(4) NOT NULL COMMENT '�Խñ� ����, M:�Խñ� �̵�,  B : ���, T:Ʈ�����, D : ������ �Խñ�, Y : ���� �Խñ�,  SELECT char(ascii(\'Y\') using ascii);',
  `next_attached_file_sq` TINYINT(3) UNSIGNED NULL DEFAULT NULL COMMENT '���� ÷�� ���� ������, ó�� 0���� ����',
  `pwd_base64` VARCHAR(88) NULL DEFAULT NULL COMMENT '�Խñ� ��й�ȣ, �մ��� ��� �ݵ��� �Խñ� ��й�ȣ�� �Է��Ѵ�. �Խñ� �����Ҷ� �� ���� null  �� �ƴϸ� �Խñ� ��й�ȣ�� �Է��Ѱ����� �����Ͽ� �� ��ġ�Ѱ�쿡�� ������ ����Ѵ�',
  PRIMARY KEY (`board_id`, `board_no`),
  INDEX `sb_board_fk1_idx` (`board_id` ASC),
  INDEX `sb_board_idx1` (`board_id` DESC, `group_no` DESC, `group_sq` DESC, `board_st` DESC),
  INDEX `sb_board_idx2` (`board_id` DESC, `parent_no` DESC, `board_no` DESC, `board_st` DESC),
  CONSTRAINT `sb_board_fk1`
    FOREIGN KEY (`board_id`)
    REFERENCES `SB_DB`.`SB_BOARD_INFO_TB` (`board_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

CREATE TABLE IF NOT EXISTS `SB_DB`.`SB_BOARD_FILELIST_TB` (
  `board_id` TINYINT(3) UNSIGNED NOT NULL,
  `board_no` INT(10) UNSIGNED NOT NULL,
  `attached_file_sq` TINYINT(3) UNSIGNED NOT NULL COMMENT '÷�� ���� ����',
  `attached_fname` VARCHAR(255) NULL DEFAULT NULL COMMENT '÷�� ���� �̸�',
  `attached_fsize` BIGINT(20) NULL DEFAULT NULL COMMENT '÷�� ���� ũ��',
  PRIMARY KEY (`board_id`, `board_no`, `attached_file_sq`),
  CONSTRAINT `sb_board_filelist_fk1`
    FOREIGN KEY (`board_id` , `board_no`)
    REFERENCES `SB_DB`.`SB_BOARD_TB` (`board_id` , `board_no`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

CREATE TABLE IF NOT EXISTS `SB_DB`.`SB_BOARD_VOTE_TB` (
  `board_id` TINYINT(3) UNSIGNED NOT NULL,
  `board_no` INT(10) UNSIGNED NOT NULL,
  `user_id` VARCHAR(20) NOT NULL,
  `ip` VARCHAR(40) NULL DEFAULT NULL,
  `reg_dt` DATETIME NULL DEFAULT NULL,
  PRIMARY KEY (`board_no`, `user_id`, `board_id`),
  CONSTRAINT `sb_board_vote_fk2`
    FOREIGN KEY (`user_id`)
    REFERENCES `SB_DB`.`SB_MEMBER_TB` (`user_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `sb_board_vote_fk1`
    FOREIGN KEY (`board_id` , `board_no`)
    REFERENCES `SB_DB`.`SB_BOARD_TB` (`board_id` , `board_no`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

CREATE TABLE IF NOT EXISTS `SB_DB`.`SB_SEQ_TB` (
  `sq_id` TINYINT(3) UNSIGNED NOT NULL COMMENT '������ �ĺ���, 0:�޴�, 1:�����Խ��� ������, 2:�����Խ��ǽ�����, 3:FAQ������',
  `sq_value` INT(10) UNSIGNED NULL DEFAULT NULL COMMENT '������ ��, 1 ���� ����',
  `sq_name` VARCHAR(45) NULL DEFAULT NULL COMMENT '������ �̸�',
  PRIMARY KEY (`sq_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

CREATE TABLE IF NOT EXISTS `SB_DB`.`SB_SITEMENU_TB` (
  `menu_no` INT(10) UNSIGNED NOT NULL COMMENT '�޴� ��ȣ,  1���� ���۵ȴ�. 1 �� �ʱ�ȭ �Ǵ� ������ ���̺�(SB_SEQ_TB) �� ���� �޴� ��ȣ�� ����.',
  `parent_no` INT(10) UNSIGNED NOT NULL COMMENT '�θ� �޴� ��ȣ,  �޴� ��ȣ�� 1���� ���۵Ǹ� �θ� ���� ��� �θ� �޴� ��ȣ ����  0 ���� ���´�.',
  `depth` TINYINT(3) UNSIGNED NOT NULL COMMENT 'Ʈ�� ����,  0 ���� �����ϸ� �θ𺸴� + 1 �� ũ��',
  `order_sq` TINYINT(3) UNSIGNED NOT NULL COMMENT '��ü �޴� ����',
  `menu_nm` VARCHAR(100) NOT NULL COMMENT '�޴� �̸�',
  `link_url` VARCHAR(2048) NOT NULL COMMENT '�޴��� �����Ǵ� ��ũ �ּ�',
  PRIMARY KEY (`menu_no`),
  INDEX `sb_sitemenu_idx1` (`order_sq` ASC),
  INDEX `sb_sitemenu_idx2` (`parent_no` ASC, `order_sq` ASC))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

CREATE TABLE IF NOT EXISTS `SB_DB`.`SB_BOARD_HISTORY_TB` (
  `board_id` TINYINT(3) UNSIGNED NOT NULL,
  `board_no` INT(10) UNSIGNED NOT NULL,
  `history_sq` TINYINT(3) UNSIGNED NOT NULL COMMENT '�����丮 ����',
  `subject` VARCHAR(255) NULL DEFAULT NULL,
  `contents` MEDIUMTEXT NULL DEFAULT NULL,
  `registrant_id` VARCHAR(20) NOT NULL COMMENT '�ۼ���',
  `ip` VARCHAR(40) NULL DEFAULT NULL,
  `reg_dt` DATETIME NULL DEFAULT NULL COMMENT '���� �ۼ���',
  PRIMARY KEY (`board_id`, `board_no`, `history_sq`),
  INDEX `sb_board_history_fk2_idx` (`registrant_id` ASC),
  CONSTRAINT `sb_board_history_fk1`
    FOREIGN KEY (`board_id` , `board_no`)
    REFERENCES `SB_DB`.`SB_BOARD_TB` (`board_id` , `board_no`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `sb_board_history_fk2`
    FOREIGN KEY (`registrant_id`)
    REFERENCES `SB_DB`.`SB_MEMBER_TB` (`user_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

CREATE TABLE IF NOT EXISTS `SB_DB`.`SB_SITE_LOG_TB` (
  `yyyymmdd` CHAR(8) NOT NULL COMMENT '�α� ��¥, yyyyMMdd ������ 8�ڸ� ����� ����',
  `day_log_sq` INT(10) UNSIGNED NOT NULL COMMENT '���� �α� ����',
  `user_id` VARCHAR(20) NULL DEFAULT NULL COMMENT '����� ���̵�',
  `log_txt` TEXT NULL DEFAULT NULL COMMENT '�α� ����, �α׷� ����� ���� (1) ȸ�� ����, (2) ȸ�� Ż��, (3) ����� ����, (4) ����� ���� ����, (5) �Խñ� ����, (6) �Խñ� ���� ����,  (7) ������ �α���',
  `reg_dt` DATETIME NULL DEFAULT NULL,
  `ip` VARCHAR(40) NULL DEFAULT NULL,
  PRIMARY KEY (`yyyymmdd`, `day_log_sq`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

CREATE TABLE IF NOT EXISTS `SB_DB`.`SB_MEMBER_ACTIVITY_HISTORY_TB` (
  `user_id` VARCHAR(20) NOT NULL COMMENT '����� ���̵�, Ȱ���� �� ȸ�� ���̵�',
  `activity_sq` BIGINT(20) NOT NULL COMMENT 'Ȱ�� ����, 0 ���� ���۵Ǹ� ���� MAX + 1 �� �ȴ�',
  `board_id` TINYINT(3) UNSIGNED NOT NULL COMMENT '�Խ��� �ĺ���,  Ȱ��  ����� �Ǵ� �Խñ��� �Խ��� �ĺ���',
  `board_no` INT(10) UNSIGNED NOT NULL COMMENT '�Խ��� ��ȣ, Ȱ�� ����� �Ǵ� �Խñ��� �Խ��� ��ȣ',
  `activity_type` TINYINT(4) NOT NULL COMMENT '����� Ȱ�� ����, \'W\'(=87):�Խñ� �ۼ�, \'R\'(=82):�Խñ� ���, \'V\'(=86):�Խñ� ��õ, \'D\'(=68):�Խñ� ����, \' SELECT char(ascii(\'W\') using ascii);',
  `reg_dt` DATETIME NULL DEFAULT NULL,
  PRIMARY KEY (`user_id`, `activity_sq`),
  INDEX `board_id_idx` (`board_id` ASC, `board_no` ASC),
  CONSTRAINT `member_activity_history_fk1`
    FOREIGN KEY (`user_id`)
    REFERENCES `SB_DB`.`SB_MEMBER_TB` (`user_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `member_activity_hisotry_fk2`
    FOREIGN KEY (`board_id` , `board_no`)
    REFERENCES `SB_DB`.`SB_BOARD_TB` (`board_id` , `board_no`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

CREATE TABLE IF NOT EXISTS `SB_DB`.`SB_ACCOUNT_SERARCH_TB` (
  `user_id` VARCHAR(20) NOT NULL COMMENT '����� ���̵�',
  `fail_cnt` TINYINT(3) UNSIGNED NOT NULL COMMENT '��� ���� �� ���� Ƚ��, 0 ���� ���� ��й�ȣ Ʋ���� �� 1 ����',
  `retry_cnt` TINYINT(3) UNSIGNED NOT NULL COMMENT '��й�ȣ ã�� ��õ� Ƚ��, 1���� �����ϸ� ��õ��Ҷ� ���� 1�� �����Ѵ�',
  `last_secret_auth_value` VARCHAR(20) NOT NULL COMMENT '������ ��� ���� ��, ��й�ȣ ã�� ��û�� �ִ� Ƚ������ �����ϸ� �׶����� \'��� ���� ��\' �� \'��� ��ȣ ã�� ��û��\' �� ����ȴ�',
  `last_req_dt` DATETIME NOT NULL COMMENT '������ ��й�ȣ ã�� ��û��, ��й�ȣ ã�� ��û�� �ִ� Ƚ������ �����ϸ� �׶����� \'��� ���� ��\' �� \'��� ��ȣ ã�� ��û��\' �� ����ȴ�',
  `is_finished` CHAR(1) NULL DEFAULT NULL COMMENT '���Ῡ��,  \'N\':�̰�, \'Y\':����, 24�ð� ������ �����Ͽ� �Ϸ�� ���� ������ Ƚ���� �����ϱ� �����̸� ��ġ���� ���� ���·� 24�ð��� ���� ���ڵ� �ϰ� �����ϵ����Ѵ�.',
  PRIMARY KEY (`user_id`),
  CONSTRAINT `pwd_search_req_fk1`
    FOREIGN KEY (`user_id`)
    REFERENCES `SB_DB`.`SB_MEMBER_TB` (`user_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
