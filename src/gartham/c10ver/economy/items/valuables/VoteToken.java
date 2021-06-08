package gartham.c10ver.economy.items.valuables;

import org.alixia.javalibrary.json.JSONObject;

import gartham.c10ver.economy.items.Item;

public class VoteToken extends Item {
	public static final String ITEM_TYPE = "vote-token", ITEM_NAME = "Vote Token";
	private static final String TOKEN_TYPE_PK = "type";

	{
		enumProperty(TOKEN_TYPE_PK, Type.class);
		setItemName(ITEM_NAME);
	}

	public enum Type {
		RED("<:RedVoteToken:851408233759506432>", "Red"), GOLD("<:GoldVoteToken:851408233716383784>", "Gold"),
		NORMAL("<:VoteToken:851408233998450688>");

		private Type(String icon, String name) {
			this.icon = icon;
			this.name = name;
		}

		private Type(String icon) {
			this(icon, null);
		}

		private static String getName(String name) {
			String res = name.charAt(0) + name.substring(1).toLowerCase();
			return res;
		}

		private final String icon, name;

		public String getIcon() {
			return icon;
		}

		public String getName() {
			return name;
		}
	}

	private Property<Type> tokenTypeProperty() {
		return getProperty(TOKEN_TYPE_PK);
	}

	public Type getTokenType() {
		return tokenTypeProperty().get();
	}

	public VoteToken(Type type) {
		super(ITEM_TYPE);
		tokenTypeProperty().set(type);
		setIcon(type.icon);
		if (type.name != null)
			setCustomName(ITEM_NAME + " - " + type.name);
	}

	public VoteToken(JSONObject props) {
		super(ITEM_TYPE, props);
		load(tokenTypeProperty(), props);
		setIcon(getTokenType().icon);
		if (getTokenType().name != null)
			setCustomName(ITEM_NAME + " - " + getTokenType().name);
	}

}
