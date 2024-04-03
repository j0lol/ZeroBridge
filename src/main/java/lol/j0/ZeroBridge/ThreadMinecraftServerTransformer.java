package lol.j0.ZeroBridge;

import nilloader.api.lib.mini.MiniTransformer;
import nilloader.api.lib.mini.PatchContext;
import nilloader.api.lib.mini.annotation.Patch;

// fy net/minecraft/server/ThreadMinecraftServer

@Patch.Class("net.minecraft.server.ThreadMinecraftServer")
public class ThreadMinecraftServerTransformer extends MiniTransformer {
    //c ()Z
    @Patch.Method("run()V")
    public void patchStartServerThread(PatchContext ctx) {
        ctx.jumpToStart();

        ctx.add(
                // This is the recommended way to do ASM hooks in NilLoader - invoke a helper defined
                // in an inner class for your transformer.
                INVOKESTATIC("lol/j0/ZeroBridge/MinecraftServerTransformer$Hooks", "startBridgeThread", "()V")
        );

        // And, for the sake of illustration, let's also inject bytecode to invoke an entrypoint.
        // Mini provides a convenience method for this (which you must use; the bytecode it
        // generates calls a method that is not part of the API and may change.)
        ctx.addFireEntrypoint("zerobridge-init");

    }
    public static class Hooks {

        public static void startBridgeThread() {
            ZeroBridgePremain.log.info("Starting Bridge");

            Thread t = new Thread(DiscordBridge::run);
            t.start();
        }

    }
}
