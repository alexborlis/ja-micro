package com.sixt.service.test_service;

import static org.assertj.core.api.Assertions.assertThat;

import com.sixt.service.framework.intercepted.api.Intercepted;
import com.sixt.service.framework.rpc.RpcCallException;

import org.junit.Test;

public class InterceptionTest {

    @Test
    public void test() throws RpcCallException {
        Intercepted.InterceptedRequest request = Intercepted.InterceptedRequest.newBuilder()
            .setId("id")
            .setMessage("message")
            .build();

        Intercepted.InterceptedResponse response
            = (Intercepted.InterceptedResponse) ServiceIntegrationTestSuite.testService.sendRequest(
            "Intercepted.InterceptedMethod", request);

        assertThat(response).isEqualTo(Intercepted.InterceptedResponse.getDefaultInstance());
    }
}
