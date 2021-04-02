package gartham.c10ver.games.math.building;

import java.math.BigDecimal;

public class Term implements MathExpression {

	private final BigDecimal val;

	public Term(BigDecimal val) {
		this.val = val;
	}

	@Override
	public BigDecimal eval() {
		return val;
	}

}
