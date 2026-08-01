package yt.graven.gravensupport.utils.messages;

import java.awt.*;
import java.time.Instant;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import org.springframework.stereotype.Component;
import yt.graven.gravensupport.commands.ping.PingComputer;
import yt.graven.gravensupport.commands.ticket.TicketOpeningReason;
import yt.graven.gravensupport.utils.messages.builder.MessageFactory;
import yt.graven.gravensupport.utils.messages.builder.data.TicketMessage;

@Component
public class Embeds {

  public EmbedBuilder error(String message) {
    return new EmbedBuilder().setColor(Color.RED).addField("Erreur :", "> " + message, false);
  }

  public TicketMessage errorMessage(String message) {
    return MessageFactory.create().addEmbeds(error(message));
  }

  public EmbedBuilder success(String message) {
    return new EmbedBuilder().setColor(Color.GREEN).setTitle("Succès").setDescription(message);
  }

  public TicketMessage successMessage(String message) {
    return MessageFactory.create().addEmbeds(success(message));
  }

  public EmbedBuilder ticketAlreadyExists(boolean personal) {
    return error(
        personal
            ? "Vous avez déjà un ticket ouvert avec la modération."
            : "Un ticket est déjà ouvert avec cet utilisateur.");
  }

  public TicketMessage ticketAlreadyExistsMessage(
      GuildMessageChannel ticketChannel, boolean personal) {
    // spotless:off
        return MessageFactory.create()
                .addEmbeds(ticketAlreadyExists(personal))
                .addActionRow(actionRow -> actionRow
                        .addButton(button -> button
                                .setText("Aller au ticket")
                                .setLink(ticketChannel.getJumpUrl())
                        )
                );
        // spotless:on
  }

  public TicketMessage ping(PingComputer manager) {
    EmbedBuilder embed =
        new EmbedBuilder()
            .setTitle(":ping_pong: Pong !")
            .setColor(Color.green)
            .addField(
                "↔️ Ping du Gateway :", "\n**`%s`** ms".formatted(manager.getGatewayPing()), false)
            .addField(
                "➡️ Ping de l'API :", "\n**`%s`** ms".formatted(manager.getRestPing()), false);

    // spotless:off
        return MessageFactory.create()
                .addEmbeds(embed)
                .addActionRow(actionRow -> actionRow
                        .addButton("refresh-ping", button -> button
                                .setStyle(ButtonStyle.PRIMARY)
                                .setText("Actualiser")
                                .setEmoji(Emoji.fromUnicode("🔁"))
                        )
                );
        // spotless:on
  }

  public EmbedBuilder noTicketAttached() {
    return error("Impossible de trouver un ticket rattaché à ce salon");
  }

  public TicketMessage noTicketAttachedMessage() {
    return MessageFactory.create().addEmbeds(noTicketAttached());
  }

  public EmbedBuilder proposeOpening(String sentEmote) {
    return new EmbedBuilder()
        .setAuthor("Message automatique")
        .setTitle("Ticket en cours d'ouverture !")
        .setDescription(
            """
                                Votre demande d'ouverture de ticket a bien été prise en compte.
                                Veuillez cependant confirmer que vous avez pris connaissance des règles de ceux-cis.
                                """)
        .addField(
            "✉️ Règles des tickets :",
            """
                                - Les messages que vous envoyez ne peuvent ni être édités, ni être supprimés.
                                - Tous les messages envoyés sont enregistrés.
                                - Les règles du discord `Graven - Développement` sont également applicables dans les tickets.
                                - Les tickets ouverts sans justification seront sanctionnés.
                                - Les tickets sont destinés à la modération. Les demandes d'aides sont susceptibles d'être sanctionnées.
                                """,
            false)
        .addField(
            "❔ Utilisation :",
            String.format(
                """
                                        - Pour transmettre un message, vous avez juste à envoyer un message en privé avec ce bot.
                                        - Les réponses de la modération se feront par le biais de ces message privés.
                                        - Si votre message est transmis, la réaction %s sera ajoutée à vos messages
                                        """,
                sentEmote),
            false)
        .addField(
            "✨ Terminer l'ouverture du ticket",
            """
                                Afin de terminer l'ouverture du ticket, merci de sélectionner ci-dessous la raison de l'ouverture de votre ticket.
                                Vous serez ensuite recontacté dans les plus brefs délais.
                                """,
            false)
        .setColor(Color.GREEN);
  }

