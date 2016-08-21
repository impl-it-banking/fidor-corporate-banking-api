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

import it.impl.banking.api.account.transaction.Transaction;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Test;

public class TransactionCsvLineParserTest {

    String CSV_LINE_1
            = "15.08.2016;Überweisung: Lebenshaltungskosten;Empfänger: Benjamin Asbach, IBAN: DE17500105170000000000;-123,45";

    @Test
    public void bookingDateIsExtractedFromCsvLine1() throws Exception {
        TransactionCsvLineParser parser = new TransactionCsvLineParser();
        Transaction transaction = parser.parseTransactionCsvLine(CSV_LINE_1);

        assertEquals(LocalDate.parse("15.08.2016", DateTimeFormatter.ofPattern("dd.MM.yyyy")), transaction.getBookingDate());
    }

    @Test
    public void purposeIsExtractedFromCsvLine1() throws Exception {
        TransactionCsvLineParser parser = new TransactionCsvLineParser();
        Transaction transaction = parser.parseTransactionCsvLine(CSV_LINE_1);

        assertEquals("Überweisung: Lebenshaltungskosten", transaction.getPurpose());
    }

    @Test
    public void counterpartyOwnerIsExtractedFromCsvLine1() throws Exception {
        TransactionCsvLineParser parser = new TransactionCsvLineParser();
        Transaction transaction = parser.parseTransactionCsvLine(CSV_LINE_1);

        assertEquals("Benjamin Asbach", transaction.getCounterparty().getOwner());
    }

    @Test
    public void counterpartyIbanIsExtractedFromCsvLine1() throws Exception {
        TransactionCsvLineParser parser = new TransactionCsvLineParser();
        Transaction transaction = parser.parseTransactionCsvLine(CSV_LINE_1);

        assertEquals("DE17500105170000000000", transaction.getCounterparty().getIban());
    }

    @Test
    public void valueIsExtractedFromCsvLine1() throws Exception {
        TransactionCsvLineParser parser = new TransactionCsvLineParser();
        Transaction transaction = parser.parseTransactionCsvLine(CSV_LINE_1);

        assertEquals(BigDecimal.valueOf(-123.45), transaction.getValue());
    }

    String CSV_LINE_2
            = "03.08.2016;Gutschrift FA BAD HOMBURG V D H ERSTATT. OHNE BESTIMMUNG  19,46 EUR;Absender: FA BAD HOMBURG V D H, IBAN: DE67500500000001000124, BIC: HELADEFFXXX;19,46";

    @Test
    public void bookingDateIsExtractedFromCsvLine2() throws Exception {
        TransactionCsvLineParser parser = new TransactionCsvLineParser();
        Transaction transaction = parser.parseTransactionCsvLine(CSV_LINE_2);

        assertEquals(LocalDate.parse("03.08.2016", DateTimeFormatter.ofPattern("dd.MM.yyyy")), transaction.getBookingDate());
    }

    @Test
    public void purposeIsExtractedFromCsvLine2() throws Exception {
        TransactionCsvLineParser parser = new TransactionCsvLineParser();
        Transaction transaction = parser.parseTransactionCsvLine(CSV_LINE_2);

        assertEquals("Gutschrift FA BAD HOMBURG V D H ERSTATT. OHNE BESTIMMUNG  19,46 EUR", transaction.getPurpose());
    }

    @Test
    public void counterpartyOwnerIsExtractedFromCsvLine2() throws Exception {
        TransactionCsvLineParser parser = new TransactionCsvLineParser();
        Transaction transaction = parser.parseTransactionCsvLine(CSV_LINE_2);

        assertEquals("FA BAD HOMBURG V D H", transaction.getCounterparty().getOwner());
    }

    @Test
    public void counterpartyIbanIsExtractedFromCsvLine2() throws Exception {
        TransactionCsvLineParser parser = new TransactionCsvLineParser();
        Transaction transaction = parser.parseTransactionCsvLine(CSV_LINE_2);

        assertEquals("DE67500500000001000124", transaction.getCounterparty().getIban());
    }

    @Test
    public void valueIsExtractedFromCsvLine2() throws Exception {
        TransactionCsvLineParser parser = new TransactionCsvLineParser();
        Transaction transaction = parser.parseTransactionCsvLine(CSV_LINE_2);

        assertEquals(BigDecimal.valueOf(19.46), transaction.getValue());
    }

    String CSV_LINE_3
            = "12.08.2016;MasterCard Transaktion in Höhe von 52,69 EUR bei SB-Tank 5100;\"\";-52,69";

    @Test
    public void bookingDateIsExtractedFromCsvLine3() throws Exception {
        TransactionCsvLineParser parser = new TransactionCsvLineParser();
        Transaction transaction = parser.parseTransactionCsvLine(CSV_LINE_3);

        assertEquals(LocalDate.parse("12.08.2016", DateTimeFormatter.ofPattern("dd.MM.yyyy")), transaction.getBookingDate());
    }

    @Test
    public void purposeIsExtractedFromCsvLine3() throws Exception {
        TransactionCsvLineParser parser = new TransactionCsvLineParser();
        Transaction transaction = parser.parseTransactionCsvLine(CSV_LINE_3);

        assertEquals("MasterCard Transaktion in Höhe von 52,69 EUR bei SB-Tank 5100", transaction.getPurpose());
    }

    @Test
    public void counterpartyOwnerIsExtractedFromCsvLine3() throws Exception {
        TransactionCsvLineParser parser = new TransactionCsvLineParser();
        Transaction transaction = parser.parseTransactionCsvLine(CSV_LINE_3);

        assertEquals("MasterCard", transaction.getCounterparty().getOwner());
    }

    @Test
    public void counterpartyIbanIsExtractedFromCsvLine3() throws Exception {
        TransactionCsvLineParser parser = new TransactionCsvLineParser();
        Transaction transaction = parser.parseTransactionCsvLine(CSV_LINE_3);

        assertNull(transaction.getCounterparty().getIban());
    }

    @Test
    public void valueIsExtractedFromCsvLine3() throws Exception {
        TransactionCsvLineParser parser = new TransactionCsvLineParser();
        Transaction transaction = parser.parseTransactionCsvLine(CSV_LINE_3);

        assertEquals(BigDecimal.valueOf(-52.69), transaction.getValue());
    }

    String CSV_LINE_4
            = "30.06.2016;Kapitalertragsteuer;\"\";-0,01";

    @Test
    public void counterpartyOwnerFidorBankIsExtractedFromCsvLine4() throws Exception {
        TransactionCsvLineParser parser = new TransactionCsvLineParser();
        Transaction transaction = parser.parseTransactionCsvLine(CSV_LINE_4);

        assertEquals("Fidor Bank", transaction.getCounterparty().getOwner());
    }

    String CSV_LINE_5
            = "30.06.2016;Habenzinsen;\"\";0,07";

    @Test
    public void counterpartyOwnerFidorBankIsExtractedFromCsvLine5() throws Exception {
        TransactionCsvLineParser parser = new TransactionCsvLineParser();
        Transaction transaction = parser.parseTransactionCsvLine(CSV_LINE_5);

        assertEquals("Fidor Bank", transaction.getCounterparty().getOwner());
    }
}
