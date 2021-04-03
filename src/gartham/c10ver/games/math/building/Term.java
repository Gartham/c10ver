package gartham.c10ver.games.math.building;

import java.math.BigDecimal;

public final class Term implements MathExpression {

	private final BigDecimal val;

	public static Term of(String val) {
		return new Term(new BigDecimal(val));
	}

	public static Term of(BigDecimal val) {
		return new Term(val);
	}

	public static Term of(long val) {
		return new Term(BigDecimal.valueOf(val));
	}

	public static Term of(double val) {
		return new Term(BigDecimal.valueOf(val));
	}

	public Term(BigDecimal val) {
		this.val = val;
	}

	@Override
	public BigDecimal eval() {
		return val;
	}

	@Override
	public String problem() {
		return val.toPlainString();
	}

}
