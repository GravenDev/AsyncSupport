package yt.graven.gravensupport.commands.ticket.interactions;

import java.awt.*;
import java.time.Instant;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.utils.MiscUtil;
import org.springframework.stereotype.Component;
import yt.graven.gravensupport.commands.ticket.Ticket;
import yt.graven.gravensupport.commands.ticket.TicketManager;
import yt.graven.gravensupport.utils.interactions.InteractionAction;
import yt.graven.gravensupport.utils.messages.Embeds;

@Component
@RequiredArgsConstructor
public class ConfirmMessageHandler implements InteractionAction<ButtonInteractionEvent> {

  private final TicketManager ticketManager;
  private final Embeds embeds;

  @Override
  public void run(ButtonInteractionEvent event) {
    Message embedMessage = event.getMessage();

    MessageEmbed baseEmbed = embedMessage.getEmbeds().get(0);
    String originalMessageId = baseEmbed.getFields().get(0).getValue();
    originalMessageId =
        originalMessageId
            .replace(originalMessageId.replaceAll("\\[[0-9]+]", ""), "")
            .replaceAll("[\\[\\]]", "");

    Message referingMessage =
        event
            .getChannel()
            .getHistoryAround(originalMessageId, 50)
            .complete()
            .getMessageById(originalMessageId);

    if (referingMessage == null) {
      event
          .deferReply(true)
          .addEmbeds(
              new EmbedBuilder()
                  .setColor(Color.RED)
                  .setTitle("Erreur")
                  .setDescription("Impossible de trouver le message original !")
                  .build())
          .queue();
      return;
    }

    Optional<Ticket> ticket =
        ticketManager.get(MiscUtil.parseLong(((TextChannel) event.getChannel()).getTopic()));
    if (ticket.isEmpty()) {
      event
          .deferReply(true)
          .addEmbeds(
              new EmbedBuilder()
                  .setColor(Color.RED)
                  .setTitle("Erreur")
                  .setDescription("Impossible de trouver le ticket associé à ce salon !")
                  .setFooter("")
                  .build())
          .queue();
      return;
    }

    boolean attachments = !referingMessage.getAttachments().isEmpty();

    InteractionHook interaction = null;
    if (attachments) {
      interaction = event.deferReply().complete();
    }

    InteractionHook fInteraction = interaction;
    ticket
        .get()
        .confirmSendToUser(referingMessage)
        .thenAccept(
            (message) -> {
              EmbedBuilder embed =
                  new EmbedBuilder(baseEmbed)
                      .setTitle("Message transmis :")
                      .setDescription(message.getContentRaw())
                      .setFooter("")
                      .setTimestamp(Instant.now())
                      .setFooter(
                          "Envoyé par " + event.getUser().getAsTag(),
                          event.getUser().getAvatarUrl())
                      .setColor(Color.GREEN);

              embed
                  .getFields()
                  .add(
                      new MessageEmbed.Field(
                          "🔗 Identifiant du message envoyé", message.getId(), true));

              updatePinnedMessages(referingMessage);

              if (attachments) {
                fInteraction.deleteOriginal().queue();
                embedMessage
                    .editMessageEmbeds(embed.build())
                    .setActionRow(
                        Button.of(
                            ButtonStyle.SUCCESS,
                            "edit-message",
                            "Modifier le message",
                            Emoji.fromUnicode("✏️")),
                        Button.of(
                            ButtonStyle.DANGER,
                            "delete-message",
                            "Supprimer le message",
                            Emoji.fromUnicode("🗑️")))
                    .queue();
              } else {
                event
                    .deferEdit()
                    .setEmbeds(embed.build())
                    .setActionRow(
                        Button.of(
                            ButtonStyle.SUCCESS,
                            "edit-message",
                            "Modifier le message",
                            Emoji.fromUnicode("✏️")),
                        Button.of(
                            ButtonStyle.DANGER,
                            "delete-message",
                            "Supprimer le message",
                            Emoji.fromUnicode("🗑️")))
                    .queue();
              }
            })
        .exceptionally(
            error -> {
              embeds.errorMessage(error.getMessage()).editReply(fInteraction).queue();
              return null;
            });
  }

  private void updatePinnedMessages(Message message) {
    ErrorHandler handleMaxPinError =
        new ErrorHandler()
            .handle(
                ErrorResponse.MAX_MESSAGE_PINS,
                (exception) -> {
                  MessageChannelUnion channel = message.getChannel();
                  channel
                      .retrievePinnedMessages()
                      .map(pinnedMessages -> pinnedMessages.get(pinnedMessages.size() - 1))
                      .queue(
                          oldestPinnedMessage ->
                              channel
                                  .unpinMessageById(oldestPinnedMessage.getId())
                                  .queue(nothing -> message.pin().queue()));
                });

    message.pin().queue(null, handleMaxPinError);
  }
}
