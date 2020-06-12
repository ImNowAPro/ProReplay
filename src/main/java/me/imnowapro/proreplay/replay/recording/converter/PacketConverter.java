package me.imnowapro.proreplay.replay.recording.converter;

import com.comphenix.protocol.events.PacketContainer;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.logging.Level;
import me.imnowapro.proreplay.ProReplay;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public interface PacketConverter {

  PacketContainer createPlayerListItemPacket(Player player);

  PacketContainer createPlayerSpawnPacket(Player player);

  PacketContainer convertPositionPacket(Player player, PacketContainer oldPacket);

  PacketContainer convertPositionLookPacket(Player player, PacketContainer oldPacket);

  PacketContainer convertLookPacket(Player player, PacketContainer oldPacket);

  static Optional<PacketConverter> getConverter() {
    try {
      String version = Bukkit.getServer().getClass().getPackage().getName()
          .replace(".", ",").split(",")[3].substring(1);
      return Optional.of((PacketConverter) Class
          .forName(PacketConverter.class.getCanonicalName() + "_" + version)
          .getConstructors()[0].newInstance());
    } catch (InstantiationException | IllegalAccessException
        | ClassNotFoundException | InvocationTargetException e) {
      ProReplay.getInstance().getLogger().log(Level.WARNING, "Failed to get PacketConverter.", e);
    }
    return Optional.empty();
  }
}
