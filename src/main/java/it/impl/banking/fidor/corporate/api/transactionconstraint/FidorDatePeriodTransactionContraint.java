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
package it.impl.banking.fidor.corporate.api.transactionconstraint;

import it.impl.banking.api.account.transaction.Transaction;
import it.impl.banking.api.account.transaction.TransactionConstraint;
import it.impl.banking.api.account.transaction.TransactionConstraintException;
import it.impl.banking.fidor.corporate.api.TransactionCsvProcessor;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FidorDatePeriodTransactionContraint extends TransactionConstraint {

    private final static String FIDOR_CORPORATE_TRANSACTION_CSV_PAGE
            = "https://banking.fidor.de/smart-account/transactions.csv?from=%s&time_selection=days&to=%s";

    private final TransactionCsvProcessor transactionCsvProcessor;

    private final LocalDate from;

    private final LocalDate until;

    public FidorDatePeriodTransactionContraint(
            Type type,
            TransactionCsvProcessor transactionCsvProcessor,
            LocalDate from,
            LocalDate until) {
        super(type);
        this.transactionCsvProcessor = transactionCsvProcessor;
        this.from = from;
        this.until = until;
    }

    @Override
    public List<Transaction> getTransactionsFilteredByConstraint() throws TransactionConstraintException {
        DateTimeFormatter germanDateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String fromStr = germanDateFormat.format(from);
        String untilStr = germanDateFormat.format(until);

        String csvUrl = String.format(FIDOR_CORPORATE_TRANSACTION_CSV_PAGE, fromStr, untilStr);

        try {
            return transactionCsvProcessor.parseTransactionsCsv(csvUrl);
        } catch (ParseException | IOException ex) {
            throw new TransactionConstraintException(ex);
        }
    }
}
