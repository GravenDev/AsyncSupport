package yt.graven.gravensupport.utils.messages.serializable;

import com.google.gson.annotations.Expose;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SerializableMessage {
  @Expose private SerializableMessageAuthor author = new SerializableMessageAuthor();

  @Expose private long creationTimestamp = Instant.EPOCH.toEpochMilli();

  @Expose private String content = "";

  @Expose private List<String> attachmentUrls = new ArrayList<>();

  @Expose private boolean edited = false;

  @Expose private MessageType messageType;

  @Expose private List<Map<String, Object>> embeds = new ArrayList<>();

  public void setCreationTimestamp(Instant creationTimestamp) {
    this.creationTimestamp = creationTimestamp.toEpochMilli();
  }

  public void addAttachments(List<Message.Attachment> attachments) {
    attachments.forEach(this::addAttachment);
  }

  public void addAttachment(Message.Attachment attachment) {
    this.attachmentUrls.add(attachment.getUrl());
  }

  public void addEmbeds(List<MessageEmbed> embeds) {
    embeds.forEach(this::addEmbed);
  }

  public void addEmbed(MessageEmbed embed) {
    this.embeds.add(embed.toData().toMap());
  }
}
