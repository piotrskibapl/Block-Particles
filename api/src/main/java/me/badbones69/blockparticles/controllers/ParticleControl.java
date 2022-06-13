package me.badbones69.blockparticles.controllers;

import me.badbones69.blockparticles.api.enums.Particles;
import org.bukkit.Location;

import java.util.HashMap;

public interface ParticleControl {
    
    HashMap<String, Integer> getLocations();
    
    void playVolcano(Location location, String id, int cooldown);
    
    void playBigFlame(Location location, String id, int cooldown);
    
    void playFlame(Location location, String id, int cooldown);
    
    void playDoubleSpiral(Location location, String id, Particles particle, int amount, int cooldown);
    
    void playSpiral(Location location, String id, Particles particle, int amount, int cooldown);
    
    void playCrit(Location location, String id, int cooldown);
    
    void playBigCrit(Location location, String id, int cooldown);
    
    void playStorm(Location location, String id, int cooldown);
    
    void playFog(Location location, String id, int cooldown);
    
    void playEnchant(Location location, String id, int cooldown);
    
    void playChains(Location location, String id, int cooldown);
    
    void playFireStorm(Location location, String id, int cooldown);
    
    void playSnow(Location location, String id, int cooldown);
    
    void playSpew(Location location, String id, int cooldown);
    
    void playPotion(Location location, String id, int cooldown);
    
    void playMusic(Location location, String id, int cooldown);
    
    void playMagic(Location location, String id, int cooldown);
    
    void playSnowStorm(Location location, String id, int cooldown);
    
    void playFireSpew(Location location, String id, int cooldown);
    
    void playFootPrint(Location location, String id, int cooldown);
    
    void playHappyVillager(Location location, String id, int cooldown);
    
    void playAngryVillager(Location location, String id, int cooldown);
    
    void playMobSpawner(Location location, String id, int cooldown);
    
    void startWater(Location location, String id, int cooldown);
    
    void playEnderSignal(Location location, String id, int cooldown);
    
    void playRainbow(Location location, String id, int cooldown);
    
    void playSnowBlast(Location location, String id, int cooldown);
    
    void playHalo(Location location, String id, int cooldown);
    
    void playSantaHat(Location location, String id, int cooldown);
    
    void playSoulWell(Location location, String id, int cooldown);
    
    void playBigSoulWell(Location location, String id, int cooldown);
    
    void playFlameWheel(Location location, String id, int cooldown);
    
    void playWitchTornado(Location location, String id, int cooldown);
    
    void playLoveTornado(Location location, String id, int cooldown);
    
    void playBigLoveWell(Location location, String id, int cooldown);
    
    void playLoveWell(Location location, String id, int cooldown);
    
}