/*
 * Copyright (C) 2016 Benjamin Asbach (https://www.impl.it)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package it.impl.banking.fidor.corporate.api;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import it.impl.banking.api.authentication.AuthenticationServiceException;
import java.io.IOException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import static org.mockito.Matchers.anyString;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class FidorWebClientTest {

    @Mock
    WebClient webClient;

    @InjectMocks
    FidorWebClient fidorWebClient;

    @Test(expected = AuthenticationServiceException.class)
    public void authenticationServiceExceptionOccursOnIOException() throws Exception {
        when(webClient.getPage(anyString())).thenThrow(IOException.class);
        fidorWebClient.signIn(null);
    }

    @Test(expected = AuthenticationServiceException.class)
    public void authenticationServiceExceptionOccursOnFailingHttpStatusCodeException() throws Exception {
        when(webClient.getPage(anyString())).thenThrow(FailingHttpStatusCodeException.class);
        fidorWebClient.signIn(null);
    }
}
