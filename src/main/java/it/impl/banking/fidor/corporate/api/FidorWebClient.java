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
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import it.impl.banking.api.BankingApiException;
import it.impl.banking.api.account.balance.BalanceService;
import it.impl.banking.api.account.balance.BalanceServiceException;
import it.impl.banking.api.account.information.StaticAccountInformationService;
import it.impl.banking.api.account.transaction.Transaction;
import it.impl.banking.api.account.transaction.TransactionConstraint;
import it.impl.banking.api.account.transaction.TransactionConstraintException;
import it.impl.banking.api.account.transaction.TransactionService;
import it.impl.banking.api.account.transaction.TransactionServiceException;
import it.impl.banking.api.authentication.AuthenticationService;
import it.impl.banking.api.authentication.AuthenticationServiceException;
import it.impl.banking.api.authentication.InvalidAuthenticationTokenException;
import it.impl.banking.api.authentication.MailAddressPasswordToken;
import it.impl.banking.api.authentication.UnauthenticatedException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;
import lombok.Setter;

public class FidorWebClient implements
        AuthenticationService<MailAddressPasswordToken>,
        StaticAccountInformationService,
        BalanceService,
        TransactionService {

    final static String FIDOR_SIGN_IN_PAGE
            = "https://banking.fidor.de/users/sign_in";

    final static String FIDOR_CORPORATE_SIGN_OUT_PAGE
            = "https://banking.fidor.de/corporate/logout";

    final static String FIDOR_CORPORATE_TRANSACTIONS_CSV_PAGE
            = "https://banking.fidor.de/smart-account/transactions.csv?days=60&time_selection=days";

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

    @Override
    public String getIban() throws BankingApiException {
        checkForAuthenticatedSession();

        return ((HtmlElement) overviewPage
                .getByXPath("//ul[@class='account-number-info']/li/strong[1]")
                .get(0))
                .asText()
                .replaceAll("\\s+", "");
    }

    @Override
    public BigDecimal getBalanace() throws UnauthenticatedException, BalanceServiceException {
        checkForAuthenticatedSession();
        try {
            String balanceWithCurrency = ((HtmlElement) overviewPage
                    .getByXPath("//div[@class='available-balance']/span")
                    .get(0))
                    .asText()
                    .replaceAll("[^0-9\\,]", "");

            NumberFormat germanNumberFormat = NumberFormat.getInstance(Locale.GERMANY);
            Number balance = germanNumberFormat.parse(balanceWithCurrency);

            return BigDecimal.valueOf(balance.doubleValue());
        } catch (ParseException ex) {
            throw new BalanceServiceException(ex);
        }
    }

    @Override
    public List<Transaction> getTransactions(TransactionConstraint constraint) throws TransactionServiceException {
        try {
            return constraint.getTransactionsFilteredByConstraint();
        } catch (TransactionConstraintException ex) {
            throw new TransactionServiceException(ex);
        }
    }
}
