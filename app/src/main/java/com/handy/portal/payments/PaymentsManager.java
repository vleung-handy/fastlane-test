package com.handy.portal.payments;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.handy.portal.core.model.SuccessWrapper;
import com.handy.portal.data.DataManager;
import com.handy.portal.payments.model.BatchPaymentReviewRequest;
import com.handy.portal.payments.model.BookingPaymentReviewRequest;
import com.handy.portal.payments.model.BookingTransactions;
import com.handy.portal.payments.model.CreateDebitCardResponse;
import com.handy.portal.payments.model.NeoPaymentBatch;
import com.handy.portal.payments.model.PaymentBatches;
import com.handy.portal.payments.model.PaymentCashOutInfo;
import com.handy.portal.payments.model.PaymentCashOutRequest;
import com.handy.portal.payments.model.PaymentGroup;
import com.handy.portal.payments.model.PaymentOutstandingFees;
import com.handy.portal.payments.model.PaymentReviewResponse;
import com.handy.portal.payments.model.RequiresPaymentInfoUpdate;
import com.stripe.android.model.Token;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

public class PaymentsManager {
    private final EventBus mBus;
    private final DataManager mDataManager;

    //TODO: We're using a cache for what is currently one value, maybe look into Guava Suppliers in future
    private Cache<String, Boolean> mNeedsUpdatedPaymentInformationCache;
    private static final String NEEDS_UPDATED_PAYMENT_CACHE_KEY = "needs_updated_payment";

    @Inject
    public PaymentsManager(final EventBus bus, final DataManager dataManager) {
        mBus = bus;
        mBus.register(this);
        mDataManager = dataManager;
        mNeedsUpdatedPaymentInformationCache = CacheBuilder.newBuilder()
                .maximumSize(1)
                .expireAfterWrite(1, TimeUnit.DAYS)
                .build();
    }

    public void submitBatchPaymentReviewRequest(@NonNull BatchPaymentReviewRequest paymentSupportRequest,
                                                DataManager.Callback<PaymentReviewResponse> cb) {
        mDataManager.submitPaymentBatchReviewRequest(paymentSupportRequest, cb);
    }

    public void submitBookingPaymentReviewRequest(@NonNull BookingPaymentReviewRequest bookingPaymentReviewRequest,
                                                  DataManager.Callback<PaymentReviewResponse> cb) {
        mDataManager.submitBookingPaymentTransactionRequest(bookingPaymentReviewRequest, cb);
    }

    @Subscribe
    public void onRequestShouldUserUpdatePaymentInfo(PaymentEvent.RequestShouldUserUpdatePaymentInfo event) {
        Boolean cachedValue = mNeedsUpdatedPaymentInformationCache.getIfPresent(NEEDS_UPDATED_PAYMENT_CACHE_KEY);
        if (cachedValue != null) {
            mBus.post(new PaymentEvent.ReceiveShouldUserUpdatePaymentInfoSuccess(cachedValue));
        }
        else {
            mDataManager.getNeedsToUpdatePaymentInfo(new DataManager.Callback<RequiresPaymentInfoUpdate>() {
                @Override
                public void onSuccess(RequiresPaymentInfoUpdate response) {
                    mBus.post(new PaymentEvent.ReceiveShouldUserUpdatePaymentInfoSuccess(response.getNeedsUpdate()));
                    mNeedsUpdatedPaymentInformationCache.put(NEEDS_UPDATED_PAYMENT_CACHE_KEY, response.getNeedsUpdate());
                }

                @Override
                public void onError(DataManager.DataManagerError error) {
                    mBus.post(new PaymentEvent.ReceiveShouldUserUpdatePaymentInfoError(error));
                }
            });
        }
    }

    public void onRequestBookingPaymentDetails(@NonNull final String bookingId,
                                               @NonNull final String bookingType,
                                               @NonNull final DataManager.Callback<BookingTransactions> callback) {
        mDataManager.getBookingTransactions(bookingId, bookingType.toLowerCase(), callback);
    }

    public void requestPaymentCashOutInfo(final DataManager.Callback<PaymentCashOutInfo> cb) {
        mDataManager.getPaymentCashOutInfo(cb);
    }

