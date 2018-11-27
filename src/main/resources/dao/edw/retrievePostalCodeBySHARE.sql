select sax.SHARE_ID_NBR, coalesce(sads.pstl_cd, sadn.pstl_cd, sadr.pstl_cd) as pstl_cd
from smartpost_prod_view_db.share_address_xref sax
left join smartpost_prod_view_db.share_addr_detl_std sads on sads.addr_id_nbr = cast(substr(sax.OPNL_ADDR_ID_NBR, 0, length(sax.OPNL_ADDR_ID_NBR) - 4) as char(25))
left join smartpost_prod_view_db.share_addr_detl_norm sadn on sadn.addr_id_nbr = cast(substr(sax.OPNL_ADDR_ID_NBR, 0, length(sax.OPNL_ADDR_ID_NBR) - 4) as char(25))
left join smartpost_prod_view_db.share_addr_detl_raw sadr on sadr.addr_id_nbr = cast(substr(sax.OPNL_ADDR_ID_NBR, 0, length(sax.OPNL_ADDR_ID_NBR) - 4) as char(25))
where sax.SHARE_ID_NBR in (select * from sharetable)