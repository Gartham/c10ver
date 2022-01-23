package gartham.c10ver.games.rpg.wilderness;

/**
 * Represents a type of link between two {@link WildernessTile}s. Instances are
 * used to get a {@link WildernessTile} that is in some way connected to another
 * {@link WildernessTile}. The most common type of connection is via physical
 * adjacency, which is captured by {@link AdjacencyLink}.
 * 
 * @author Gartham
 *
 */
public interface LinkType {
	enum AdjacencyLink implements LinkType {
		TOP, RIGHT, BOTTOM, LEFT;
	}
}
