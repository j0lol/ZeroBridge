package lol.j0.ZeroBridge;


import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessage;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.requests.restaction.WebhookAction;
import nilloader.api.NilLogger;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static lol.j0.ZeroBridge.ZeroBridgePremain.log;
import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;

public class DiscordBridge extends ListenerAdapter implements EventListener {

    public static final NilLogger log = NilLogger.get("ZeroBridge Discord");
    public static TextChannel bridgeChannel;
    public static Config config;

    public static boolean ready = false;

    public static void run() {
        config = new Config().loadConfig();
        JDA jda = JDABuilder.createLight(config.token, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_WEBHOOKS, GatewayIntent.GUILD_MODERATION)
                .addEventListeners(new DiscordBridge())
                .build();

        CommandListUpdateAction commands = jda.updateCommands();

        try {
            jda.awaitReady();
        } catch (InterruptedException e) {
            log.error("JDA BROKE :(");
        }

        bridgeChannel = jda.getTextChannelById(config.channel);

        if (config.bot.avatar_url != null) {
            try {
                jda.getSelfUser().getManager().setAvatar(Icon.from(
                        new URL(config.bot.avatar_url).openStream()
                )).complete();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
        if (config.bot.nickname != null) {
            bridgeChannel.getGuild().getMemberById(jda.getSelfUser().getId()).modifyNickname(config.bot.nickname).complete();
        }

        // Simple reply commands
        commands.addCommands(
                Commands.slash("say", "Makes the bot say what you tell it to")
                        .addOption(STRING, "content", "What the bot should say", true) // you can add required options like this too
        );
        commands.addCommands(
                Commands.slash("sayd", "Makes the bot say what you tell it to with delete")
                        .addOption(STRING, "content", "What the bot should say", true) // you can add required options like this too
        );

        commands.queue();

        post(config.strings.server_start);
        ready = true;
    }
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event)
    {
        // Only accept commands from guilds
        if (event.getGuild() == null)
            return;
        switch (event.getName()) {
            case "say":
                say(event, event.getOption("content").getAsString()); // content is required so no null-check here
                break;
            case "sayd":
                sayd(event, event.getOption("content").getAsString()); // content is required so no null-check here
                break;
            default:
                event.reply("I can't handle that command right now :(").setEphemeral(true).queue();

        }
    }
    public static void say(SlashCommandInteractionEvent event, String content)
    {
        event.reply(content).queue(); // This requires no permissions!
    }
    public static void post(String content) {
        Pattern p = Pattern.compile("<(.*)> (.*)");
        Matcher m = p.matcher(content);
        if (config.webhook && m.matches()) {
            Optional<Webhook> webhook = bridgeChannel.retrieveWebhooks().complete().stream().filter(webhook1 -> webhook1.getName().equals("zerobridge")).findFirst();
            if (webhook.equals(Optional.empty())) {
                webhook = Optional.of(bridgeChannel.createWebhook("zerobridge").complete());
            }
            webhook.ifPresent(webhook_n -> {
                WebhookClient client = WebhookClient.withUrl(webhook_n.getUrl());

                // Change appearance of webhook message
                WebhookMessage message = new WebhookMessageBuilder()
                        .setUsername(m.group(1)) // use this username
                        .setAvatarUrl("https://visage.surgeplay.com/bust/256/" + m.group(1)) // use this avatar
                        .setContent(m.group(2))
                        .build();

                client.send(message);
            });
        } else {
            bridgeChannel.sendMessage(content).queue();
        }
    }

    public void sayd(SlashCommandInteractionEvent event, String content)
    {
        event.reply("ok").setEphemeral(true).queue();
        event.getChannel().sendMessage(content).queue();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        if (event.getAuthor().isBot()) return;
        if (!event.getChannel().equals(bridgeChannel)) return;

        sendMessage(event.getAuthor().getEffectiveName(), event.getMessage().getContentDisplay());

    }

    void sendMessage(String username, String message) {
        try {
            Class<?> serverClass = Class.forName("net.minecraft.server.MinecraftServer");
            Method method = serverClass.getMethod("D");
            Object minecraftServer = method.invoke(null);

            Method getConfigurationManager = serverClass.getMethod("ad");
            Object serverConfigurationManager = getConfigurationManager.invoke(minecraftServer);

            // gm net/minecraft/src/ServerConfigurationManager
            Class<?> serverConfigurationManagerClass = Class.forName("gm");

            // a sendPacketToAllPlayers
            // ef net/minecraft/src/Packet
            Method sendPacketToAllPlayers = serverConfigurationManagerClass.getMethod("a", Class.forName("ef"));

            // cu net/minecraft/src/Packet3Chat
            Class<?> packet3Class = Class.forName("cu");
            Constructor<?> Packet3Constructor = packet3Class.getConstructor(String.class, boolean.class);
            Object packet3Chat = Packet3Constructor.newInstance(config.strings.discord_message_template.replace("{USER}", username).replace("{MSG}", message), true);

            sendPacketToAllPlayers.invoke(serverConfigurationManager, packet3Chat);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException |
                 InstantiationException e) {
            log.error(e.toString());
        }
    }
}

