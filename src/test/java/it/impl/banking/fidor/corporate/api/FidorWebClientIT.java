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

import com.gargoylesoftware.htmlunit.WebClient;
import it.impl.banking.api.account.transaction.Transaction;
import it.impl.banking.api.account.transaction.TransactionConstraint;
import it.impl.banking.api.authentication.MailAddressPasswordToken;
import it.impl.banking.api.authentication.UnauthenticatedException;
import it.impl.banking.fidor.corporate.api.transactionconstraint.FidorDatePeriodTransactionContraint;
import it.impl.banking.fidor.corporate.api.transactionconstraint.FidorDaysBeforeTodayTransactionConstraint;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class FidorWebClientIT {

    @Mock
    WebClient webClient;

    @InjectMocks
    FidorWebClient fidorWebClient;

    @Test
    @Category(FidorCorporateAccountRequired.class)
    public void performRealSignIn() throws Exception {
        String email = System.getProperty("fidor.email");
        String password = System.getProperty("fidor.password");

        fidorWebClient.setWebClient(new WebClient());
        fidorWebClient.signIn(new MailAddressPasswordToken(email, password));
    }

    @Test
    @Category(FidorCorporateAccountRequired.class)
    public void performRealSignOut() throws Exception {
        String email = System.getProperty("fidor.email");
        String password = System.getProperty("fidor.password");

        fidorWebClient.setWebClient(new WebClient());
        fidorWebClient.signIn(new MailAddressPasswordToken(email, password));
        fidorWebClient.singOut();
    }

    @Test
    @Category(FidorCorporateAccountRequired.class)
    public void retrieveStaticAccountInformationIban() throws Exception {
        String email = System.getProperty("fidor.email");
        String password = System.getProperty("fidor.password");
        String owniban = System.getProperty("fidor.owniban");

        fidorWebClient.setWebClient(new WebClient());
        fidorWebClient.signIn(new MailAddressPasswordToken(email, password));

        assertEquals(owniban, fidorWebClient.getIban());
        fidorWebClient.singOut();
    }

    @Test
    @Category(FidorCorporateAccountRequired.class)
    public void getAccountBalance() throws Exception {
        String email = System.getProperty("fidor.email");
        String password = System.getProperty("fidor.password");

        fidorWebClient.setWebClient(new WebClient());
        fidorWebClient.signIn(new MailAddressPasswordToken(email, password));

        assertNotNull(fidorWebClient.getBalanace());
        fidorWebClient.singOut();
    }

    @Test(expected = UnauthenticatedException.class)
    public void AccountBalanceIsNotRetrievableWithoutSignIn() throws Exception {
        fidorWebClient.getIban();
    }

    @Test
    @Category(FidorCorporateAccountRequired.class)
    public void transactionsAreReturnedOnADaysBeforeTodayConstraint() throws Exception {
        String email = System.getProperty("fidor.email");
        String password = System.getProperty("fidor.password");

        WebClient webClientImpl = new WebClient();
        fidorWebClient.setWebClient(webClientImpl);

        TransactionCsvProcessor processor = new TransactionCsvProcessor();
        processor.setWebClient(webClientImpl);
        processor.setTransactionCsvLineParser(new TransactionCsvLineParser());

        TransactionConstraint constraint = new FidorDaysBeforeTodayTransactionConstraint(null, processor, 60);

        fidorWebClient.signIn(new MailAddressPasswordToken(email, password));
        List<Transaction> transactions = fidorWebClient.getTransactions(constraint);
        assertFalse(transactions.isEmpty());

        fidorWebClient.singOut();
    }

    @Test
    @Category(FidorCorporateAccountRequired.class)
    public void transactionsAreReturnedOnDatePeriodConstraint() throws Exception {
        String email = System.getProperty("fidor.email");
        String password = System.getProperty("fidor.password");

        WebClient webClientImpl = new WebClient();
        fidorWebClient.setWebClient(webClientImpl);

        TransactionCsvProcessor processor = new TransactionCsvProcessor();
        processor.setWebClient(webClient);
        processor.setTransactionCsvLineParser(new TransactionCsvLineParser());

        LocalDate from = LocalDate.of(2016, Month.AUGUST, 11);
        LocalDate until = LocalDate.of(2016, Month.AUGUST, 21);
        TransactionConstraint constraint = new FidorDatePeriodTransactionContraint(null, processor, from, until);

        fidorWebClient.signIn(new MailAddressPasswordToken(email, password));
        List<Transaction> transactions = fidorWebClient.getTransactions(constraint);
        assertFalse(transactions.isEmpty());

        fidorWebClient.singOut();
    }
}