    public void requestCashOut(
            @NonNull PaymentCashOutRequest paymentCashOutRequest,
            @NonNull final DataManager.Callback<SuccessWrapper> callback) {
        mDataManager.requestPaymentCashOut(paymentCashOutRequest, callback);
    }

    /**
     * request next N payment batches
     *
     * @param lastBatchId the id of the last batch in the current page. the server will use this to get the next page
     * @param pageSize    the maximum number of batches to return
     */
    public void requestPaymentBatchesPage(@Nullable final Integer lastBatchId,
                                          int pageSize,
                                          @NonNull final DataManager.Callback<PaymentBatches> callback) {
        mDataManager.getPaymentBatchesPage(lastBatchId, pageSize, new DataManager.Callback<PaymentBatches>() {
            @Override
            public void onSuccess(@NonNull PaymentBatches paymentBatches) {
                //for now, filter non-legacy payment batches to remove empty groups until server side changes are made
                removeEmptyGroupsFromPaymentBatches(paymentBatches);
                callback.onSuccess(paymentBatches);
            }

            @Override
            public void onError(DataManager.DataManagerError error) {
                callback.onError(error);
            }
        });
    }

    //fixme remove
    @Deprecated
    public void requestPaymentBatches(@NonNull final Date startDate,
                                      @NonNull final Date endDate,
                                      @NonNull final DataManager.Callback<PaymentBatches> callback) {
        //assuming startDate is inclusive and endDate is inclusive
        mDataManager.getPaymentBatches(startDate, endDate, new DataManager.Callback<PaymentBatches>() {
            @Override
            public void onSuccess(PaymentBatches paymentBatches) {
                //for now, filter non-legacy payment batches to remove empty groups until server side changes are made
                removeEmptyGroupsFromPaymentBatches(paymentBatches);
                callback.onSuccess(paymentBatches);
            }

            @Override
            public void onError(DataManager.DataManagerError error) {
                callback.onError(error);
            }
        });
    }

    /**
     * for now, filter non-legacy payment batches to remove empty groups until server side changes are made
     */
    private void removeEmptyGroupsFromPaymentBatches(@NonNull PaymentBatches paymentBatches) {
        NeoPaymentBatch neoPaymentBatches[] = paymentBatches.getNeoPaymentBatches();
        for (NeoPaymentBatch neoPaymentBatch : neoPaymentBatches) {
            PaymentGroup paymentGroups[] = neoPaymentBatch.getPaymentGroups();
            List<PaymentGroup> paymentGroupList = new LinkedList<>();
            for (PaymentGroup paymentGroup : paymentGroups) {
                if (paymentGroup.getPayments() != null && paymentGroup.getPayments().length > 0) {
                    paymentGroupList.add(paymentGroup);
                }
            }
            neoPaymentBatch.setPaymentGroups(paymentGroupList.toArray(new PaymentGroup[paymentGroupList.size()]));
        }
    }

    @Subscribe
    public void onRequestPaymentOutstandingFees(final PaymentEvent.RequestPaymentOutstandingFees event) {
        mDataManager.getPaymentOutstandingFees(new DataManager.Callback<PaymentOutstandingFees>() {
            @Override
            public void onSuccess(final PaymentOutstandingFees response) {
                mBus.post(new PaymentEvent.ReceivePaymentOutstandingFeesSuccess(response));
            }

            @Override
            public void onError(DataManager.DataManagerError error) {
                mBus.post(new PaymentEvent.ReceivePaymentOutstandingFeesError(error));
            }
        });
    }

    @Subscribe
    public void onRequestCreateBankAccount(final PaymentEvent.RequestCreateBankAccount event) {
        mDataManager.createBankAccount(buildParamsForCreateBankAccount(event.stripeToken, event.taxId, event.accountNumberLast4Digits), new DataManager.Callback<SuccessWrapper>() {
            @Override
            public void onSuccess(SuccessWrapper successWrapper) {
                mBus.post(new PaymentEvent.ReceiveCreateBankAccountSuccess(successWrapper.getSuccess()));
            }

            @Override
            public void onError(DataManager.DataManagerError error) {
                mBus.post(new PaymentEvent.ReceiveCreateBankAccountError(error));
            }
        });
    }

