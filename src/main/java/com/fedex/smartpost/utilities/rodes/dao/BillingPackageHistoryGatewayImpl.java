package com.fedex.smartpost.utilities.rodes.dao;

import com.fedex.smartpost.common.io.classpath.ClassPathResourceUtil;
import com.fedex.smartpost.utilities.rodes.model.EPDIRecord;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.annotation.PreDestroy;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BillingPackageHistoryGatewayImpl extends NamedParameterJdbcTemplate implements BillingPackageHistoryGateway {
    private static final String EPDI_BY_PACKAGE_ID_SQL = ClassPathResourceUtil.getString("/dao/rodes/epdiHistoryByPackageId.sql");
    private DataSource dataSource;

    public BillingPackageHistoryGatewayImpl(DataSource dataSource) {
        super(dataSource);
        this.dataSource = dataSource;
    }

    private static RowMapper<EPDIRecord> EPDI_MAPPER = (rs, rowNum) -> {
        EPDIRecord epdiRecord = new EPDIRecord();

        epdiRecord.setFedexPkgId(rs.getString("FEDEX_PKG_ID"));
        epdiRecord.setFedexCustAcctNbr(rs.getLong("FEDEX_CUST_ACCT_NBR"));
        epdiRecord.setHandleFlag(rs.getString("HANDLE_FLAG"));
        epdiRecord.setPkgCategoryCd(rs.getString("PKG_CATEGORY_CD"));
        epdiRecord.setPkgTypeCd(rs.getString("PKG_TYPE_CD"));
        epdiRecord.setCustDistCtr(rs.getString("CUST_DIST_CTR"));
        epdiRecord.setCustMnfstId(rs.getString("CUST_MNFST_ID"));
        epdiRecord.setSpPkgWgt(rs.getFloat("SP_PKG_WGT"));
        epdiRecord.setSpPkgHgt(rs.getFloat("SP_PKG_HGT"));
        epdiRecord.setSpPkgLength(rs.getFloat("SP_PKG_LENGTH"));
        epdiRecord.setSpPkgWidth(rs.getFloat("SP_PKG_WIDTH"));
        epdiRecord.setSpPkgWgtSourceCd(rs.getString("SP_PKG_WGT_SOURCE_CD"));
        epdiRecord.setSpPkgDimSourceCd(rs.getString("SP_PKG_DIM_SOURCE_CD"));
        epdiRecord.setCustPkgWgt(rs.getFloat("CUST_PKG_WGT"));
        epdiRecord.setCustPkgHgt(rs.getFloat("CUST_PKG_HGT"));
        epdiRecord.setCustPkgLength(rs.getFloat("CUST_PKG_LENGTH"));
        epdiRecord.setCustPkgWidth(rs.getFloat("CUST_PKG_WIDTH"));
        epdiRecord.setPstlClassCd(rs.getString("PSTL_CLASS_CD"));
        epdiRecord.setPstlSubClassCd(rs.getString("PSTL_SUB_CLASS_CD"));
        epdiRecord.setOriginHubCd(rs.getString("ORIGIN_HUB_CD"));
        epdiRecord.setOriginPstlCd(rs.getString("ORIGIN_PSTL_CD"));
        epdiRecord.setRecpPstlCd(rs.getString("RECP_PSTL_CD"));
        epdiRecord.setCustPkgId(rs.getString("CUST_PKG_ID"));
        epdiRecord.setSpBlngSvcCd(rs.getString("SP_BLNG_SVC_CD"));
        epdiRecord.setDelConReqFlag(rs.getString("DEL_CON_REQ_FLAG"));
        epdiRecord.setEpdiAuditFlag(rs.getString("EPDI_AUDIT_FLAG"));
        epdiRecord.setExpectedFlag(rs.getString("EXPECTED_FLAG"));
        epdiRecord.setPkgRcvdFlag(rs.getString("PKG_RCV_FLAG"));
        epdiRecord.setBillingNbr(rs.getString("BILLING_NBR"));
        epdiRecord.setMnfstGrpText(rs.getString("MNFST_GRP_TEXT"));
        epdiRecord.setFxgSvcCdSrc(rs.getString("FXG_SVC_CD_SRC"));
        epdiRecord.setChanlAppId(rs.getString("CHANL_APP_ID"));
        epdiRecord.setPoNum(rs.getString("PO_NUM"));
        epdiRecord.setMeterNum(rs.getString("METER_NUM"));
        epdiRecord.setRmaNum(rs.getString("RMA_NUM"));
        epdiRecord.setTptyFedexCustAcctNbr(rs.getString("TPTY_FEDEX_CUST_ACCT_NBR"));
        epdiRecord.setDelConfReqdFlagSrc(rs.getString("DEL_CONF_REQD_FLAG_SRC"));
        epdiRecord.setFedexCustAcctNbrSrc(rs.getString("FEDEX_CUST_ACCT_NBR_SRC"));
        epdiRecord.setMailerId(rs.getString("MAILER_ID"));
        epdiRecord.setLabelFmt(rs.getString("LABEL_FMT"));
        epdiRecord.setReturnTyp(rs.getString("RETURN_TYP"));
        epdiRecord.setCustMailClassCd(rs.getString("CUST_MAIL_CLASS_CD"));
        epdiRecord.setCustSubMailClassCd(rs.getString("CUST_SUB_MAIL_CLASS_CD"));
        epdiRecord.setCustFxgHubId(rs.getString("CUST_FXG_HUB_ID"));
        return epdiRecord;
    };

    @Override
    public List<EPDIRecord> retrieveEPDIRecordsByPackageIds(List<String> packageIds) {
        MapSqlParameterSource parameters;
		List<EPDIRecord> epdiRecords = new ArrayList<>();
		int startPos = 0;
		int length;

		while (startPos < packageIds.size()) {
			length = Math.min(packageIds.size() - startPos, 1000);
			parameters = new MapSqlParameterSource();
	        parameters.addValue("packageIds", packageIds.subList(startPos, startPos + length));
			epdiRecords.addAll(query(EPDI_BY_PACKAGE_ID_SQL, parameters, EPDI_MAPPER));
			startPos += length;
		}
        return epdiRecords;
	}

    @Override
    @PreDestroy
    public void close() throws SQLException {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        connection.close();
    }
}
