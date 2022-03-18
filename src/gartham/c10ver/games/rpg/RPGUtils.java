package gartham.c10ver.games.rpg;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

public class RPGUtils {
	private static final String[][] BARS = { { "<:HealthFront:856774887379959818>" },
			{ "<:HealthSectionEmpty:856778113206452254>", "<:HealthSection12_5p:856774887296991253>",
					"<:HealthSection25p:856774887423344660>", "<:HealthSection37_5p:856774887388610561>",
					"<:HealthSection50p:856774886998409268>", "<:HealthSection62_5p:856774887103266847>",
					"<:HealthSection75p:856774887179943937>", "<:HealthSection87_5p:856774887565033482>",
					"<:HealthSectionFull:856774887439990834>" },
			{ "<:HealthBackEmpty:856774887377076274>", "<:HealthBackFull:856774887137345547>" } },
			LARGE_BARS = { { "<:HealthFrontMedium:863306492967256104>" }, {
					"<:HealthSectionEmptyMedium:863306492958081044>", "<:HealthSection12_5pMedium:863306492936716289>",
					"<:HealthSection25pMedium:863306492673392671>", "<:HealthSection37_5pMedium:863306492941172756>",
					"<:HealthSection50pMedium:863306492592914454>", "<:HealthSection62_5pMedium:863306492828319745>",
					"<:HealthSection75pMedium:863306492961488896>", "<:HealthSection87_5pMedium:863306493120872479>",
					"<:HealthSectionFullMedium:863306492949823488>" },
					{ "<:HealthBackEmptyMedium:863306492941565982>", "<:HealthBackFullMedium:863306492714156053>" } };

	public static String calcHealthbar(BigInteger health, BigInteger maxHealth) {
		StringBuilder bar = new StringBuilder(LARGE_BARS[0][0]);
		if (health.equals(maxHealth)) {
			bar.append(LARGE_BARS[1][LARGE_BARS[1].length - 1]);
			bar.append(LARGE_BARS[2][1]);
		} else {
			bar.append(LARGE_BARS[1][health.equals(BigInteger.ZERO) ? 0
					: new BigDecimal(health.multiply(BigInteger.valueOf(LARGE_BARS[1].length - 1)))
							.divide(new BigDecimal(maxHealth), RoundingMode.HALF_UP).setScale(0, RoundingMode.HALF_UP)
							.intValue()]);
			bar.append(LARGE_BARS[2][0]);
		}

		return bar.toString();
	}

	public static final long CLOVER_WILDERNESS_RANDOM_SEED_TERRAIN_SOFTMAP = 11,
			CLOVER_WILDERNESS_RANDOM_SEED_TERRAIN_HARDMAP = CLOVER_WILDERNESS_RANDOM_SEED_TERRAIN_SOFTMAP + 1,
			BURNING_GRASSLANDS_FIRE = CLOVER_WILDERNESS_RANDOM_SEED_TERRAIN_SOFTMAP + 1;
}