    @Subscribe
    public void onRequestCreateDebitCardRecipient(final PaymentEvent.RequestCreateDebitCardRecipient event) {
        mDataManager.createDebitCardRecipient(buildParamsForDebitCardRecipient(event.stripeToken, event.taxId, event.cardNumberLast4Digits, event.expMonth, event.expYear), new DataManager.Callback<SuccessWrapper>() {
            @Override
            public void onSuccess(SuccessWrapper successWrapper) {
                mBus.post(new PaymentEvent.ReceiveCreateDebitCardRecipientSuccess(successWrapper.getSuccess()));
            }

            @Override
            public void onError(DataManager.DataManagerError error) {
                mBus.post(new PaymentEvent.ReceiveCreateDebitCardRecipientError(error));
            }
        });
    }

    @Subscribe
    public void onRequestCreateDebitCardForCharge(final PaymentEvent.RequestCreateDebitCardForCharge event) {
        mDataManager.createDebitCardForCharge(event.stripeToken, new DataManager.Callback<CreateDebitCardResponse>() {
            @Override
            public void onSuccess(CreateDebitCardResponse response) {
                mBus.post(new PaymentEvent.ReceiveCreateDebitCardForChargeSuccess(response));
            }

            @Override
            public void onError(DataManager.DataManagerError error) {
                mBus.post(new PaymentEvent.ReceiveCreateDebitCardForChargeError(error));
            }
        });
    }

    @Subscribe
    public void onRequestUpdateCreditCard(final PaymentEvent.RequestUpdateCreditCard event) {
        final Token token = event.getToken();
        mDataManager.updateCreditCard(token.getId(), new DataManager.Callback<SuccessWrapper>() {
            @Override
            public void onSuccess(final SuccessWrapper response) {
                mBus.post(new PaymentEvent.ReceiveUpdateCreditCardSuccess());
            }

            @Override
            public void onError(final DataManager.DataManagerError error) {
                mBus.post(new PaymentEvent.ReceiveUpdateCreditCardError(error));
            }
        });
    }

    private final class ParamKeys {
        static final String STRIPE_TOKEN = "token";
        static final String TAX_ID = "tax_id";
        static final String ACCOUNT_NUMBER_LAST4_DIGITS = "last4";
        static final String EXP_MONTH = "exp_month";
        static final String EXP_YEAR = "exp_year";
        static final String ACCOUNT_TYPE = "account_type";
    }


    private final class PaymentMethodAccountType {
        static final String DEBIT_CARD = "debit_card";
        static final String BANK_ACCOUNT = "bank_account";
    }

    private Map<String, String> buildParamsForDebitCardRecipient(String stripeToken, String taxId, String cardNumberLast4Digits, String expMonth, String expYear) {
        Map<String, String> params = new HashMap<>();
        params.put(ParamKeys.STRIPE_TOKEN, stripeToken);
        params.put(ParamKeys.TAX_ID, taxId);
        params.put(ParamKeys.ACCOUNT_NUMBER_LAST4_DIGITS, cardNumberLast4Digits);
        params.put(ParamKeys.EXP_MONTH, expMonth);
        params.put(ParamKeys.EXP_YEAR, expYear);
        params.put(ParamKeys.ACCOUNT_TYPE, PaymentMethodAccountType.DEBIT_CARD);
        return params;
    }

    private Map<String, String> buildParamsForCreateBankAccount(String stripeToken, String taxId, String accountNumberLast4Digits) {
        Map<String, String> params = new HashMap<>();
        params.put(ParamKeys.STRIPE_TOKEN, stripeToken);
        params.put(ParamKeys.TAX_ID, taxId);
        params.put(ParamKeys.ACCOUNT_NUMBER_LAST4_DIGITS, accountNumberLast4Digits);
        params.put(ParamKeys.ACCOUNT_TYPE, PaymentMethodAccountType.BANK_ACCOUNT);
        return params;
    }
}
