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
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import it.impl.banking.api.authentication.AuthenticationService;
import it.impl.banking.api.authentication.AuthenticationServiceException;
import it.impl.banking.api.authentication.InvalidAuthenticationTokenException;
import it.impl.banking.api.authentication.MailAddressPasswordToken;
import it.impl.banking.api.authentication.UnauthenticatedException;
import java.io.IOException;
import lombok.Setter;

public class FidorWebClient implements
        AuthenticationService<MailAddressPasswordToken> {

    final static String FIDOR_SIGN_IN_PAGE = "https://banking.fidor.de/users/sign_in";

    final static String FIDOR_CORPORATE_SIGN_OUT_PAGE = "https://banking.fidor.de/corporate/logout";

    @Setter
    private WebClient webClient;

    private HtmlPage overviewPage;

    @Override
    public void signIn(MailAddressPasswordToken token) throws AuthenticationServiceException {
        try {
            HtmlPage loginPage = webClient.getPage(FIDOR_SIGN_IN_PAGE);
            HtmlForm form = loginPage.getFormByName("");
            form.getInputByName("user[email]").setValueAttribute(token.getMailAddress());
            form.getInputByName("user[password]").setValueAttribute(token.getPassword());

            overviewPage = form.getInputByName("commit").click();
            checkForSuccessfulSignIn();
        } catch (IOException | FailingHttpStatusCodeException ex) {
            overviewPage = null;
            throw new AuthenticationServiceException(ex);
        } catch (AuthenticationServiceException ex) {
            overviewPage = null;
            throw ex;
        }
    }

    private void checkForSuccessfulSignIn() throws InvalidAuthenticationTokenException {
        if (overviewPage.getByXPath("//div[@class='fidorpay-app-navigation']").isEmpty()) {
            throw new InvalidAuthenticationTokenException("Could not find expected div. This could be caused by invalid credentials or fidor business html changed.");
        }
    }

    @Override
    public void singOut() throws AuthenticationServiceException {
        try {
            checkForAuthenticatedSession();

            webClient.getPage(FIDOR_CORPORATE_SIGN_OUT_PAGE);
        } catch (IOException | FailingHttpStatusCodeException ex) {
            throw new AuthenticationServiceException(ex);
        }
    }

    private void checkForAuthenticatedSession() throws UnauthenticatedException {
        if (overviewPage == null) {
            throw new UnauthenticatedException();
        }
    }
}
