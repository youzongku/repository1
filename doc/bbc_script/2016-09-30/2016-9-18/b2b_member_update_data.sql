insert into t_dis_member(
	user_name,
	pass_word,
	nick_name,
	real_name,
	gender,
	birthday,
	email,
	telphone,
	profile,
	head_img,
	is_actived,
	create_date,
	last_update_date,
	role_id,
	rank_id,
	is_customized,
	customized_discount,
	work_no,
	comsumer_type,
	register_invite_code,
	self_invite_code,
	erp_account,
	distribution_mode
) select  user_name,
					pass_word,
					nick_name,
					real_name,
					gender,
					birthday,
					email,
					telphone,
					profile,
					head_img,
					is_actived,
					create_date,
					last_update_date,
					role_id,
					rank_id,
					is_customized,
					customized_discount,
					work_no,
					comsumer_type,
					register_invite_code,
					self_invite_code,
					erp_account,
					distribution_mode
    from  t_dis_member
	 where  id = 1;

update t_dis_member set email = 'superadmin' where id = 1;

insert into t_dis_role(
	role_name,
	role_desc,
	create_date,
	button_authority,
	isactive,
	is_message
) select role_name,
				 role_desc,
				 create_date,
				 button_authority,
				 isactive,
				 is_message
    from t_dis_role
   where id = 1;

update t_dis_role set role_name = 'superadmin',role_desc = '超级管理员' where id = 1;

update t_dis_member set role_id = (select r.id from t_dis_role r where r.role_name = 'admin')
 where role_id = 1 and id != 1;

insert into t_dis_role_menu_mapper(
	roleid,
	menuid
) select (select r.id from t_dis_role r where r.role_name = 'admin'),menuid from t_dis_role_menu_mapper where roleid = 1;