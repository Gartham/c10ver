package gartham.c10ver.economy.questions;

import java.math.BigInteger;

import org.alixia.javalibrary.json.JSONObject;

import gartham.c10ver.data.PropertyObject;

public class Question extends PropertyObject {
	private final Property<String> question = stringProperty("question");
	private final Property<BigInteger> value = bigIntegerProperty("value");
	private final Property<Difficulty> difficulty = enumProperty("difficulty", Difficulty.class);

	public Question(JSONObject data) {
		load(data);
	}

	public Question(String question, BigInteger value, Difficulty difficulty) {
		setQuestion(question);
		setValue(value);
		setDifficulty(difficulty);
	}

	public String getQuestion() {
		return question.get();
	}

	public void setQuestion(String question) {
		this.question.set(question);
	}

	public BigInteger getValue() {
		return value.get();
	}

	public void setValue(BigInteger value) {
		this.value.set(value);
	}

	public Difficulty getDifficulty() {
		return difficulty.get();
	}

	public void setDifficulty(Difficulty difficulty) {
		this.difficulty.set(difficulty);
	}

	public enum Difficulty {
		EASY, MEDIUM, HARD;

		@Override
		public String toString() {
			return name().charAt(0) + name().toLowerCase().substring(1);
		}
	}
}