  public EmbedBuilder forceOpening(String sentEmote, TicketOpeningReason reason) {
    String reasonAsString =
        switch (reason) {
          case TicketOpeningReason.Empty r -> "Aucune raison n'a été spécifiée";
          case TicketOpeningReason.Simple r -> r.reason();
          case TicketOpeningReason.UserReport r ->
              "Signalement utilisateur à cause de : %s".formatted(r.reportReason());
        };
    return new EmbedBuilder()
        .setAuthor("Message automatique")
        .setTitle("Ticket ouvert !")
        .setDescription(
            """
                                La modération a ouvert un ticket vous impliquant.
                                Vous pouvez désormais discuter avec le staff en message privé avec le bot
                                """)
        .addField(
            "✉️ Règles des tickets :",
            """
                                - Les messages que vous envoyez ne peuvent ni être édités, ni être supprimés.
                                - Tous les messages envoyés sont enregistrés.
                                - Les règles du discord `Graven - Développement` sont également applicables dans les tickets.
                                """,
            false)
        .addField(
            "❔ Utilisation :",
            String.format(
                """
                                        - Pour transmettre un message, vous avez juste à envoyer un message en privé avec ce bot.
                                        - Les réponses de la modération se feront par le biais de ces message privés.
                                        - Si votre message est transmis, la réaction %s sera ajoutée à vos messages
                                        """,
                sentEmote),
            false)
        .addField(
            "⚠️ Avertissement :",
            """
                                Ce ticket a été ouvert par la modération pour la raison suivante :
                                ```
                                %s
                                ```
                                """
                .formatted(reasonAsString),
            false)
        .setColor(Color.ORANGE);
  }

  public EmbedBuilder ticketOpening(
      boolean forced, User by, User from, TextChannel channel, String reason) {
    return new EmbedBuilder()
        .setTitle("Ticket ouvert")
        .setColor(forced ? Color.CYAN : Color.GREEN)
        .setTimestamp(Instant.now())
        .setThumbnail(from.getAvatarUrl())
        .addField(
            "ℹ️ Détails :",
            """
                                > **Identifiant de l'utilisateur**
                                %s

                                > **Nom de l'utilisateur**
                                %s (`@%s`)

                                :hash: **Salon**
                                %s (`#%s`)

                                📝 **Raison**
                                `%s`

                                %s
                                """
                .formatted(
                    from.getId(),
                    from.getAsMention(),
                    from.getAsTag(),
                    channel.getAsMention(),
                    channel.getName(),
                    reason,
                    forced
                        ? String.format(
                            """
                                                 🛂 **Ouvert par**
                                                 %s (`@%s`)

                                                """,
                            by.getAsMention(), by.getAsTag())
                        : ""),
            false);
  }

  public EmbedBuilder ticketClosing(User from, String jumpUrl) {
    return new EmbedBuilder()
        .setTitle("Ticket fermé")
        .setColor(Color.RED)
        .setTimestamp(Instant.now())
        .setThumbnail(from.getAvatarUrl())
        .addField(
            "ℹ️ Détails :",
            String.format(
                """
                                        > **Identifiant de l'utilisateur**
                                        %s

                                        > **Nom de l'utilisateur**
                                        %s (`@%s`)

                                        :spiral_note_pad: **Rapport**
                                        [Lien du rapport](%s)
                                        """,
                from.getId(), from.getAsMention(), from.getAsTag(), jumpUrl),
            false);
  }
}
