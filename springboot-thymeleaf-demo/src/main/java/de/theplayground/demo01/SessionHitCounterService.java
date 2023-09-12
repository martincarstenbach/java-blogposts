package de.theplayground.demo01;

import oracle.jdbc.OracleTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class SessionHitCounterService {
    private static final Logger LOG = LoggerFactory.getLogger(SessionHitCounterController.class);

    @Autowired
    private JdbcTemplate connection;

    public SessionHitCounter incrementCount(String sessionID, String userAgent) {
        int hitCount = 0;

        SimpleJdbcCall call = new SimpleJdbcCall(connection)
                .withCatalogName("HIT_COUNTER_PKG")
                .withFunctionName("INCREMENT_COUNTER")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlOutParameter("result", OracleTypes.NUMBER),
                        new SqlParameter("p_id", OracleTypes.VARCHAR),
                        new SqlParameter("p_user_agent", OracleTypes.VARCHAR)
                );

        BigDecimal result = call.executeFunction(BigDecimal.class, sessionID, userAgent);

        return new SessionHitCounter(
                sessionID,
                result.intValue()
        );
    }

    public int incrementCount(String sessionID) {
        int hitCount = 0;

        SimpleJdbcCall call = new SimpleJdbcCall(connection)
                .withCatalogName("HIT_COUNTER_PKG")
                .withFunctionName("INCREMENT_COUNTER")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlOutParameter("result", OracleTypes.NUMBER),
                        new SqlParameter("p_id", OracleTypes.VARCHAR)
                );

        BigDecimal result = call.executeFunction(BigDecimal.class, sessionID);

        return result.intValue();
    }
}
