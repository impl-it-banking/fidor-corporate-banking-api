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

import it.impl.banking.api.account.transaction.TransactionConstraint;
import it.impl.banking.api.account.transaction.TransactionConstraintFactory;
import it.impl.banking.fidor.corporate.api.TransactionCsvProcessor;
import java.time.LocalDate;
import lombok.Setter;

public class FidorTransactionConstraintFactory implements TransactionConstraintFactory {

    @Setter
    private TransactionCsvProcessor transactionCsvProcessor;

    @Override
    public boolean constraintTypeIsSupported(TransactionConstraint.Type type) {
        switch (type) {
            case DAYS_BEFORE_TODAY:
            case DATE_PERIOD:
                return true;
            default:
                return false;
        }
    }

    @Override
    public TransactionConstraint createDaysBeforeTodayTransactionConstraint(int days) {
        return new FidorDaysBeforeTodayTransactionConstraint(
                TransactionConstraint.Type.DAYS_BEFORE_TODAY,
                transactionCsvProcessor,
                days
        );
    }

    @Override
    public TransactionConstraint createDatePeriodTransactionConstraint(LocalDate from, LocalDate until) {
        return new FidorDatePeriodTransactionContraint(
                TransactionConstraint.Type.DATE_PERIOD,
                transactionCsvProcessor,
                from,
                until
        );
    }
}
