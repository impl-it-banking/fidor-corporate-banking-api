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

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import it.impl.banking.api.account.transaction.Transaction;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import lombok.Setter;

public class TransactionCsvProcessor {

    @Setter
    private WebClient webClient;

    @Setter
    private TransactionCsvLineParser transactionCsvLineParser;

    public List<Transaction> parseTransactionsCsv(String csvPageUrl) throws IOException, ParseException {
        List<Transaction> transactions = new ArrayList<>();

        Page transactionsPage = webClient.getPage(csvPageUrl);

        try (InputStreamReader csvReader = new InputStreamReader(
                transactionsPage.getWebResponse().getContentAsStream(),
                "ISO-8859-1")) {
            try (BufferedReader reader = new BufferedReader(csvReader)) {
                skipHeaderLine(reader);

                String csvLine;
                while ((csvLine = reader.readLine()) != null) {
                    Transaction transaction = transactionCsvLineParser.parseTransactionCsvLine(csvLine);
                    transactions.add(transaction);
                }
            }
        }

        return transactions;
    }

    private void skipHeaderLine(BufferedReader reader) throws IOException {
        reader.readLine();
    }
}
