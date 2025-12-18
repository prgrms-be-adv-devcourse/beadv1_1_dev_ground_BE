package io.devground.dbay.order.infrastructure.mapper;

import io.devground.core.model.vo.DepositHistoryType;
import io.devground.dbay.order.domain.vo.DepositType;

public class EnumMapper {

    public static DepositHistoryType toCoreDepositHistoryType(DepositType type) {
        return switch (type) {
            case CHARGE_TRANSFER -> DepositHistoryType.CHARGE_TRANSFER;
            case CHARGE_TOSS -> DepositHistoryType.CHARGE_TOSS;
            case PAYMENT_TOSS -> DepositHistoryType.PAYMENT_TOSS;
            case PAYMENT_INTERNAL -> DepositHistoryType.PAYMENT_INTERNAL;
            case REFUND_INTERNAL -> DepositHistoryType.REFUND_INTERNAL;
            case REFUND_TOSS -> DepositHistoryType.REFUND_TOSS;
            case SETTLEMENT -> DepositHistoryType.SETTLEMENT;
        };
    }

    public static DepositType toDepositType(DepositHistoryType type) {
        return switch (type) {
            case CHARGE_TRANSFER -> DepositType.CHARGE_TRANSFER;
            case CHARGE_TOSS -> DepositType.CHARGE_TOSS;
            case PAYMENT_TOSS -> DepositType.PAYMENT_TOSS;
            case PAYMENT_INTERNAL -> DepositType.PAYMENT_INTERNAL;
            case REFUND_INTERNAL -> DepositType.REFUND_INTERNAL;
            case REFUND_TOSS -> DepositType.REFUND_TOSS;
            case SETTLEMENT -> DepositType.SETTLEMENT;
        };
    }
}
