package com.sangeeth.gitbot.retrofitEndPoints;

import retrofit2.Call;
import retrofit2.http.*;

/**
 * @author dtsangeeth
 * @created 18 / 04 / 2020
 * @project GitComponentManager
 *
 * Elastic Rest API
 *
 */
public interface ElasticAPI {


    @GET("/rest/api/2/project")
    Call<Object> getProjects();



}
