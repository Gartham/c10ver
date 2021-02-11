package gartham.c10ver.utils;

import java.util.List;

public final class Paginator {
	public static <E> List<E> paginate(int page, int itemsPerPage, List<E> items) {
		if (items.size() == 0 && page == 1)
			return items.subList(0, 0);
		int item = (page - 1) * itemsPerPage;
		int maxPage = maxPage(itemsPerPage, items);
		if (page < 1 || page > maxPage)
			return null;

		return items.subList(item, Math.min(item + itemsPerPage, items.size()));
	}

	public static int maxPage(int itemsPerPage, List<?> items) {
		return maxPage(itemsPerPage, items.size());
	}

	public static int maxPage(int itemsPerPage, int listSize) {
		return (listSize + itemsPerPage - 1) / itemsPerPage;
	}
}
