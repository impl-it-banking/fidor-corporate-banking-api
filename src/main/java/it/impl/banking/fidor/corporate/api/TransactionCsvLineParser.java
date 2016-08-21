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

import it.impl.banking.api.account.Account;
import it.impl.banking.api.account.transaction.Transaction;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class TransactionCsvLineParser {

    public Transaction parseTransactionCsvLine(String transactionCsvLine) throws ParseException {
        Transaction transaction = new Transaction();

        String[] csvLineParts = transactionCsvLine.split(";");
        applyBookingDate(transaction, csvLineParts[0]);
        applyPurpose(transaction, csvLineParts[1]);
        applyCounterparty(transaction, csvLineParts[2]);
        applyValue(transaction, csvLineParts[3]);

        return transaction;
    }

    private void applyBookingDate(Transaction transaction, String unprocessedBookingDate) {
        LocalDate bookingDate = LocalDate.parse(unprocessedBookingDate, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        transaction.setBookingDate(bookingDate);
    }

    private void applyPurpose(Transaction transaction, String unprocessedPurpose) {
        transaction.setPurpose(unprocessedPurpose);
    }

    private void applyCounterparty(Transaction transaction, String unprocessedCounterparty) {
        String owner;
        String iban;
        if (counterPartyIsProvided(unprocessedCounterparty)) {
            String[] counterpartyParts = unprocessedCounterparty.split("\\, ");

            owner = counterpartyParts[0].split("\\: ")[1];
            iban = counterpartyParts[1].split("\\: ")[1];
        } else {
            String[] counterpartyFromPurpose = transaction.getPurpose().split(" ");
            switch (counterpartyFromPurpose[0]) {
                case "Kapitalertragsteuer":
                case "Habenzinsen":
                    owner = "Fidor Bank";
                    break;
                default:
                    owner = counterpartyFromPurpose[0];
            }

            iban = null;
        }

        Account counterparty = new Account(owner, iban);
        transaction.setCounterparty(counterparty);
    }

    private boolean counterPartyIsProvided(String unprocessedCounterparty) {
        return !("\"\"".equals(unprocessedCounterparty));
    }

    private void applyValue(Transaction transaction, String unprocessedValue) throws ParseException {
        NumberFormat germanNumberFormat = NumberFormat.getInstance(Locale.GERMANY);
        Number value = germanNumberFormat.parse(unprocessedValue);
        transaction.setValue(BigDecimal.valueOf(value.doubleValue()));
    }
}
