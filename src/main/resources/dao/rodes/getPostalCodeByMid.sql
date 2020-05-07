select ec.cust_pstl_cd from SPRODS_SCHEMA.e_customer_mailer_id ecm
join SPRODS_SCHEMA.e_customer ec on ecm.e_cust_seq = ec.e_cust_seq
where ecm.cust_mid = :mid