//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.jaxrs

import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertFalse

final class LoggingReplacerUnitTest {
    private LoggingReplacer loggingReplacer
    @Before
    void before() {
        System.setProperty('config.name', 'common')
        System.setProperty('common.env', 'test')
        loggingReplacer = LoggingReplacer.instance
    }

    @Test
    void testCreditCardNumber1() {
        String s = '''
{
    "accountClosed": false,
    "accountUseDesc": "Personal",
    "applicationFraud": false,
    "availableCredit": 333.74,
    "cardImageCode": "IMG_CD",
    "cardTypeId": "3088",
    "cashAdvanceAvailableCredit": 333.74,
    "cashAdvanceCreditLimit": 500.0,
    "chargedOff": false,
    "closedStatusCode": "Open",
    "countryCode": "USA",
    "creditCardExpirationDate": "2016-08-23",
    "creditCardNumber": "4862365555555555",
    "creditLimit": 1801.0,
    "creditRevoked": false,
    "currentMinimumAmountDue": 0,
    "customerDeceased": false,
    "electronicStatementsOnlyInd": false,
    "inBankruptcy": false,
    "inCollections": false,
    "lastBillingCycleDate": "2015-01-19",
    "lastPaymentAmount": 300.0,
    "lastPaymentDate": "2015-01-22",
    "lastStatementDate": "2015-01-19",
    "lastStatementEndingBalance": 1326.91,
    "lastStatementMinimumDueAmount": 31.0,
    "minimumPaymentDueDate": "2015-02-16",
    "onOfacList": false,
    "openDate": "2006-02-01",
    "overlimitAccountInd": false,
    "pastDueAccountInd": false,
    "presentBalance": 1132.2,
    "productDesc": "VISA PLATINUM",
    "rewardsEnrolled": false,
    "securityFraud": false,
    "suspectedFraud": false,
    "systemMaintenanceMode": false
}
        '''
        s = loggingReplacer.replaceAll(s)
        assertFalse(s.contains('4862365555555555'))
    }

    @Test
    void testCreditCardNumber2() {
        String s = '''
{"creditCardNumber":"4862365555555555", "creditCardExpirationDate":"2016-08-23","productDesc":"VISA PLATINUM","countryCode":"USA","openDate":"2006-02-01","presentBalance":1132.20,"accountUseDesc":"Personal","currentMinimumAmountDue":0,"minimumPaymentDueDate":"2015-02-16","creditLimit":1801.00,"availableCredit":333.74,"cashAdvanceAvailableCredit":333.74,"cashAdvanceCreditLimit":500.00,"overlimitAccountInd":false,"lastStatementMinimumDueAmount":31.00,"pastDueAccountInd":false,"lastPaymentDate":"2015-01-22","lastPaymentAmount":300.00,"lastStatementEndingBalance":1326.91,"lastStatementDate":"2015-01-19","lastBillingCycleDate":"2015-01-19","electronicStatementsOnlyInd":false,"inBankruptcy":false,"suspectedFraud":false,"applicationFraud":false,"onOfacList":false,"customerDeceased":false,"securityFraud":false,"chargedOff":false,"creditRevoked":false,"rewardsEnrolled":false,"accountClosed":false,"closedStatusCode":"Open","inCollections":false,"cardTypeId":"3088","cardImageCode":"IMG_CD","systemMaintenanceMode":false}
        '''
        s = loggingReplacer.replaceAll(s)
        assertFalse(s.contains('4862365555555555'))
    }

    @Test
    void testTaxID1() {
        String s = '''
{
    "accountCycle": "09 BUSINESS DAY",
    "accountNumber": "8231735837",
    "accountReferenceId": "16--8231735837",
    "accountStatus": "Active",
    "accountTitles": {
        "accountTitleLine1": "CHARLES U MONTGOMERY OR",
        "accountTitleLine2": "LAKESHA N PALMER"
    },
    "accountTransactionsURL": {
        "href": "https://cllappcap-rtm.kdc.capitalone.com/deposits/accounts/16--8231735837/transactions",
        "method": "GET",
        "type": "application/json,application/xml"
    },
    "address": {
        "city": "HOUSTON",
        "countryCode": "US",
        "countryName": "United States",
        "line1": "5300 COKE",
        "line2": "APT 154",
        "postalCode": "77020",
        "state": "TX"
    },
    "analysisCode": "Account Not Used for Analysis",
    "availableBalance": -75.24,
    "bankABANumber": "113024915",
    "bankFloatAmount": 96.0,
    "bankNumber": "30",
    "bankNumberDescription": "TX",
    "branchAreaCode": 37,
    "branchAreaDescription": "Texas",
    "branchCostCenter": "41713",
    "branchName": "Fallcreek-Retail",
    "businessLine": "Deposit",
    "collectedBalance": 443.8,
    "currencyCode": "USD",
    "currentBalance": -75.24,
    "customerFloat": {
        "day1AvailableAmount": 0.0,
        "day2AvailableAmount": 0.0,
        "day3AvailableAmount": 0.0,
        "day4AvailableAmount": 0.0,
        "day5AvailableAmount": 0.0,
        "day6AvailableAmount": 0.0,
        "day7AvailableAmount": 0.0,
        "totalFloatAmount": 0.0
    },
    "holdsIndicator": false,
    "interestAccruedAmount": 0.0,
    "interestEarnedLastYear": 0.0,
    "interestEarnedYTD": 0.0,
    "interestRate": 0.0,
    "interestRatePointer": "NONE",
    "lastActivityDate": "2015-01-23T00:00:00.000-0500",
    "lastMaintenanceDate": "2014-11-05T00:00:00.000-0500",
    "lastStatementBalance": 1046.63,
    "lastStatementDate": "2015-01-14T00:00:00.000-0500",
    "ledgerBalance": 539.8,
    "openDate": "2014-11-05T00:00:00.000-0500",
    "openMethod": "Face To Face",
    "openingDepositAmount": 50.0,
    "product": {
        "productClassCode": "DP",
        "productClassDescription": "Deposit",
        "productId": "IM218",
        "productName": "Rewards Checking",
        "productTypeCode": "DDA",
        "productTypeDescription": "Checking"
    },
    "restrictionsIndicator": false,
    "statementType": "Electronic",
    "stopPaymentsIndicator": false,
    "taxId": "123456789",
    "taxIdType": "SSN",
    "totalHoldAmount": 0.0,
    "totalPendingCredits": 0.0,
    "totalPendingDebits": 615.04,
    "withholdingAmountYTD": 0.0,
    "withholdingReason": ""
}
        '''
        s = loggingReplacer.replaceAll(s)
        assertFalse(s.contains('4862365555555555'))
    }
}
