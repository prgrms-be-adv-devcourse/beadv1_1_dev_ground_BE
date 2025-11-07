package io.devground.dbay.domain.deposit.entity.vo;

import io.devground.dbay.domain.deposit.entity.Deposit;

public enum DepositHistoryType {

	// 입금
	CHARGE_TRANSFER {
		@Override
		public void apply(Deposit deposit, Long amount) {
			deposit.charge(amount);
		}
	},
	CHARGE_TOSS {
		@Override
		public void apply(Deposit deposit, Long amount) {
			deposit.charge(amount);
		}
	},
	// 출금
	PAYMENT_TOSS {
		@Override
		public void apply(Deposit deposit, Long amount) {
			deposit.withdraw(amount);
		}
	},
	PAYMENT_INTERNAL {
		@Override
		public void apply(Deposit deposit, Long amount) {
			deposit.withdraw(amount);
		}
	},

	// 환불
	REFUND_INTERNAL {
		@Override
		public void apply(Deposit deposit, Long amount) {
			deposit.charge(amount);
		}
	},
	REFUND_TOSS {
		@Override
		public void apply(Deposit deposit, Long amount) {
			deposit.charge(amount);
		}
	},

	// 정산
	SETTLEMENT {
		@Override
		public void apply(Deposit deposit, Long amount) {
			deposit.charge(amount);
		}
	};

	public abstract void apply(Deposit deposit, Long amount);

}