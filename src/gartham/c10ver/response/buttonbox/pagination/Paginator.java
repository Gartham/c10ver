package gartham.c10ver.response.buttonbox.pagination;

import gartham.c10ver.commands.InputProcessor;
import gartham.c10ver.commands.consumers.InputConsumer;
import gartham.c10ver.response.buttonbox.ButtonBox;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;

public class Paginator implements InputConsumer<ButtonClickEvent> {
	private final ButtonBox box;
	private Message message;
	private InputProcessor<ButtonClickEvent> buttonProcessor;

	public Paginator(ButtonBox box) {
		this.box = box;
		
		box.get(0, 0)
	}

	public void attach(Message message, InputProcessor<ButtonClickEvent> buttonProcessor) {
		(this.buttonProcessor = buttonProcessor).registerInputConsumer(this);
		this.message = message;
	}

	public void detach() {
		buttonProcessor.removeInputConsumer(this);
		message = null;
	}

	@Override
	public boolean consume(ButtonClickEvent event, InputProcessor<? extends ButtonClickEvent> processor,
			InputConsumer<ButtonClickEvent> consumer) {
		if (!event.getMessageId().equals(message.getId()))
			return false;
		
		

	}

}
