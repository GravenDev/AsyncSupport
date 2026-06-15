package yt.graven.gravensupport.utils.messages.serializable;

import net.dv8tion.jda.api.entities.Message;

public enum MessageType {
  BOT,
  TARGET,
  MODERATION;

  public static MessageType fromMessage(Message message) {
    if (!message.getAuthor().isBot()) {
      return MODERATION;
    } else if (message.getAuthor().getIdLong() == message.getJDA().getSelfUser().getIdLong()) {
      return BOT;
    }
    return TARGET;
  }
}
