select * from (
    select eshx.FXG_HUB_ID from SPRODS_SCHEMA.e_customer_mailer_id ecm
    join SPRODS_SCHEMA.e_cust_dist_ctr_mid_sphub_xref ecdcmsx on ecdcmsx.e_cust_seq = ecm.e_cust_seq
    join SPRODS_SCHEMA.e_smartpost_hub_xref eshx on eshx.e_sphubx_seq = ecdcmsx.e_sphubx_seq
    where ecm.cust_mid = :mid
    order by eshx.created_dt desc
) where rownum < 2
