//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.routes;

/**
 * Resource paths.
 */
public final class Paths {
    //
    // Orchestrator core:
    //
    public static final String ALERT_SUBSCRIPTIONS = "alertsubscriptions";
    public static final String AUTHENTICATE_ID     = "authenticate-id";
    public static final String DEVICE_ENROLLMENT   = "device-enrollment";
    public static final String DIRECT_BANK         = "directbank";
    public static final String GIFT_CARDS          = "giftcards";
    public static final String HEALTH              = "health";
    public static final String MFA                 = "mfa";
    public static final String PAYMENT_CARDS       = "paymentcards";
    public static final String SURE_SWIPE          = "sure-swipe";
    public static final String TOUCH_ID            = "touch-id";
    public static final String USERS               = "users";
    public static final String VERIFY              = "verify";
    public static final String VERIFY_ANSWERS      = "verify-answers";
    public static final String VERIFY_PASSWORD     = "verify-password";
    public static final String VERIFY_PIN          = "verify-pin";
    public static final String BOOKMARK            = "bookmark";
    public static final String PROVISION           = "provision";
    public static final String V1                  = "v1";


    public static final String GIFT_CARD_ID_PATH_PARAMETER   = "giftCardId";
    public static final String GIFT_CARD_ID_PATH_TEMPLATE    = '{' + GIFT_CARD_ID_PATH_PARAMETER + '}';
    public static final String PAYMENT_CARD_ID_PATH_PARAMETER = "paymentCardId";
    public static final String PAYMENT_CARD_ID_PATH_TEMPLATE  = '{' + PAYMENT_CARD_ID_PATH_PARAMETER + '}';
    public static final String PRODUCT_ID_PATH_PARAMETER      = "productId";
    public static final String PRODUCT_ID_PATH_TEMPLATE       = '{' + PRODUCT_ID_PATH_PARAMETER + '}';
    public static final String USER_ID_PATH_PARAMETER         = "userId";
    public static final String USER_ID_PATH_TEMPLATE          = '{' + USER_ID_PATH_PARAMETER + '}';

    public static final String BOOKMARK_PATH_PARAMETER      = "bookmark";
    public static final String BOOKMARK_PATH_TEMPLATE       = '{' + BOOKMARK_PATH_PARAMETER +'}';

    public static final String PROVISION_PATH_PARAMETER      = "provision";
    public static final String PROVISION_PATH_TEMPLATE       = '{' + PROVISION_PATH_PARAMETER +'}';
    //
    // Passbook web service:
    //
    public static final String PASSBOOK_DEVICE_LIBRARY_IDENTIFIER_PATH_PARAMETER = "deviceLibraryIdentifier";
    public static final String PASSBOOK_DEVICE_LIBRARY_IDENTIFIER_PATH_TEMPLATE  = '{' + PASSBOOK_DEVICE_LIBRARY_IDENTIFIER_PATH_PARAMETER + '}';
    public static final String PASSBOOK_PASS_TYPE_IDENTIFIER_PATH_PARAMETER      = "passTypeIdentifier";
    public static final String PASSBOOK_PASS_TYPE_IDENTIFIER_PATH_TEMPLATE       = '{' + PASSBOOK_PASS_TYPE_IDENTIFIER_PATH_PARAMETER + '}';
    public static final String PASSBOOK_PUSH_TOKEN_PATH_PARAMETER                = "pushToken";
    public static final String PASSBOOK_REGISTRATIONS_FOR_PASS_TYPE_PATH         = PASSBOOK_DEVICE_LIBRARY_IDENTIFIER_PATH_TEMPLATE + "/registrations/" + PASSBOOK_PASS_TYPE_IDENTIFIER_PATH_TEMPLATE;
    public static final String PASSBOOK_SERIAL_NUMBER_PATH_PARAMETER             = "serialNumber";
    public static final String PASSBOOK_SERIAL_NUMBER_PATH_TEMPLATE              = '{' + PASSBOOK_SERIAL_NUMBER_PATH_PARAMETER + '}';
    public static final String PASSBOOK_LATEST_VERSION_OF_PASS_PATH              = PASSBOOK_PASS_TYPE_IDENTIFIER_PATH_TEMPLATE + '/' + PASSBOOK_SERIAL_NUMBER_PATH_TEMPLATE;
    public static final String PASSBOOK_REGISTRATIONS_FOR_SERIAL_NUMBER_PATH     = PASSBOOK_REGISTRATIONS_FOR_PASS_TYPE_PATH + '/' + PASSBOOK_SERIAL_NUMBER_PATH_TEMPLATE;

    //
    // Card Notification Service proxy:
    //
    public static final String CNS_APP_ID_PATH_PARAMETER            = "appId";
    public static final String CNS_APP_ID_PATH_TEMPLATE             = '{' + CNS_APP_ID_PATH_PARAMETER            + '}';
    public static final String CNS_DEVICE_IDENTIFIER_PATH_PARAMETER = "deviceIdentifier";
    public static final String CNS_DEVICE_IDENTIFIER_PATH_TEMPLATE  = '{' + CNS_DEVICE_IDENTIFIER_PATH_PARAMETER + '}';
    public static final String CNS_DPAN_IDENTIFIER_PATH_PARAMETER   = "dpanIdentifier";
    public static final String CNS_DPAN_IDENTIFIER_PATH_TEMPLATE    = '{' + CNS_DPAN_IDENTIFIER_PATH_PARAMETER   + '}';

    private Paths() {}
}
