package lol.j0.ZeroBridge;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import nilloader.api.lib.mini.MiniTransformer;
import nilloader.api.lib.mini.PatchContext;
import nilloader.api.lib.mini.annotation.Patch;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;

import static lol.j0.ZeroBridge.ZeroBridgePremain.log;

@Patch.Class("net.minecraft.network.NetServerHandler")
public class NetServerHandlerTransformer extends MiniTransformer {
    @Patch.Method("handleChat(Lnet/minecraft/network/packet/Packet3Chat;)V")
    public void patchHandleChat(PatchContext ctx) {
        ctx.search(INVOKEVIRTUAL("java/util/logging/Logger", "info", "(Ljava/lang/String;)V")).jumpAfter();

        ctx.add(
                ALOAD(2),
                INVOKESTATIC("lol/j0/ZeroBridge/NetServerHandlerTransformer$Hooks", "postChat", "(Ljava/lang/String;)V")
        );
    }

    @Patch.Method("handleErrorMessage(Ljava/lang/String;[Ljava/lang/Object;)V")
    public void patchHandleErrorMessage(PatchContext ctx) {
        ctx.search(
                INVOKEVIRTUAL("net/minecraft/server/management/ServerConfigurationManager", "sendPacketToAllPlayers", "(Lnet/minecraft/network/packet/Packet;)V")
        ).jumpAfter();

        ctx.add(
                ALOAD(0),
                GETFIELD("net/minecraft/network/NetServerHandler", "playerEntity", "Lnet/minecraft/entity/player/EntityServerPlayer;"),
                GETFIELD("net/minecraft/entity/player/EntityServerPlayer", "username", "Ljava/lang/String;"),
                INVOKESTATIC("lol/j0/ZeroBridge/NetServerHandlerTransformer$Hooks", "postDisconnect", "(Ljava/lang/String;)V")
        );
    }

    @Patch.Method("kickPlayer(Ljava/lang/String;)V")
    public void patchKickUser(PatchContext ctx) {
        ctx.search(
                INVOKEVIRTUAL("net/minecraft/server/management/ServerConfigurationManager", "sendPacketToAllPlayers", "(Lnet/minecraft/network/packet/Packet;)V")
        ).jumpAfter();

        ctx.add(
                ALOAD(0),
                GETFIELD("net/minecraft/network/NetServerHandler", "playerEntity", "Lnet/minecraft/entity/player/EntityServerPlayer;"),
                GETFIELD("net/minecraft/entity/player/EntityServerPlayer", "username", "Ljava/lang/String;"),
                INVOKESTATIC("lol/j0/ZeroBridge/NetServerHandlerTransformer$Hooks", "postDisconnect", "(Ljava/lang/String;)V")
        );
    }

    public static class Hooks {
        public static TextChannel channel;

        public static void postChat(String message) {
            if (!DiscordBridge.ready) return;
            DiscordBridge.post(message);
        }
        public static void postDisconnect(String message) {
            if (!DiscordBridge.ready) return;
            DiscordBridge.post(DiscordBridge.config.strings.player_leave.replace("{USER}", message));
        }

    }

}
