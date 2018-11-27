select sp_package_num, sp_sort_start_date, sp_sort_location, sp_fxsp_origin_location
from SPRODS_SCHEMA.PACKAGE_DETAIL_XFER
where sp_package_num in (:packageIds) and
      rac_xfer_ctrl_seq in (SELECT rac_xfer_ctrl_seq FROM sprods_schema.rac_xfer_ctrl
                            WHERE rac_id in (:racIds))