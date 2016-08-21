This is an api for accessing fidor corporate bank accounts.

##License
This project is licensed under the APACHE LICENSE, VERSION 2.0

##Usage
Please note that we currently do not deploy to maven central. Therefore the project needs to be compiled from source.
```
git clone https://github.com/impl-it-banking/fidor-corporate-banking-api.git
cd fidorbusiness-banking-api
mvn clean install
```

After that you can use the library as maven dependency
```xml
<dependency>
    <groupId>it.impl.banking</groupId>
    <artifactId>fidor-corporate-banking-api</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

### Sign In
```java
WebClient webClient = new WebClient();

FidorWebClient fidorWebClient = new FidorWebClient();
fidorWebClient.setWebClient(webClient);
fidorWebClient.signIn(new MailAddressPasswordToken("max.mustermann@mailprovider.de", "geheimnis"));
```

### Get your current balance
```java
fidorWebClient.getBalance();
```

### Get your current IBAN
```java
fidorWebClient.getIban();
```

### Get a list of transctions
Currently there are two flavours accessing transactions:
1) By days before today
2) By period between two dates

#### Get transactions by days before today
```java
TransactionCsvProcessor transactionCsvProcessor = new TransactionCsvProcessor();
transactionCsvProcessor.setTransactionCsvLineParser(new TransactionCsvLineParser());
//ensure that you insert the same webClient as in fidorWebClient
transactionCsvProcessor.setWebClient(webClient);

FidorTransactionConstraintFactory constraintFactory = new FidorTransactionConstraintFactory();
constraintFactory.setTransactionCsvProcessor(transactionCsvProcessor);

TransactionConstraint constraint = constraintFactory.createDaysBeforeTodayTransactionConstraint(60);
fidorWebClient.getTransactions(constraint);
```

#### Get transactions by period betweend to dates
```java
TransactionCsvProcessor transactionCsvProcessor = new TransactionCsvProcessor();
transactionCsvProcessor.setTransactionCsvLineParser(new TransactionCsvLineParser());
//ensure that you insert the same webClient as in fidorWebClient
transactionCsvProcessor.setWebClient(webClient);

FidorTransactionConstraintFactory constraintFactory = new FidorTransactionConstraintFactory();
constraintFactory.setTransactionCsvProcessor(transactionCsvProcessor);

TransactionConstraint constraint = constraintFactory.createDatePeriodTransactionConstraint(
        LocalDate.of(2016, Month.JANUARY, 13),
        LocalDate.of(2016, Month.AUGUST, 22)
);
fidorWebClient.getTransactions(constraint);
```

### Sign Out
```java
fidorWebClient.signOut();
```
