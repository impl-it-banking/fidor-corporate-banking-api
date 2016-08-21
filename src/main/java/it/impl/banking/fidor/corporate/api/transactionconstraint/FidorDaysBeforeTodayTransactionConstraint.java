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
import java.util.List;

public class FidorDaysBeforeTodayTransactionConstraint extends TransactionConstraint {

    final static String FIDOR_CORPORATE_TRANSACTION_CSV_PAGE
            = "https://banking.fidor.de/smart-account/transactions.csv?days=%d&time_selection=days";

    private final TransactionCsvProcessor transactionCsvProcessor;

    private final int daysBeforeToday;

    public FidorDaysBeforeTodayTransactionConstraint(
            Type type,
            TransactionCsvProcessor transactionCsvProcessor,
            int daysBeforeToday) {
        super(type);
        this.transactionCsvProcessor = transactionCsvProcessor;
        this.daysBeforeToday = daysBeforeToday;
    }

    @Override
    public List<Transaction> getTransactionsFilteredByConstraint() throws TransactionConstraintException {
        String transactionsCsvPageByDays = String.format(FIDOR_CORPORATE_TRANSACTION_CSV_PAGE, daysBeforeToday);

        try {
            return transactionCsvProcessor.parseTransactionsCsv(transactionsCsvPageByDays);
        } catch (ParseException | IOException ex) {
            throw new TransactionConstraintException(ex);
        }
    }
}
