package lol.j0.ZeroBridge;

import nilloader.api.lib.mini.MiniTransformer;
import nilloader.api.lib.mini.PatchContext;
import nilloader.api.lib.mini.annotation.Patch;


@Patch.Class("net.minecraft.server.MinecraftServer")
public class MinecraftServerTransformer extends MiniTransformer {

    @Patch.Method("run()V")
    public void patchStartServerThread(PatchContext ctx) {
        ctx.jumpToStart();

        ctx.add(
                INVOKESTATIC("lol/j0/ZeroBridge/MinecraftServerTransformer$Hooks", "startBridgeThread", "()V")
        );
    }

    @Patch.Method("stopServer()V")
    public void patchStopServer(PatchContext ctx) {
        ctx.search(
                INVOKEVIRTUAL( "net/minecraft/network/NetworkListenThread","stopListening","()V")
        ).jumpAfter();
        ctx.add(
                INVOKESTATIC("lol/j0/ZeroBridge/MinecraftServerTransformer$Hooks", "postServerStop", "()V")
        );
    }

    public static class Hooks {

        public static void startBridgeThread() {
            ZeroBridgePremain.log.info("Starting Bridge");

            Thread t = new Thread(DiscordBridge::run);
            t.start();
        }

        public static void postServerStop() {
            ZeroBridgePremain.log.info("Stopping");
            if (!DiscordBridge.ready) return;
            DiscordBridge.post(DiscordBridge.config.strings.server_stop);
        }
    }
}
