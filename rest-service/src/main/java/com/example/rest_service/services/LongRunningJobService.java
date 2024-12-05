package com.example.rest_service.services;

import com.example.rest_service.entities.LongRunningJob;
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

/**
 * This class defines the interaction with the database. In summary, it invokes
 * a PL/SQL function simulating a long running job (10 seconds)
 */
@Service
public class LongRunningJobService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LongRunningJobService.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void doWork(String requestedBy) {

        LOGGER.info("about to do some work with the database");

        SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName("APP_PACKAGE")
                .withFunctionName("DO_WORK")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlOutParameter("result", OracleTypes.NUMBER),
                        new SqlParameter("p_requested_by", OracleTypes.VARCHAR)
                );

        // the internal Job ID - as provided by the database, not JobRunr, is returned
        // use this number to look the result up in the job_log_table
        BigDecimal result = call.executeFunction(BigDecimal.class, requestedBy);

        LOGGER.info("finished database work, job ID: {}", result);
    }
}
