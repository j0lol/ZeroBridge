package lol.j0.ZeroBridge;

import nilloader.api.lib.mini.MiniTransformer;
import nilloader.api.lib.mini.PatchContext;
import nilloader.api.lib.mini.annotation.Patch;


@Patch.Class("net.minecraft.server.management.ServerConfigurationManager")
public class ServerConfigurationManagerTransformer extends MiniTransformer {

    
    @Patch.Method("initializeConnectionToPlayer(Lnet/minecraft/network/INetworkManager;Lnet/minecraft/entity/player/EntityServerPlayer;)V")
    public void patchStartServerThread(PatchContext ctx) {
        ctx.search(
                INVOKEVIRTUAL("net/minecraft/server/management/ServerConfigurationManager", "sendPacketToAllPlayers","(Lnet/minecraft/network/packet/Packet;)V\n")
        ).jumpAfter();

        ctx.add(
                ALOAD(2),
                GETFIELD("net/minecraft/entity/player/EntityServerPlayer", "username", "Ljava/lang/String;"),
                INVOKESTATIC("lol/j0/ZeroBridge/ServerConfigurationManagerTransformer$Hooks", "postJoin", "(Ljava/lang/String;)V")
        );
    }
    public static class Hooks {

        public static void postJoin(String username) {
            if (!DiscordBridge.ready) return;
            DiscordBridge.post(DiscordBridge.config.strings.player_join.replace("{USER}", username));
        }

    }
}
