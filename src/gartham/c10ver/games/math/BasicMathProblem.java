package gartham.c10ver.games.math;

import java.math.BigDecimal;

public class BasicMathProblem implements MathProblem {

	private final String problem;
	private final BigDecimal answer;

	public BasicMathProblem(String problem, BigDecimal answer) {
		this.problem = problem;
		this.answer = answer;
	}

	@Override
	public String problem() {
		return problem;
	}

	@Override
	public boolean check(String result) {
		try {
			return answer.equals(new BigDecimal(result));
		} catch (NumberFormatException e) {
			return false;
		}
	}

}
