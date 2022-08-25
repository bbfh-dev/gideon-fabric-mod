package xyz.bubblefish.gideon.mixins;

import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static xyz.bubblefish.gideon.GideonClient.playerDealtReach;


@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {
    @Inject(method = "attackEntity", at = @At(value = "TAIL", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;ClientPlayerInteractionManager()Z"))
    public void ClientPlayerInteractionManager(PlayerEntity player, Entity target, CallbackInfo ci) {
        playerDealtReach = Math.max(0f, Math.round((player.distanceTo(target) - target.getWidth() / 2f) * 100f) / 100f);
    }
}