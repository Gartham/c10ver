/**
 * <p>
 * This package comprises the {@link gartham.c10ver.actions.ActionMessage} API,
 * which is used to display a menu with options that a user can select. The user
 * selects an option by clicking a reaction at the bottom of the message
 * containing the menu.
 * </p>
 * <p>
 * {@link gartham.c10ver.actions.ActionMessage}s essentially represent a menu,
 * where each option is represented by an {@link gartham.c10ver.actions.Action}.
 * This means that, if you want to make a menu with the
 * {@link gartham.c10ver.actions.ActionMessage} API and show it to a user,
 * you'll need to create a new {@link gartham.c10ver.actions.ActionMessage} (use
 * a subclass like {@link gartham.c10ver.actions.SimpleActionMessage} for a
 * quick and dirty menu) and then a few {@link gartham.c10ver.actions.Action}s
 * and add those {@link gartham.c10ver.actions.Action}s to the
 * {@link gartham.c10ver.actions.ActionMessage}. After you're done, you can
 * simply display the {@link gartham.c10ver.actions.ActionMessage} to the user
 * using the message's
 * {@link gartham.c10ver.actions.ActionMessage#send(gartham.c10ver.Clover, net.dv8tion.jda.api.entities.MessageChannel, net.dv8tion.jda.api.entities.User)}
 * method (this sends an {@link gartham.c10ver.actions.ActionMessage} in a
 * channel, and lets only the specified
 * {@link net.dv8tion.jda.api.entities.User} select its options). You can also
 * get the discord embed object by calling the
 * {@link gartham.c10ver.actions.ActionMessage#embed()} method. The embed
 * approach is suitable for simply showing the user the menu, without displaying
 * the reaction options or handling any selections.
 * </p>
 * <p>
 * Here are a few examples of some various
 * {@link gartham.c10ver.actions.ActionMessage}s being shown in discord:
 * 
 * 
 * </p>
 * 
 * @author Gartham
 *
 */
package gartham.c10ver.actions;