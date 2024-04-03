package lol.j0.ZeroBridge;

import nilloader.api.lib.mini.MiniTransformer;
import nilloader.api.lib.mini.PatchContext;
import nilloader.api.lib.mini.annotation.Patch;

@Patch.Class("net.minecraft.entity.player.EntityServerPlayer")
public class EntityServerPlayerTransformer extends MiniTransformer {

	// Mini is the transformer framework bundled with NilLoader. It's pretty low level, but tries
	// to file off a lot of the sharp edges from doing ASM patches. This is a really minimal example
	// of a patch to just print out something when the Minecraft class static-inits. This is chosen
	// as the example as it works on multiple versions.

	// NilLoader will automatically reobfuscate references to classes, fields, and methods in your
	// patches based on your currently selected mapping. This patch carefully avoids obfuscated
	// things to provide a semi-version-agnostic example.

	@Patch.Method("onDeath(Lnet/minecraft/util/DamageSource;)V")
	public void patchOnDeath(PatchContext ctx) {
		ctx.jumpToStart();

		ctx.add(
				ALOAD (1),
				ALOAD (0),
				INVOKEVIRTUAL( "net/minecraft/util/DamageSource","getDeathMessage" ,"(Lnet/minecraft/entity/player/EntityPlayer;)Ljava/lang/String;"),
				INVOKESTATIC("lol/j0/ZeroBridge/EntityServerPlayerTransformer$Hooks", "postDeath", "(Ljava/lang/String;)V")
		);
	}

	public static class Hooks {

		public static void postDeath(String death) {
			if (!DiscordBridge.ready) return;
			DiscordBridge.post(death);
		}

	}

}
