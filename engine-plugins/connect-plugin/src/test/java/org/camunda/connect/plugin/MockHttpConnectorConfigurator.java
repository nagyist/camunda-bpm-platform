/*
 * Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH
 * under one or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information regarding copyright
 * ownership. Camunda licenses this file to you under the Apache License,
 * Version 2.0; you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.camunda.connect.plugin;

import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicHttpResponse;
import org.camunda.connect.httpclient.HttpConnector;
import org.camunda.connect.httpclient.impl.HttpResponseImpl;
import org.camunda.connect.spi.ConnectorConfigurator;
import org.camunda.connect.spi.ConnectorInvocation;
import org.camunda.connect.spi.ConnectorRequestInterceptor;

/**
 * @author Daniel Meyer
 *
 */
public class MockHttpConnectorConfigurator implements ConnectorConfigurator<HttpConnector> {

  public void configure(HttpConnector connecor) {
    connecor.addRequestInterceptor(new ConnectorRequestInterceptor() {

      public Object handleInvocation(ConnectorInvocation invocation) throws Exception {

        // intercept the call. => do not call invocation.proceed()

        // Could do validation on the invocation here:
        // invocation.getRequest() ....

        // build response using http client api...
        TestHttpResonse testHttpResonse = new TestHttpResonse();
        testHttpResonse.setEntity(new StringEntity("{...}", ContentType.APPLICATION_JSON));

        // return the response
        return new HttpResponseImpl(testHttpResonse);
      }
    });
  }

  public Class<HttpConnector> getConnectorClass() {
    return HttpConnector.class;
  }

  static class TestHttpResonse extends BasicHttpResponse implements ClassicHttpResponse {

    private HttpEntity entity;

    public TestHttpResonse() {
      super(200, "OK");
    }

    @Override
    public HttpEntity getEntity() {
      return entity;
    }

    @Override
    public void setEntity(HttpEntity entity) {
      this.entity = entity;
    }

    @Override
    public void close() {
      /* NOP */
    }
  }

}
