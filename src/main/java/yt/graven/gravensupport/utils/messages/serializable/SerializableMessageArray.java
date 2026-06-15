package yt.graven.gravensupport.utils.messages.serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.utils.FileUpload;

public class SerializableMessageArray {

  @Expose private final int version = 2;

  @Expose(deserialize = false, serialize = false)
  public User fromUser;

  @Expose(deserialize = false, serialize = false)
  private String attachmentsChannelId;

  @Expose
  @SerializedName("to")
  private SerializableMessageAuthor from;

  @Expose private List<SerializableMessage> messages = new ArrayList<>();

  public SerializableMessageArray(String attachmentsChannelId, User from) {
    this.attachmentsChannelId = attachmentsChannelId;
    this.fromUser = from;

    this.from = new SerializableMessageAuthor();
    this.from.setId(from.getIdLong());
    this.from.setName(from.getAsTag());
    this.from.setAvatarUrl(from.getAvatarUrl());
  }

  public void addMessage(Message message) {
    SerializableMessage serializableMessage = serializeMessage(message);
    serializableMessage.addAttachments(sendAttachments(message));
    serializableMessage.addEmbeds(message.getEmbeds());

    messages.add(serializableMessage);
  }

  private List<Message.Attachment> sendAttachments(Message message) {
    if (message.getAttachments().isEmpty()) {
      return List.of();
    }

    List<Message.Attachment> attachmentAccumulator = new ArrayList<>();

    TextChannel attachmentsChannel =
        Optional.ofNullable(message.getJDA().getTextChannelById(attachmentsChannelId))
            .orElseThrow();

    for (Message.Attachment attachment : message.getAttachments()) {
      CompletableFuture<InputStream> attachmentStream = attachment.getProxy().download();
      Message attachmentMessage =
          attachmentsChannel
              .sendMessage("Attachment of @" + message.getAuthor().getAsTag())
              .addFiles(FileUpload.fromData(attachmentStream.join(), attachment.getFileName()))
              .complete();

      attachmentAccumulator.addAll(attachmentMessage.getAttachments());
    }

    return Collections.unmodifiableList(attachmentAccumulator);
  }

  private SerializableMessage serializeMessage(Message message) {
    SerializableMessage serializableMessage = new SerializableMessage();

    SerializableMessageAuthor serializableAuthor = new SerializableMessageAuthor();
    MessageType messageType = MessageType.fromMessage(message);
    User author = messageType == MessageType.TARGET ? fromUser : message.getAuthor();

    serializableAuthor.setAvatarUrl(author.getAvatarUrl());
    serializableAuthor.setName(author.getAsTag());
    serializableAuthor.setId(author.getIdLong());

    serializableMessage.setMessageType(messageType);

    serializableMessage.setAuthor(serializableAuthor);
    serializableMessage.setContent(message.getContentRaw());
    serializableMessage.setEdited(message.isEdited());
    serializableMessage.setCreationTimestamp(message.getTimeCreated().toInstant());
    return serializableMessage;
  }
}
